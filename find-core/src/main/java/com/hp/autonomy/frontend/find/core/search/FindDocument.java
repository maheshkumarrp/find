/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.core.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableSet;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Data
@JsonDeserialize(builder = FindDocument.Builder.class)
public class FindDocument implements Serializable {
    public static final String CONTENT_TYPE_FIELD = "content_type";
    public static final String URL_FIELD = "url";
    public static final String OFFSET_FIELD = "offset";
    public static final String AUTHOR_FIELD = "author";
    public static final String CATEGORY_FIELD = "category";
    public static final String DATE_FIELD = "date";
    public static final String DATE_CREATED_FIELD = "date_created";
    public static final String CREATED_DATE_FIELD = "created_date";
    public static final String DATE_MODIFIED_FIELD = "date_modified";
    public static final String MODIFIED_DATE_FIELD = "modified_date";
    public static final String THUMBNAIL_URL_FIELD = "thumbnail_url";

    public static final ImmutableSet<String> ALL_FIELDS = ImmutableSet.of(
            CONTENT_TYPE_FIELD,
            URL_FIELD,
            OFFSET_FIELD,
            AUTHOR_FIELD,
            CATEGORY_FIELD,
            DATE_FIELD,
            DATE_CREATED_FIELD,
            CREATED_DATE_FIELD,
            DATE_MODIFIED_FIELD,
            MODIFIED_DATE_FIELD,
            THUMBNAIL_URL_FIELD
    );

    private static final long serialVersionUID = 7647398627476128115L;

    private final String reference;
    private final String index;

    private final String title;
    private final String summary;
    private final String contentType;
    private final String url;
    private final String offset;
    private final String thumbnailUrl;

    private final List<String> authors;
    private final List<String> categories;

    private final DateTime date;
    private final DateTime dateCreated;
    private final DateTime dateModified;

    protected FindDocument(final Builder builder) {
        reference = builder.reference;
        index = builder.index;

        title = builder.title;
        summary = builder.summary;
        contentType = builder.contentType;
        url = builder.url;
        offset = builder.offset;

        thumbnailUrl = builder.thumbnailUrl;

        // LinkedList so we can guarantee Serializable
        authors = builder.authors == null ? Collections.<String>emptyList() : new LinkedList<>(builder.authors);
        categories = builder.categories == null ? Collections.<String>emptyList() : new LinkedList<>(builder.categories);

        date = builder.date;
        dateCreated = builder.dateCreated;
        dateModified = builder.dateModified;
    }

    @SuppressWarnings("FieldMayBeFinal")
    @Setter
    @NoArgsConstructor
    @Accessors(chain = true)
    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder {
        private String reference;
        private String index;

        private String title;
        private String summary;

        private String contentType;

        private String url;
        private String offset;

        private String thumbnailUrl;

        @JsonProperty(AUTHOR_FIELD)
        private List<String> authors;

        @JsonProperty(CATEGORY_FIELD)
        private List<String> categories;

        private DateTime date;
        private DateTime dateCreated;
        private DateTime dateModified;

        public Builder(final FindDocument document) {
            reference = document.reference;
            index = document.index;
            title = document.title;
            summary = document.summary;
            contentType = document.contentType;
            url = document.url;
            offset = document.offset;
            thumbnailUrl = document.thumbnailUrl;
            authors = document.authors;
            categories = document.categories;
            date = document.date;
            dateCreated = document.dateCreated;
            dateModified = document.dateModified;
        }

        @JsonProperty(CONTENT_TYPE_FIELD)
        public Builder setContentType(final List<String> contentTypes) {
            if (contentTypes != null && !contentTypes.isEmpty()) {
                contentType = contentTypes.get(0);
            }

            return this;
        }

        @JsonProperty(URL_FIELD)
        public Builder setUrl(final List<String> urls) {
            if (urls != null && !urls.isEmpty()) {
                url = urls.get(0);
            }

            return this;
        }

        @JsonProperty(OFFSET_FIELD)
        public Builder setOffset(final List<String> offsets) {
            if (offsets != null && !offsets.isEmpty()) {
                offset = offsets.get(0);
            }

            return this;
        }

        @JsonProperty(THUMBNAIL_URL_FIELD)
        public Builder setThumbnailUrl(final List<String> thumbnailUrls) {
            if (thumbnailUrls != null && !thumbnailUrls.isEmpty()) {
                thumbnailUrl = thumbnailUrls.get(0);
            }

            return this;
        }

        @SuppressWarnings("UseOfObsoleteDateTimeApi")
        public Builder setDate(final Date date) {
            if (date != null) {
                this.date = new DateTime(date);
            }

            return this;
        }

        @JsonProperty(DATE_FIELD)
        public Builder setDate(final List<String> dateStrings) {
            final DateTime parsedDate = parseDateList(dateStrings);

            if (parsedDate != null) {
                date = parsedDate;
            }

            return this;
        }

        @JsonProperty(DATE_CREATED_FIELD)
        public Builder setDateCreated(final List<String> dateStrings) {
            final DateTime parsedDate = parseDateList(dateStrings);

            if (parsedDate != null) {
                dateCreated = parsedDate;
            }

            return this;
        }

        @JsonProperty(CREATED_DATE_FIELD)
        public Builder setCreatedDate(final List<String> dateStrings) {
            final DateTime parsedDate = parseDateList(dateStrings);

            if (parsedDate != null) {
                dateCreated = parsedDate;
            }

            return this;
        }

        @JsonProperty(DATE_MODIFIED_FIELD)
        public Builder setDateModified(final List<String> dateStrings) {
            final DateTime parsedDate = parseDateList(dateStrings);

            if (parsedDate != null) {
                dateModified = parsedDate;
            }

            return this;
        }

        @JsonProperty(MODIFIED_DATE_FIELD)
        public Builder setModifiedDate(final List<String> dateStrings) {
            final DateTime parsedDate = parseDateList(dateStrings);

            if (parsedDate != null) {
                dateModified = parsedDate;
            }

            return this;
        }

        public FindDocument build() {
            return new FindDocument(this);
        }

        private DateTime parseDateList(final List<String> dateStrings) {
            if (dateStrings != null && !dateStrings.isEmpty()) {
                final DateTime parsedDate = parseDate(dateStrings.get(0));

                if (parsedDate != null) {
                    return parsedDate;
                }
            }

            return null;
        }

        // HOD handles date fields inconsistently; attempt to detect this here
        private DateTime parseDate(final String dateString) {
            DateTime result;

            try {
                // dateString is an ISO-8601 timestamp
                result = new DateTime(dateString);
            } catch (final IllegalArgumentException e) {
                // format is invalid, let's try a UNIX timestamp
                try {
                    result = new DateTime(Long.parseLong(dateString) * 1000L);
                } catch (final NumberFormatException e1) {
                    // date field is in a crazy unknown format, treat as if non-existent
                    result = null;
                }
            }

            return result;
        }
    }
}
