/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.hod.parametricfields;

import com.hp.autonomy.frontend.find.core.parametricfields.ParametricValuesController;
import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.hod.parametricvalues.HodParametricRequest;
import com.hp.autonomy.searchcomponents.hod.parametricvalues.HodParametricRequestBuilder;
import com.hp.autonomy.searchcomponents.hod.parametricvalues.HodParametricValuesService;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictions;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictionsBuilder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(ParametricValuesController.PARAMETRIC_PATH)
class HodParametricValuesController extends ParametricValuesController<HodQueryRestrictions, HodParametricRequest, ResourceName, HodErrorException> {
    @SuppressWarnings("TypeMayBeWeakened")
    @Autowired
    public HodParametricValuesController(
            final HodParametricValuesService parametricValuesService,
            final ObjectFactory<HodQueryRestrictionsBuilder> queryRestrictionsBuilderFactory,
            final ObjectFactory<HodParametricRequestBuilder> parametricRequestBuilderFactory
    ) {
        super(parametricValuesService, queryRestrictionsBuilderFactory, parametricRequestBuilderFactory);
    }
}
