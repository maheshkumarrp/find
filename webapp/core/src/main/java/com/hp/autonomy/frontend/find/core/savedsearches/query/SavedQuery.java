/*
 * Copyright 2016 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */
package com.hp.autonomy.frontend.find.core.savedsearches.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.frontend.find.core.savedsearches.SavedSearch;
import com.hp.autonomy.frontend.find.core.savedsearches.SavedSearchType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.jadira.usertype.dateandtime.joda.PersistentDateTime;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(SavedSearchType.Values.QUERY)
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize(builder = SavedQuery.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@TypeDefs(@TypeDef(name = SavedSearch.JADIRA_TYPE_NAME, typeClass = PersistentDateTime.class))
public class SavedQuery extends SavedSearch<SavedQuery, SavedQuery.Builder> {
    @Column(name = "last_fetched_new_date")
    @Type(type = JADIRA_TYPE_NAME)
    private DateTime dateNewDocsLastFetched;

    @Column(name = "last_fetched_date")
    @Type(type = JADIRA_TYPE_NAME)
    private DateTime dateDocsLastFetched;

    private SavedQuery(final Builder builder) {
        super(builder);
        dateNewDocsLastFetched = builder.dateNewDocsLastFetched;
        dateDocsLastFetched = builder.dateDocsLastFetched;
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    protected void mergeInternal(final SavedQuery other) {
        dateNewDocsLastFetched = other.dateNewDocsLastFetched == null ? dateNewDocsLastFetched : other.dateNewDocsLastFetched;
        dateDocsLastFetched = other.dateDocsLastFetched == null ? dateDocsLastFetched : other.dateDocsLastFetched;
    }

    @NoArgsConstructor
    @Setter
    @Accessors(chain = true)
    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder extends SavedSearch.Builder<SavedQuery, Builder> {
        private DateTime dateNewDocsLastFetched;
        private DateTime dateDocsLastFetched;

        public Builder(final SavedQuery query) {
            super(query);
            dateNewDocsLastFetched = query.dateNewDocsLastFetched;
            dateDocsLastFetched = query.dateDocsLastFetched;
        }

        @Override
        public SavedQuery build() {
            return new SavedQuery(this);
        }
    }
}
