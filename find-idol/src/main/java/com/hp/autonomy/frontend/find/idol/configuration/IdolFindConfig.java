/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.idol.configuration;

import com.autonomy.aci.client.transport.AciServerDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.frontend.configuration.AbstractConfig;
import com.hp.autonomy.frontend.configuration.Authentication;
import com.hp.autonomy.frontend.configuration.AuthenticationConfig;
import com.hp.autonomy.frontend.configuration.CommunityAuthentication;
import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.ServerConfig;
import com.hp.autonomy.frontend.find.core.configuration.MapConfig;
import com.hp.autonomy.frontend.find.core.configuration.MapConfiguration;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.idol.category.configuration.CategoryConfig;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.configuration.QueryManipulation;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewConfig;
import com.hp.autonomy.user.UserServiceConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@SuppressWarnings("InstanceVariableOfConcreteClass")
@Data
@EqualsAndHashCode(callSuper = false)
@JsonDeserialize(builder = IdolFindConfig.Builder.class)
public class IdolFindConfig extends AbstractConfig<IdolFindConfig> implements UserServiceConfig, AuthenticationConfig<IdolFindConfig>, IdolSearchCapable, MapConfig {

    private final CommunityAuthentication login;
    private final ServerConfig content;
    private final QueryManipulation queryManipulation;
    @JsonProperty("category")
    private final CategoryConfig categoryConfig;
    @JsonProperty("view")
    private final ViewConfig viewConfig;
    private final MMAP mmap;
    private final FieldsInfo fieldsInfo;
    private final MapConfiguration map;

    @Override
    public IdolFindConfig merge(final IdolFindConfig other) {
        if (other == null) {
            return this;
        }

        return new IdolFindConfig.Builder()
                .setContent(content == null ? other.content : content.merge(other.content))
                .setLogin(login == null ? other.login : login.merge(other.login))
                .setQueryManipulation(queryManipulation == null ? other.queryManipulation : queryManipulation.merge(other.queryManipulation))
                .setCategory(categoryConfig == null ? other.categoryConfig : categoryConfig.merge(other.categoryConfig))
                .setView(viewConfig == null ? other.viewConfig : viewConfig.merge(other.viewConfig))
                .setMmap(mmap == null ? other.mmap : mmap.merge(other.mmap))
                .setFieldsInfo(fieldsInfo == null ? other.fieldsInfo : fieldsInfo.merge(other.fieldsInfo))
                .setMap(map == null ? other.map : map.merge(other.map))
                .build();
    }

    @JsonIgnore
    @Override
    public AciServerDetails getCommunityDetails() {
        return login.getCommunity().toAciServerDetails();
    }

    @JsonIgnore
    @Override
    public Authentication<?> getAuthentication() {
        return login;
    }

    @Override
    public IdolFindConfig withoutDefaultLogin() {
        return new Builder(this)
                .setLogin(login.withoutDefaultLogin())
                .build();
    }

    @Override
    public IdolFindConfig generateDefaultLogin() {
        return new Builder(this)
                .setLogin(login.generateDefaultLogin())
                .build();
    }

    @Override
    public IdolFindConfig withHashedPasswords() {
        // no work to do yet
        return this;
    }

    @Override
    public void basicValidate() throws ConfigException {
        login.basicValidate();
        content.basicValidate("content");

        if (map != null) {
            map.basicValidate("map");
        }

        if (queryManipulation != null) {
            queryManipulation.basicValidate();
        }
    }

    @JsonIgnore
    @Override
    public AciServerDetails getContentAciServerDetails() {
        return content.toAciServerDetails();
    }

    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder {
        private CommunityAuthentication login;
        private ServerConfig content;
        private CategoryConfig category;
        private QueryManipulation queryManipulation;
        private ViewConfig view;
        private MMAP mmap;
        private FieldsInfo fieldsInfo;
        private MapConfiguration map;

        public Builder(final IdolFindConfig config) {
            login = config.login;
            content = config.content;
            queryManipulation = config.queryManipulation;
            category = config.categoryConfig;
            view = config.viewConfig;
            mmap = config.mmap;
            fieldsInfo = config.fieldsInfo;
            map = config.map;
        }

        public IdolFindConfig build() {
            return new IdolFindConfig(login, content, queryManipulation, category, view, mmap, fieldsInfo, map);
        }
    }

}
