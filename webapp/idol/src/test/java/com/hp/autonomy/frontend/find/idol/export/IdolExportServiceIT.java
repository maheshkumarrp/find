/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.idol.export;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.find.core.export.CsvExportStrategy;
import com.hp.autonomy.frontend.find.core.export.ExportService;
import com.hp.autonomy.frontend.find.core.export.ExportServiceIT;
import com.hp.autonomy.frontend.find.core.export.ExportStrategy;
import com.hp.autonomy.searchcomponents.core.search.StateTokenAndResultCount;
import com.hp.autonomy.searchcomponents.idol.beanconfiguration.HavenSearchIdolConfiguration;
import com.hp.autonomy.searchcomponents.idol.configuration.AciServiceRetriever;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.search.HavenSearchAciParameterHandler;
import com.hp.autonomy.searchcomponents.idol.search.IdolDocumentsService;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRequest;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(classes = {HavenSearchIdolConfiguration.class, IdolExportServiceIT.ExportConfiguration.class}, value = "export.it=true", webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class IdolExportServiceIT extends ExportServiceIT<IdolQueryRequest, IdolQueryRestrictions, AciErrorException> {
    @Configuration
    @ConditionalOnProperty("export.it")
    public static class ExportConfiguration {
        @Bean
        public ExportService<IdolQueryRequest, AciErrorException> exportService(
                final HavenSearchAciParameterHandler parameterHandler,
                final AciServiceRetriever aciServiceRetriever,
                final ExportStrategy[] exportStrategies) {
            return new IdolExportService(parameterHandler, aciServiceRetriever, exportStrategies);
        }
        @Bean
        public ExportStrategy csvExportStrategy(final ConfigService<IdolSearchCapable> configService) {
            return new CsvExportStrategy(configService);
        }
    }
}
