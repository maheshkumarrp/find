/*
 * Copyright 2014-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.configuration;

import com.hp.autonomy.frontend.configuration.AbstractAuthenticatingConfigFileService;

public class HodFindConfigFileService extends AbstractAuthenticatingConfigFileService<HodFindConfig> {

    @Override
    public HodFindConfig preUpdate(final HodFindConfig config) {
        return config;
    }

    @Override
    public void postUpdate(final HodFindConfig config) throws Exception {

    }

    @Override
    public void postInitialise(final HodFindConfig config) throws Exception {
        postUpdate(config);
    }

    @Override
    public Class<HodFindConfig> getConfigClass() {
        return HodFindConfig.class;
    }

    @Override
    public HodFindConfig getEmptyConfig() {
        return new HodFindConfig.Builder().build();
    }
}
