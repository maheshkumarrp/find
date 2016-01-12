/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.core.search;

import com.hp.autonomy.types.requests.idol.actions.query.QuerySummaryElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;
import java.util.List;

@Controller
@SuppressWarnings("SpringJavaAutowiringInspection")
@RequestMapping("/api/public/search/find-related-concepts")
public abstract class RelatedConceptsController<Q extends QuerySummaryElement, S extends Serializable, E extends Exception> {
    @Autowired
    protected RelatedConceptsService<Q, S, E> relatedConceptsService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Q> findRelatedConcepts(
            @RequestParam("text") final String text,
            @RequestParam("index") final List<S> index,
            @RequestParam("field_text") final String fieldText,
            @CookieValue(value = "fieldtext", required = false) final String implicitFieldText
    ) throws E {
        return relatedConceptsService.findRelatedConcepts(text, index, FieldTextMerge.mergeFieldText(fieldText, implicitFieldText));
    }
}
