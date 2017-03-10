/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.hod.export;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.find.core.export.CsvExportStrategy;
import com.hp.autonomy.frontend.find.core.export.ExportService;
import com.hp.autonomy.frontend.find.core.export.ExportServiceIT;
import com.hp.autonomy.frontend.find.core.export.ExportStrategy;
import com.hp.autonomy.hod.client.api.textindex.query.search.Print;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import com.hp.autonomy.searchcomponents.hod.beanconfiguration.HavenSearchHodConfiguration;
import com.hp.autonomy.searchcomponents.hod.configuration.HodSearchCapable;
import com.hp.autonomy.searchcomponents.hod.search.HodDocumentsService;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRequest;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictions;
import com.hp.autonomy.searchcomponents.hod.search.HodSearchResult;
import com.hp.autonomy.types.requests.Documents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(classes = {HavenSearchHodConfiguration.class, HodExportServiceIT.ExportConfiguration.class}, properties = "export.it=true", webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class HodExportServiceIT extends ExportServiceIT<HodQueryRequest, HodQueryRestrictions, HodErrorException> {
    @Configuration
    @ConditionalOnProperty("export.it")
    public static class ExportConfiguration {
        @Bean
        public ExportService<HodQueryRequest, HodErrorException> exportService(
                final HodDocumentsService documentsService,
                final ExportStrategy[] exportStrategies) {
            return new HodExportService(documentsService, exportStrategies);
        }

        @Bean
        public ExportStrategy csvExportStrategy(final ConfigService<HodSearchCapable> configService, final FieldPathNormaliser fieldPathNormaliser) {
            return new CsvExportStrategy(configService, fieldPathNormaliser);
        }
    }
}
