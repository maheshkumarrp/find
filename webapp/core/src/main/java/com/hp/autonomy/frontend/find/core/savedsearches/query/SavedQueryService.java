/*
 * Copyright 2016 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.core.savedsearches.query;

import com.hp.autonomy.frontend.find.core.beanconfiguration.BiConfiguration;
import com.hp.autonomy.frontend.find.core.savedsearches.AbstractSavedSearchService;
import com.hp.autonomy.frontend.find.core.savedsearches.UserEntity;
import com.hp.autonomy.searchcomponents.core.fields.TagNameFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(BiConfiguration.BI_PROPERTY)
public class SavedQueryService extends AbstractSavedSearchService<SavedQuery, SavedQuery.Builder> {
    @Autowired
    public SavedQueryService(@SuppressWarnings("TypeMayBeWeakened")
                             final SavedQueryRepository savedQueryRepository,
                             final AuditorAware<UserEntity> userEntityAuditorAware,
                             final TagNameFactory tagNameFactory) {
        super(savedQueryRepository, userEntityAuditorAware, tagNameFactory);
    }
}
