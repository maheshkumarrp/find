/*
 * Copyright 2014-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.configuration;

import com.hp.autonomy.frontend.configuration.ValidationResults;
import com.hp.autonomy.frontend.configuration.ValidationService;
import com.hp.autonomy.frontend.find.beanconfiguration.HodCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/api/useradmin/config", "/api/config/config"})
@Conditional(HodCondition.class) // TODO make this a generic type
public class ValidationController {

    @Autowired
    private ValidationService<HodFindConfig> validationService;

    @RequestMapping(value = "/config-validation", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public ValidationResults validConfig(@RequestBody final HodFindConfig config){
        return validationService.validateConfig(config);
    }
}
