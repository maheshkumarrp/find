/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.idol.export;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.transport.AciParameter;
import com.hp.autonomy.frontend.find.core.export.ExportFormat;
import com.hp.autonomy.frontend.find.core.export.ExportStrategy;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.idol.configuration.AciServiceRetriever;
import com.hp.autonomy.searchcomponents.idol.search.HavenSearchAciParameterHandler;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRequest;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRequestBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IdolExportServiceTest {
    @Mock
    private ExportStrategy exportStrategy;
    @Mock
    private HavenSearchAciParameterHandler parameterHandler;
    @Mock
    private AciServiceRetriever aciServiceRetriever;
    @Mock
    private AciService aciService;
    @Mock
    private OutputStream outputStream;
    @Mock
    private IdolQueryRequest queryRequest;
    @Mock
    private IdolQueryRequestBuilder idolQueryRequestBuilder;

    private IdolExportService idolExportService;

    @Before
    public void setUp() {
        when(exportStrategy.getExportFormat()).thenReturn(ExportFormat.CSV);
        when(aciServiceRetriever.getAciService(any(QueryRequest.QueryType.class))).thenReturn(aciService);
        when(queryRequest.toBuilder()).thenReturn(idolQueryRequestBuilder);
        when(idolQueryRequestBuilder.start(anyInt())).thenReturn(idolQueryRequestBuilder);
        when(idolQueryRequestBuilder.maxResults(anyInt())).thenReturn(idolQueryRequestBuilder);
        when(idolQueryRequestBuilder.build()).thenReturn(queryRequest);

        idolExportService = new IdolExportService(parameterHandler, aciServiceRetriever, new ExportStrategy[]{exportStrategy});
    }

    @Test
    public void export() throws IOException {
        idolExportService.export(outputStream, queryRequest, ExportFormat.CSV, Collections.emptyList(), 10l);
        verify(aciService).executeAction(anySetOf(AciParameter.class), any(Processor.class));
    }
}
