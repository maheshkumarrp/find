/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.core.fields;

import com.hp.autonomy.frontend.find.core.test.AbstractFindIT;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public abstract class FieldsControllerIT extends AbstractFindIT {
    protected abstract void addParams(MockHttpServletRequestBuilder requestBuilder);

    @Test
    public void getParametricFields() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = get(FieldsController.FIELDS_PATH + FieldsController.GET_PARAMETRIC_FIELDS_PATH).with(authentication(userAuth()));
        requestBuilder.param(FieldsController.FIELD_TYPES_PARAM, FieldTypeParam.Parametric.name(), FieldTypeParam.Numeric.name(), FieldTypeParam.NumericDate.name());
        addParams(requestBuilder);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", not(empty())));
    }
}
