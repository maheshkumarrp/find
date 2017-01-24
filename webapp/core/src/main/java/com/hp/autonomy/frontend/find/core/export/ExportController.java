/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.core.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.autonomy.frontend.find.core.web.ControllerUtils;
import com.hp.autonomy.frontend.find.core.web.ErrorModelAndViewInfo;
import com.hp.autonomy.frontend.find.core.web.RequestMapper;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import java.awt.*;
import java.awt.geom.Path2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFChart;
import org.apache.poi.xslf.usermodel.XSLFFreeformShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDoughnutChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumVal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrVal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientStop;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientStopList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping(ExportController.EXPORT_PATH)
public abstract class ExportController<R extends QueryRequest<?>, E extends Exception> {
    static final String EXPORT_PATH = "/api/bi/export";
    static final String CSV_PATH = "/csv";
    static final String PPT_TOPICMAP_PATH = "/ppt/topicmap";
    static final String PPT_SUNBURST_PATH = "/ppt/sunburst";
    static final String SELECTED_EXPORT_FIELDS_PARAM = "selectedFieldIds";
    static final String QUERY_REQUEST_PARAM = "queryRequest";
    private static final String EXPORT_FILE_NAME = "query-results";

    private final ExportService<R, E> exportService;
    private final RequestMapper<R> requestMapper;
    private final ControllerUtils controllerUtils;

    protected ExportController(final ExportService<R, E> exportService, final RequestMapper<R> requestMapper, final ControllerUtils controllerUtils) {
        this.exportService = exportService;
        this.requestMapper = requestMapper;
        this.controllerUtils = controllerUtils;
    }

    @RequestMapping(value = CSV_PATH, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<byte[]> exportToCsv(
            @RequestParam(QUERY_REQUEST_PARAM) final String queryRequestJSON,
            // required = false to prevent Spring errors if the user asks for a CSV with no fields marked for export.
            // The UI should not allow the User to send a request for a CSV with nothing in it.
            @RequestParam(value = SELECTED_EXPORT_FIELDS_PARAM, required = false) final Collection<String> selectedFieldNames
    ) throws IOException, E {
        return export(queryRequestJSON, ExportFormat.CSV, selectedFieldNames);
    }

    private ResponseEntity<byte[]> export(final String queryRequestJSON, final ExportFormat exportFormat, final Collection<String> selectedFieldNames) throws IOException, E {
        final R queryRequest = requestMapper.parseQueryRequest(queryRequestJSON);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        exportService.export(outputStream, queryRequest, exportFormat, selectedFieldNames);
        final byte[] output = outputStream.toByteArray();

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(exportFormat.getMimeType()));
        final String fileName = EXPORT_FILE_NAME + FilenameUtils.EXTENSION_SEPARATOR + exportFormat.getExtension();
        headers.setContentDispositionFormData(fileName, fileName);

        return new ResponseEntity<>(output, headers, HttpStatus.OK);
    }

    //TODO improve to inform what went wrong with export, rather than generic just error 500.
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleException(
            final Exception e,
            final HttpServletRequest request,
            final ServletResponse response
    ) {
        response.reset();

        return controllerUtils.buildErrorModelAndView(new ErrorModelAndViewInfo.Builder()
                .setRequest(request)
                .setMainMessageCode("error.internalServerErrorMain")
                .setSubMessageCode("error.internalServerErrorSub")
                .setSubMessageArguments(null)
                .setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setContactSupport(true)
                .setException(e)
                .build());
    }

    @RequestMapping(value = PPT_TOPICMAP_PATH)
    public HttpEntity<byte[]> topicmap(
            @RequestParam("paths") final String pathStr
    ) throws IOException {
        final Path[] paths = new ObjectMapper().readValue(pathStr, Path[].class);

        final XMLSlideShow ppt = new XMLSlideShow();
        final XSLFSlide sl = ppt.createSlide();

        for(final Path reqPath : paths) {
            final XSLFFreeformShape shape = sl.createFreeform();
            final Path2D.Double path = new Path2D.Double();

            boolean first = true;

            for(double[] point : reqPath.getPoints()) {
                final double x = point[0] * 720;
                final double y = point[1] * 540;
                if(first) {
                    path.moveTo(x, y);
                    first = false;
                }
                else {
                    path.lineTo(x, y);
                }
            }
            path.closePath();

            shape.setPath(path);
            shape.setStrokeStyle(2);
            shape.setLineColor(Color.GRAY);
            shape.setHorizontalCentered(true);
            shape.setVerticalAlignment(VerticalAlignment.MIDDLE);
            shape.setTextAutofit(TextShape.TextAutofit.NORMAL);

            final XSLFTextParagraph text = shape.addNewTextParagraph();
            final XSLFTextRun textRun = text.addNewTextRun();
            textRun.setText(reqPath.name);
            textRun.setFontColor(Color.WHITE);
            textRun.setBold(true);

            final int opacity = (int) (100000 * reqPath.getOpacity());
            final Color c1 = Color.decode(reqPath.getColor());
            final Color c2 = Color.decode(reqPath.getColor2());

            final CTShape cs = (CTShape) shape.getXmlObject();
            final CTGradientFillProperties gFill = cs.getSpPr().addNewGradFill();
            gFill.addNewLin().setAng(3300000);
            final CTGradientStopList list = gFill.addNewGsLst();

            final CTGradientStop stop1 = list.addNewGs();
            stop1.setPos(0);
            final CTSRgbColor color1 = stop1.addNewSrgbClr();
            color1.setVal(new byte[]{(byte) c1.getRed(), (byte) c1.getGreen(), (byte) c1.getBlue()});
            color1.addNewAlpha().setVal(opacity);

            final CTGradientStop stop2 = list.addNewGs();
            stop2.setPos(100000);
            final CTSRgbColor color2 = stop2.addNewSrgbClr();
            color2.setVal(new byte[]{(byte) c2.getRed(), (byte) c2.getGreen(), (byte) c2.getBlue()});
            color2.addNewAlpha().setVal(opacity);
        }

        return writePPT(ppt, "topicmap.pptx");
    }

    private HttpEntity<byte[]> writePPT(final XMLSlideShow ppt, final String filename) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ppt.write(baos);
        ppt.close();

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.presentationml.presentation"));
        headers.set("Content-Disposition", "inline; filename=" + filename);
        return new HttpEntity<>(baos.toByteArray(), headers);
    }

    @Value(value = "classpath:/templates/sunburst.pptx")
    private Resource sunburstTemplate;

    @RequestMapping(value = PPT_SUNBURST_PATH)
    public HttpEntity<byte[]> sunburst(
            @RequestParam("categories") final String[] categories,
            @RequestParam("values") final double[] values,
            @RequestParam("title") final String title
    ) throws IOException {
        if (values.length != categories.length) {
            throw new IllegalArgumentException("Number of values should match the number of categories");
        }

        try(final InputStream template = sunburstTemplate.getInputStream()) {
            final XMLSlideShow ppt = new XMLSlideShow(template);

            final XSLFSlide slide = ppt.getSlides().get(0);

            XSLFChart chart = null;
            for(POIXMLDocumentPart part : slide.getRelations()) {
                if(part instanceof XSLFChart) {
                    chart = (XSLFChart) part;
                    break;
                }
            }

            if(chart == null) throw new IllegalStateException("Chart required in template");

            final XSSFWorkbook workbook = new XSSFWorkbook();
            final XSSFSheet sheet = workbook.createSheet();

            final CTChart ctChart = chart.getCTChart();
            final CTPlotArea plotArea = ctChart.getPlotArea();

            final CTDoughnutChart donutChart = plotArea.getDoughnutChartArray(0);

            final CTPieSer series = donutChart.getSerArray(0);

            final CTStrRef strRef = series.getTx().getStrRef();
            strRef.getStrCache().getPtArray(0).setV(title);
            sheet.createRow(0).createCell(1).setCellValue(title);
            strRef.setF(new CellReference(sheet.getSheetName(), 0, 1, true, true).formatAsString());

            final CTStrRef categoryRef = series.getCat().getStrRef();
            final CTStrData categoryData = categoryRef.getStrCache();
            final CTNumRef numRef = series.getVal().getNumRef();
            final CTNumData numericData = numRef.getNumCache();

            categoryData.setPtArray(null);
            numericData.setPtArray(null);

            for(int idx = 0; idx < values.length; ++idx) {
                final CTStrVal categoryPoint = categoryData.addNewPt();
                categoryPoint.setIdx(idx);
                categoryPoint.setV(categories[idx]);

                final CTNumVal numericPoint = numericData.addNewPt();
                numericPoint.setIdx(idx);
                numericPoint.setV(Double.toString(values[idx]));

                XSSFRow row = sheet.createRow(idx + 1);
                row.createCell(0).setCellValue(categories[idx]);
                row.createCell(1).setCellValue(values[idx]);
            }
            categoryData.getPtCount().setVal(categories.length);
            numericData.getPtCount().setVal(values.length);

            categoryRef.setF(new CellRangeAddress(1, values.length, 0, 0).formatAsString(sheet.getSheetName(), true));
            numRef.setF(new CellRangeAddress(1, values.length, 1, 1).formatAsString(sheet.getSheetName(), true));

            for(final POIXMLDocumentPart part : chart.getRelations()) {
                final PackagePart pkg = part.getPackagePart();
                if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(pkg.getContentType())) {
                    // We have to rewrite the chart data; OpenOffice doesn't use it but Powerpoint does when you click 'Edit Data'
                    try(final OutputStream xlsOut = pkg.getOutputStream()) {
                        workbook.write(xlsOut);
                    }
                    break;
                }
            }

            return writePPT(ppt, "sunburst.pptx");
        }
    }
}
