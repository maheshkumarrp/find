/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.core.fields;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.hp.autonomy.frontend.configuration.ConfigFileService;
import com.hp.autonomy.frontend.find.core.configuration.FindConfig;
import com.hp.autonomy.frontend.find.core.configuration.UiCustomization;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequest;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.ValueDetails;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractFieldsControllerTest<C extends FieldsController<R, E, S, Q, P>, R extends FieldsRequest, E extends Exception, S extends Serializable, Q extends QueryRestrictions<S>, P extends ParametricRequest<S>> {
    @Mock
    protected FieldsService<R, E> service;

    @Mock
    protected ParametricValuesService<P, S, E> parametricValuesService;

    @Mock
    protected ConfigFileService<FindConfig> configService;

    @Mock
    private FindConfig config;

    protected C controller;

    protected abstract C constructController();

    protected abstract List<TagName> getParametricFields() throws E;

    protected abstract List<FieldAndValueDetails> getParametricDateFields() throws E;

    protected abstract List<FieldAndValueDetails> getParametricNumericFields() throws E;

    @Before
    public void setUp() throws E {
        when(configService.getConfig()).thenReturn(config);
        when(config.getUiCustomization()).thenReturn(UiCustomization.builder().setParametricAlwaysShow(Collections.emptyList()).build());

        controller = constructController();
    }

    @Test
    public void getParametricFieldsTest() throws E {
        final Map<FieldTypeParam, List<TagName>> response = new EnumMap<>(FieldTypeParam.class);
        response.put(FieldTypeParam.Numeric, ImmutableList.of(new TagName("NumericField"), new TagName("ParametricNumericField")));
        response.put(FieldTypeParam.NumericDate, ImmutableList.of(new TagName("DateField"), new TagName("ParametricDateField")));
        response.put(FieldTypeParam.Parametric, ImmutableList.of(new TagName("ParametricField"), new TagName("ParametricNumericField"), new TagName("ParametricDateField")));
        when(service.getFields(Matchers.any(), eq(FieldTypeParam.Parametric), eq(FieldTypeParam.Numeric), eq(FieldTypeParam.NumericDate))).thenReturn(response);

        final List<TagName> fields = getParametricFields();
        assertThat(fields, hasSize(1));
        assertThat(fields, hasItem(is(new TagName("ParametricField"))));
    }

    @Test
    public void getParametricFieldsWithAlwaysShowList() throws E {
        mockSimpleParametricResponse();

        when(config.getUiCustomization()).thenReturn(UiCustomization.builder()
                .setParametricAlwaysShow(Arrays.asList("ParametricField1", "DOCUMENT/ParametricField2"))
                .build());

        final List<TagName> fields = getParametricFields();
        assertThat(fields, hasSize(2));
        assertThat(fields, hasItem(is(new TagName("/DOCUMENT/ParametricField1"))));
        assertThat(fields, hasItem(is(new TagName("/DOCUMENT/ParametricField2"))));
    }

    @Test
    public void getParametricFieldsWithNeverShowList() throws E {
        mockSimpleParametricResponse();

        when(config.getUiCustomization()).thenReturn(UiCustomization.builder()
                .setParametricNeverShow(Arrays.asList("ParametricField1", "DOCUMENT/ParametricField2"))
                .build());

        final List<TagName> fields = getParametricFields();
        assertThat(fields, hasSize(1));
        assertThat(fields, hasItem(is(new TagName("/DOCUMENT/ParametricField3"))));
    }

    @Test
    public void getParametricFieldsWithAlwaysShowListAndNeverShowList() throws E {
        mockSimpleParametricResponse();

        when(config.getUiCustomization()).thenReturn(UiCustomization.builder()
                .setParametricAlwaysShow(Arrays.asList("ParametricField1", "DOCUMENT/ParametricField2"))
                .setParametricNeverShow(Collections.singletonList("ParametricField1"))
                .build());

        final List<TagName> fields = getParametricFields();
        assertThat(fields, hasSize(1));
        assertThat(fields, hasItem(is(new TagName("/DOCUMENT/ParametricField2"))));
    }

    @Test
    public void getParametricDateFieldsWithNeverShowList() throws E {
        final Map<FieldTypeParam, List<TagName>> response = new EnumMap<>(FieldTypeParam.class);
        response.put(FieldTypeParam.NumericDate, Collections.emptyList());
        response.put(FieldTypeParam.Parametric, Collections.emptyList());
        when(service.getFields(Matchers.any(), eq(FieldTypeParam.Parametric), eq(FieldTypeParam.NumericDate))).thenReturn(response);

        when(config.getUiCustomization()).thenReturn(UiCustomization.builder()
                .setParametricNeverShow(Collections.singletonList(ParametricValuesService.AUTN_DATE_FIELD))
                .build());

        final List<FieldAndValueDetails> output = getParametricDateFields();
        assertThat(output, is(empty()));
    }

    private void mockSimpleParametricResponse() throws E {
        final Map<FieldTypeParam, List<TagName>> response = new EnumMap<>(FieldTypeParam.class);
        response.put(FieldTypeParam.Numeric, Collections.emptyList());
        response.put(FieldTypeParam.NumericDate, Collections.emptyList());
        response.put(FieldTypeParam.Parametric, ImmutableList.of(new TagName("DOCUMENT/ParametricField1"), new TagName("DOCUMENT/ParametricField2"), new TagName("DOCUMENT/ParametricField3")));
        when(service.getFields(Matchers.any(), eq(FieldTypeParam.Parametric), eq(FieldTypeParam.Numeric), eq(FieldTypeParam.NumericDate))).thenReturn(response);
    }

    @Test
    public void getParametricNumericFieldsTest() throws E {
        final String fieldName = "ParametricNumericField";

        final Map<FieldTypeParam, List<TagName>> response = new EnumMap<>(FieldTypeParam.class);
        response.put(FieldTypeParam.Numeric, ImmutableList.of(new TagName("NumericField"), new TagName(fieldName)));
        response.put(FieldTypeParam.Parametric, ImmutableList.of(new TagName("ParametricField"), new TagName(fieldName), new TagName("ParametricDateField")));
        when(service.getFields(Matchers.any(), eq(FieldTypeParam.Parametric), eq(FieldTypeParam.Numeric))).thenReturn(response);

        final ValueDetails valueDetails = new ValueDetails.Builder()
                .setMin(1.4)
                .setMax(2.5)
                .setAverage(1.9)
                .setSum(10.8)
                .setTotalValues(25)
                .build();

        final Map<TagName, ValueDetails> valueDetailsOutput = ImmutableMap.<TagName, ValueDetails>builder()
                .put(new TagName(fieldName), valueDetails)
                .build();

        when(parametricValuesService.getValueDetails(Matchers.any())).thenReturn(valueDetailsOutput);

        final List<FieldAndValueDetails> fields = getParametricNumericFields();
        assertThat(fields, hasSize(1));
        assertThat(fields, hasItem(is(new FieldAndValueDetails("ParametricNumericField", "ParametricNumericField", 1.4, 2.5, 25))));
    }

    @Test
    public void getParametricDateFieldsTest() throws E {
        final Map<FieldTypeParam, List<TagName>> response = new EnumMap<>(FieldTypeParam.class);
        response.put(FieldTypeParam.NumericDate, ImmutableList.of(new TagName("DateField"), new TagName("ParametricDateField")));
        response.put(FieldTypeParam.Parametric, ImmutableList.of(new TagName("ParametricField"), new TagName("ParametricNumericField"), new TagName("ParametricDateField")));
        when(service.getFields(Matchers.any(), eq(FieldTypeParam.Parametric), eq(FieldTypeParam.NumericDate))).thenReturn(response);

        final ValueDetails valueDetails = new ValueDetails.Builder()
                .setMin(146840000d)
                .setMax(146860000d)
                .setAverage(146850000d)
                .setSum(1046850000d)
                .setTotalValues(1000)
                .build();

        final ValueDetails autnDateValueDetails = new ValueDetails.Builder()
                .setMin(100000000d)
                .setMax(150000000d)
                .setAverage(130000000d)
                .setSum(1050000000d)
                .setTotalValues(15000)
                .build();

        final Map<TagName, ValueDetails> valueDetailsOutput = ImmutableMap.<TagName, ValueDetails>builder()
                .put(new TagName("ParametricDateField"), valueDetails)
                .put(new TagName(ParametricValuesService.AUTN_DATE_FIELD), autnDateValueDetails)
                .build();

        when(parametricValuesService.getValueDetails(Matchers.any())).thenReturn(valueDetailsOutput);

        final List<FieldAndValueDetails> fields = getParametricDateFields();
        assertThat(fields, hasSize(2));
        assertThat(fields, hasItem(is(new FieldAndValueDetails("ParametricDateField", "ParametricDateField", 146840000d, 146860000d, 1000))));
        assertThat(fields, hasItem(is(new FieldAndValueDetails(ParametricValuesService.AUTN_DATE_FIELD, ParametricValuesService.AUTN_DATE_FIELD, 100000000d, 150000000d, 15000))));
    }
}
