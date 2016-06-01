/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.core.web;

public enum ViewNames
{
    ERROR("error"),
    LOGIN("login"),
    PUBLIC("public"),
    SSO("sso"),
    SSO_LOGOUT("sso-logout"),
    BLANK("blank");

    private final String viewName;

    ViewNames(final String viewName)
    {
        this.viewName = viewName;
    }

    public String viewName()
    {
        return viewName;
    }
}
