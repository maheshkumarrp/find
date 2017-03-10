/*
 * Copyright 2016 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */
package com.hp.autonomy.frontend.find.core.savedsearches;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.jadira.usertype.dateandtime.joda.PersistentDateTime;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Entity
@Table(name = SavedSearch.Table.NAME)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "search_type")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@TypeDefs(@TypeDef(name = SavedSearch.JADIRA_TYPE_NAME, typeClass = PersistentDateTime.class))
@Access(AccessType.FIELD)
public abstract class SavedSearch<T extends SavedSearch<T, B>, B extends SavedSearch.Builder<T, B>> {
    public static final String JADIRA_TYPE_NAME = "jadira";

    @Id
    @Column(name = Table.Column.ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @SuppressWarnings("InstanceVariableOfConcreteClass")
    @CreatedBy
    @ManyToOne
    @JoinColumn(name = Table.Column.USER_ID)
    @JsonIgnore
    private UserEntity user;

    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = IndexesTable.NAME, joinColumns = @JoinColumn(name = IndexesTable.Column.SEARCH_ID))
    private Set<EmbeddableIndex> indexes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = ParametricValuesTable.NAME, joinColumns = @JoinColumn(name = ParametricValuesTable.Column.SEARCH_ID))
    private Set<FieldAndValue> parametricValues;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = ParametricRangesTable.NAME, joinColumns = @JoinColumn(name = ParametricRangesTable.Column.SEARCH_ID))
    private Set<ParametricRange> parametricRanges;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = ConceptClusterPhraseTable.NAME, joinColumns = @JoinColumn(name = ConceptClusterPhraseTable.Column.SEARCH_ID))
    private Set<ConceptClusterPhrase> conceptClusterPhrases;

    @Column(name = Table.Column.START_DATE)
    @Type(type = JADIRA_TYPE_NAME)
    private DateTime minDate;

    @Column(name = Table.Column.END_DATE)
    @Type(type = JADIRA_TYPE_NAME)
    private DateTime maxDate;

    @CreatedDate
    @Column(name = Table.Column.CREATED_DATE)
    @Type(type = JADIRA_TYPE_NAME)
    private DateTime dateCreated;

    @LastModifiedDate
    @Column(name = Table.Column.MODIFIED_DATE)
    @Type(type = JADIRA_TYPE_NAME)
    private DateTime dateModified;

    @Transient
    private DateRange dateRange;

    @Column(name = Table.Column.ACTIVE)
    @JsonIgnore
    private Boolean active;

    @Column(name = Table.Column.MIN_SCORE, nullable = false)
    private Integer minScore = 0;

    protected SavedSearch(final Builder<?, ?> builder) {
        id = builder.id;
        title = builder.title;
        indexes = builder.indexes;
        parametricValues = builder.parametricValues;
        parametricRanges = builder.parametricRanges;
        conceptClusterPhrases = builder.conceptClusterPhrases;
        minDate = builder.minDate;
        maxDate = builder.maxDate;
        dateCreated = builder.dateCreated;
        dateModified = builder.dateModified;
        dateRange = builder.dateRange;
        active = builder.active;
        minScore = builder.minScore;
    }

    /**
     * Merge client-mutable SavedSearch-implementation specific fields from the other search into this one.
     */
    protected abstract void mergeInternal(T other);

    public abstract B toBuilder();

    /**
     * Merge client-mutable fields from the other search into this one.
     */
    @SuppressWarnings("OverlyComplexMethod")
    public void merge(final T other) {
        if (other != null) {
            mergeInternal(other);

            title = other.getTitle() == null ? title : other.getTitle();
            minDate = other.getMinDate() == null ? minDate : other.getMinDate();
            maxDate = other.getMaxDate() == null ? maxDate : other.getMaxDate();
            minScore = other.getMinScore() == null ? minScore : other.getMinScore();
            dateRange = other.getDateRange() == null ? dateRange : other.getDateRange();

            indexes = other.getIndexes() == null ? indexes : other.getIndexes();
            parametricValues = other.getParametricValues() == null ? parametricValues : other.getParametricValues();
            parametricRanges = other.getParametricRanges() == null ? parametricRanges : other.getParametricRanges();

            if (other.getConceptClusterPhrases() != null) {
                conceptClusterPhrases.clear();
                conceptClusterPhrases.addAll(other.getConceptClusterPhrases());
            }
        }
    }

    @SuppressWarnings("unused")
    @Access(AccessType.PROPERTY)
    @Column(name = Table.Column.DATE_RANGE_TYPE)
    @JsonIgnore
    public Integer getDateRangeInt() {
        return dateRange == null ? null : dateRange.getId();
    }

    @SuppressWarnings("unused")
    public void setDateRangeInt(final Integer dateRangeInt) {
        dateRange = DateRange.getType(dateRangeInt);
    }

    // WARNING: This logic is duplicated in the client-side search-data-util
    // Caution: Method has multiple exit points.
    public String toQueryText() {
        if (conceptClusterPhrases.isEmpty()) {
            return "*";
        } else {
            final Collection<List<ConceptClusterPhrase>> groupedClusters = conceptClusterPhrases.stream()
                    .collect(Collectors.groupingBy(ConceptClusterPhrase::getClusterId)).values();

            return groupedClusters.stream()
                    .map(clusterList -> clusterList.stream()
                            .sorted()
                            .map(ConceptClusterPhrase::getPhrase).collect(toList()))
                    .map(clusterPhrases -> wrapInBrackets(StringUtils.join(clusterPhrases, ' ')))
                    .collect(joining(" AND "));
        }
    }

    private String wrapInBrackets(final String input) {
        return input.isEmpty() ? input : '(' + input + ')';
    }

    protected interface Table {
        String NAME = "searches";

        @SuppressWarnings("InnerClassTooDeeplyNested")
        interface Column {
            String ID = "search_id";
            String USER_ID = "user_id";
            String START_DATE = "start_date";
            String END_DATE = "end_date";
            String CREATED_DATE = "created_date";
            String MODIFIED_DATE = "modified_date";
            String ACTIVE = "active";
            String TOTAL_RESULTS = "total_results";
            String DATE_RANGE_TYPE = "date_range_type";
            String MIN_SCORE = "min_score";
        }
    }

    private interface IndexesTable {
        String NAME = "search_indexes";

        @SuppressWarnings("InnerClassTooDeeplyNested")
        interface Column {
            String SEARCH_ID = "search_id";
        }
    }

    private interface ParametricValuesTable {
        String NAME = "search_parametric_values";

        @SuppressWarnings("InnerClassTooDeeplyNested")
        interface Column {
            String SEARCH_ID = "search_id";
        }
    }

    private interface ParametricRangesTable {
        String NAME = "search_parametric_ranges";

        @SuppressWarnings("InnerClassTooDeeplyNested")
        interface Column {
            String SEARCH_ID = "search_id";
        }
    }

    protected interface StoredStateTable {
        String NAME = "search_stored_state";

        @SuppressWarnings("InnerClassTooDeeplyNested")
        interface Column {
            String SEARCH_ID = "search_id";
        }
    }

    interface ConceptClusterPhraseTable {
        String NAME = "search_concept_cluster_phrases";

        @SuppressWarnings("InnerClassTooDeeplyNested")
        interface Column {
            String ID = "search_concept_cluster_phrase_id";
            String SEARCH_ID = "search_id";
            String PHRASE = "phrase";
            String PRIMARY = "primary_phrase";
            String CLUSTER_ID = "cluster_id";
        }
    }

    @NoArgsConstructor
    @Getter
    public abstract static class Builder<T extends SavedSearch<T, B>, B extends Builder<T, B>> {
        private Long id;
        private String title;
        private Set<EmbeddableIndex> indexes;
        private Set<FieldAndValue> parametricValues;
        private Set<ParametricRange> parametricRanges;
        private Set<ConceptClusterPhrase> conceptClusterPhrases;
        private DateTime minDate;
        private DateTime maxDate;
        private DateTime dateCreated;
        private DateTime dateModified;
        private DateRange dateRange;
        private Boolean active = true;
        private Integer minScore;

        protected Builder(final SavedSearch<T, B> search) {
            id = search.id;
            title = search.title;
            indexes = search.indexes;
            parametricValues = search.parametricValues;
            parametricRanges = search.parametricRanges;
            conceptClusterPhrases = search.conceptClusterPhrases;
            minDate = search.minDate;
            maxDate = search.maxDate;
            dateCreated = search.dateCreated;
            dateModified = search.dateModified;
            dateRange = search.dateRange;
            active = search.active;
            minScore = search.minScore;
        }

        public abstract T build();

        public Builder<T, B> setId(final Long id) {
            this.id = id;
            return this;
        }

        public Builder<T, B> setTitle(final String title) {
            this.title = title;
            return this;
        }

        public Builder<T, B> setIndexes(final Set<EmbeddableIndex> indexes) {
            this.indexes = new LinkedHashSet<>(indexes);
            return this;
        }

        public Builder<T, B> setParametricValues(final Set<FieldAndValue> parametricValues) {
            this.parametricValues = new LinkedHashSet<>(parametricValues);
            return this;
        }

        public Builder<T, B> setParametricRanges(final Set<ParametricRange> parametricRanges) {
            this.parametricRanges = new LinkedHashSet<>(parametricRanges);
            return this;
        }

        public Builder<T, B> setConceptClusterPhrases(final Set<ConceptClusterPhrase> conceptClusterPhrases) {
            this.conceptClusterPhrases = new LinkedHashSet<>(conceptClusterPhrases);
            return this;
        }

        public Builder<T, B> setMinDate(final DateTime minDate) {
            this.minDate = minDate;
            return this;
        }

        public Builder<T, B> setMaxDate(final DateTime maxDate) {
            this.maxDate = maxDate;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder<T, B> setDateCreated(final DateTime dateCreated) {
            this.dateCreated = dateCreated;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder<T, B> setDateModified(final DateTime dateModified) {
            this.dateModified = dateModified;
            return this;
        }

        public Builder<T, B> setDateRange(final DateRange dateRange) {
            this.dateRange = dateRange;
            return this;
        }

        public Builder<T, B> setActive(final Boolean active) {
            this.active = active;
            return this;
        }

        public Builder<T, B> setMinScore(final Integer minScore) {
            this.minScore = minScore;
            return this;
        }
    }
}
