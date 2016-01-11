/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.idol.search;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.aci.content.database.Databases;
import com.hp.autonomy.aci.content.identifier.reference.Reference;
import com.hp.autonomy.aci.content.printfields.PrintFields;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.find.core.search.DocumentsService;
import com.hp.autonomy.frontend.find.core.search.FindDocument;
import com.hp.autonomy.frontend.find.core.search.FindQueryParams;
import com.hp.autonomy.frontend.find.idol.configuration.IdolFindConfig;
import com.hp.autonomy.frontend.find.idol.configuration.OptionalAciService;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.types.idol.DocContent;
import com.hp.autonomy.types.idol.Hit;
import com.hp.autonomy.types.idol.QueryResponseData;
import com.hp.autonomy.types.idol.SuggestResponseData;
import com.hp.autonomy.types.requests.Documents;
import com.hp.autonomy.types.requests.idol.actions.query.QueryActions;
import com.hp.autonomy.types.requests.idol.actions.query.params.CombineParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.HighlightParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import com.hp.autonomy.types.requests.idol.actions.query.params.SuggestParams;
import com.hp.autonomy.types.requests.idol.actions.query.params.SummaryParam;
import com.hp.autonomy.types.requests.qms.QmsActionParams;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class IdolDocumentService implements DocumentsService<String, FindDocument, AciErrorException> {
    static final String MISSING_RULE_ERROR = "missing rule";
    static final String INVALID_RULE_ERROR = "invalid rule";
    private static final String IDOL_DATE_PARAMETER_FORMAT = "HH:mm:ss dd/MM/yyyy";
    private static final int MAX_SIMILAR_DOCUMENTS = 3;

    private final ConfigService<IdolFindConfig> configService;
    private final AciService contentAciService;
    private final OptionalAciService qmsAciService;
    private final Processor<QueryResponseData> queryResponseProcessor;
    private final Processor<SuggestResponseData> suggestResponseProcessor;

    @Autowired
    public IdolDocumentService(final ConfigService<IdolFindConfig> configService, final AciService contentAciService, final OptionalAciService qmsAciService, final AciResponseJaxbProcessorFactory aciResponseProcessorFactory) {
        this.configService = configService;
        this.contentAciService = contentAciService;
        this.qmsAciService = qmsAciService;

        queryResponseProcessor = aciResponseProcessorFactory.createAciResponseProcessor(QueryResponseData.class);
        suggestResponseProcessor = aciResponseProcessorFactory.createAciResponseProcessor(SuggestResponseData.class);
    }

    @Override
    public Documents<FindDocument> queryTextIndex(final FindQueryParams<String> findQueryParams) throws AciErrorException {
        return queryTextIndex(qmsAciService.isEnabled() ? qmsAciService : contentAciService, findQueryParams, false);
    }

    @Override
    public Documents<FindDocument> queryTextIndexForPromotions(final FindQueryParams<String> findQueryParams) throws AciErrorException {
        return qmsAciService.isEnabled() ? queryTextIndex(qmsAciService, findQueryParams, true) : new Documents<>(Collections.<FindDocument>emptyList(), 0, null);
    }

    private Documents<FindDocument> queryTextIndex(final AciService aciService, final FindQueryParams<String> findQueryParams, final boolean promotions) {
        final AciParameters aciParameters = new AciParameters(QueryActions.Query.name());
        aciParameters.add(QueryParams.Text.name(), findQueryParams.getText());
        aciParameters.add(QueryParams.MaxResults.name(), findQueryParams.getMaxResults());
        aciParameters.add(QueryParams.Summary.name(), SummaryParam.fromValue(findQueryParams.getSummary(), null));

        if (!promotions && !findQueryParams.getIndex().isEmpty()) {
            aciParameters.add(QueryParams.DatabaseMatch.name(), new Databases(findQueryParams.getIndex()));
        }

        aciParameters.add(QueryParams.Combine.name(), CombineParam.Simple);
        aciParameters.add(QueryParams.Predict.name(), false);
        aciParameters.add(QueryParams.FieldText.name(), findQueryParams.getFieldText());
        aciParameters.add(QueryParams.Sort.name(), findQueryParams.getSort());
        aciParameters.add(QueryParams.MinDate.name(), formatDate(findQueryParams.getMinDate()));
        aciParameters.add(QueryParams.MaxDate.name(), formatDate(findQueryParams.getMaxDate()));
        aciParameters.add(QueryParams.Print.name(), PrintParam.Fields);
        aciParameters.add(QueryParams.PrintFields.name(), new PrintFields(FindDocument.ALL_FIELDS));
        aciParameters.add(QueryParams.XMLMeta.name(), true);
        aciParameters.add(QueryParams.AnyLanguage.name(), true);

        if (findQueryParams.isHighlight()) {
            aciParameters.add(QueryParams.Highlight.name(), HighlightParam.SummaryTerms);
            aciParameters.add(QueryParams.StartTag.name(), HIGHLIGHT_START_TAG);
            aciParameters.add(QueryParams.EndTag.name(), HIGHLIGHT_END_TAG);
        }

        aciParameters.add(QmsActionParams.Blacklist.name(), configService.getConfig().getQueryManipulation().getBlacklist());
        aciParameters.add(QmsActionParams.ExpandQuery.name(), configService.getConfig().getQueryManipulation().getExpandQuery());

        if (promotions) {
            aciParameters.add(QmsActionParams.Promotions.name(), true);
        }

        return executeQuery(aciService, aciParameters);
    }

    private Documents<FindDocument> executeQuery(final AciService aciService, final AciParameters aciParameters) {
        QueryResponseData responseData;
        try {
            responseData = aciService.executeAction(aciParameters, queryResponseProcessor);
        } catch (final AciErrorException e) {
            final String errorString = e.getErrorString();
            if (MISSING_RULE_ERROR.equals(errorString) || INVALID_RULE_ERROR.equals(errorString)) {
                aciParameters.remove(QmsActionParams.Blacklist.name());
                responseData = aciService.executeAction(aciParameters, queryResponseProcessor);
            }
            else {
                throw e;
            }
        }

        final List<Hit> hits = responseData.getHit();
        final List<FindDocument> results = parseQueryHits(hits);
        return new Documents<>(results, responseData.getTotalhits(), null);
    }

    @Override
    public List<FindDocument> findSimilar(final Set<String> indexes, final String reference) throws AciErrorException {
        final AciParameters aciParameters = new AciParameters(QueryActions.Suggest.name());
        aciParameters.add(SuggestParams.Reference.name(), new Reference(reference));
        if (!indexes.isEmpty()) {
            aciParameters.add(SuggestParams.DatabaseMatch.name(), new Databases(indexes));
        }
        aciParameters.add(SuggestParams.Print.name(), PrintParam.None);
        aciParameters.add(SuggestParams.MaxResults.name(), MAX_SIMILAR_DOCUMENTS);
        aciParameters.add(SuggestParams.Summary.name(), SummaryParam.Concept);

        final SuggestResponseData responseData = contentAciService.executeAction(aciParameters, suggestResponseProcessor);
        final List<Hit> hits = responseData.getHit();
        return parseQueryHits(hits);
    }

    private String formatDate(final ReadableInstant date) {
        return date == null ? null : DateTimeFormat.forPattern(IDOL_DATE_PARAMETER_FORMAT).print(date);
    }

    private List<FindDocument> parseQueryHits(final Collection<Hit> hits) {
        final List<FindDocument> results = new ArrayList<>(hits.size());
        for (final Hit hit : hits) {
            final FindDocument.Builder findDocumentBuilder = new FindDocument.Builder()
                    .setReference(hit.getReference())
                    .setIndex(hit.getDatabase())
                    .setTitle(hit.getTitle())
                    .setSummary(hit.getSummary())
                    .setDate(hit.getDatestring());

            final DocContent content = hit.getContent();
            if (content != null) {
                final Element docContent = (Element) content.getContent().get(0);
                findDocumentBuilder
                        .setContentType(parseFields(docContent, FindDocument.CONTENT_TYPE_FIELD))
                        .setUrl(parseFields(docContent, FindDocument.URL_FIELD))
                        .setAuthors(parseFields(docContent, FindDocument.AUTHOR_FIELD))
                        .setCategories(parseFields(docContent, FindDocument.CATEGORY_FIELD))
                        .setDateCreated(parseFields(docContent, FindDocument.DATE_CREATED_FIELD))
                        .setCreatedDate(parseFields(docContent, FindDocument.CREATED_DATE_FIELD))
                        .setModifiedDate(parseFields(docContent, FindDocument.MODIFIED_DATE_FIELD))
                        .setDateModified(parseFields(docContent, FindDocument.DATE_MODIFIED_FIELD))
                        .setThumbnailUrl(parseFields(docContent, FindDocument.THUMBNAIL_URL_FIELD));
            }
            results.add(findDocumentBuilder.build());
        }
        return results;
    }

    private List<String> parseFields(final Element node, final String fieldName) {
        final NodeList childNodes = node.getElementsByTagName(fieldName.toUpperCase());
        final int length = childNodes.getLength();
        final List<String> values = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            final Node childNode = childNodes.item(i);
            values.add(childNode.getFirstChild().getNodeValue());
        }

        return values;
    }
}
