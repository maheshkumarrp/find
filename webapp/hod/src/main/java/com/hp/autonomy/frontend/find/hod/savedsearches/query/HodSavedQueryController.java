/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.hod.savedsearches.query;

import com.hp.autonomy.frontend.find.core.beanconfiguration.BiConfiguration;
import com.hp.autonomy.frontend.find.core.savedsearches.EmbeddableIndex;
import com.hp.autonomy.frontend.find.core.savedsearches.FieldTextParser;
import com.hp.autonomy.frontend.find.core.savedsearches.SavedSearchService;
import com.hp.autonomy.frontend.find.core.savedsearches.query.SavedQuery;
import com.hp.autonomy.frontend.find.core.savedsearches.query.SavedQueryController;
import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.hod.client.api.textindex.query.search.Print;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.search.QueryRequestBuilder;
import com.hp.autonomy.searchcomponents.hod.search.*;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty(BiConfiguration.BI_PROPERTY)
class HodSavedQueryController extends SavedQueryController<HodQueryRequest, ResourceName, HodQueryRestrictions, HodSearchResult, HodErrorException> {
    @SuppressWarnings("TypeMayBeWeakened")
    @Autowired
    public HodSavedQueryController(final SavedSearchService<SavedQuery, SavedQuery.Builder> service,
                                   final HodDocumentsService documentsService,
                                   final FieldTextParser fieldTextParser,
                                   final ObjectFactory<HodQueryRestrictionsBuilder> queryRestrictionsBuilderFactory,
                                   final ObjectFactory<HodQueryRequestBuilder> queryRequestBuilderFactory) {
        super(service, documentsService, fieldTextParser, queryRestrictionsBuilderFactory, queryRequestBuilderFactory);
    }

    @Override
    protected ResourceName convertEmbeddableIndex(final EmbeddableIndex embeddableIndex) {
        return new ResourceName(embeddableIndex.getDomain(), embeddableIndex.getName());
    }

    @Override
    protected void addParams(final QueryRequestBuilder<HodQueryRequest, HodQueryRestrictions, ?> queryRequestBuilder) {
        queryRequestBuilder.print(Print.no_results.name());
    }
}
