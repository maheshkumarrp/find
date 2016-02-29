/*
 * Copyright 2016 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

define([
    'backbone',
    'moment',
    'find/app/page/search/filters/date/date-periods',
    'find/app/util/search-data-util'
], function(Backbone, moment, DatePeriods, searchDataUtil) {

    /**
     * @readonly
     * @enum {String}
     */
    var Sort = {
        date: 'date',
        relevance: 'relevance'
    };

    var DEBOUNCE_WAIT_MILLISECONDS = 500;

    var collectionBuildIndexes = function(collection) {
        return searchDataUtil.buildIndexes(collection.map(function (model) {
            return model.pick('domain', 'name');
        }));
    };

    return Backbone.Model.extend({
        defaults: {
            autoCorrect: true,
            datePeriod: 'hour',
            queryText: '',
            indexes: [],
            fieldText: null,
            minDate: undefined,
            maxDate: undefined,
            sort: Sort.relevance
        },

        /**
         * @param {Object} attributes
         * @param {{queryState: QueryState}} options
         */
        initialize: function(attributes, options) {
            this.queryState = options.queryState;

            this.on('change:minDate change:maxDate', function(){
                var min = this.get('minDate')
                // The max is always bounded by the current time, assuming there's no future-dated documents.
                // If the user specifies a maxDate in the future, presumably they're expecting future-dated docs, so
                //  use that value as-is.
                var max = this.get('maxDate') || moment()

                if (min && max) {
                    this.set('datePeriod', DatePeriods.choosePeriod(min, max))
                }
                else {
                    this.set('datePeriod', 'day')
                }
            }, this)

            this.listenTo(this.queryState.queryTextModel, 'change', function() {
                this.set('queryText', this.queryState.queryTextModel.makeQueryText());
            });

            this.listenTo(this.queryState.datesFilterModel, 'change', function() {
                this.set(this.queryState.datesFilterModel.toQueryModelAttributes());
            });

            this.listenTo(this.queryState.selectedIndexes, 'update reset', _.debounce(_.bind(function() {
                this.set('indexes', collectionBuildIndexes(this.queryState.selectedIndexes));
            }, this), DEBOUNCE_WAIT_MILLISECONDS));

            this.listenTo(this.queryState.selectedParametricValues, 'add remove reset', _.debounce(_.bind(function() {
                var fieldTextNode = this.queryState.selectedParametricValues.toFieldTextNode();
                this.set('fieldText', fieldTextNode ? fieldTextNode : null);
            }, this), DEBOUNCE_WAIT_MILLISECONDS));

            var fieldTextNode = this.queryState.selectedParametricValues.toFieldTextNode();

            this.set(_.extend({
                queryText: this.queryState.queryTextModel.makeQueryText(),
                indexes: collectionBuildIndexes(this.queryState.selectedIndexes),
                fieldText: fieldTextNode ? fieldTextNode : null
            }, this.queryState.datesFilterModel.toQueryModelAttributes()));
        },

        getIsoDate: function(type) {
            var date = this.get(type);

            if (date) {
                return date.toISOString();
            } else {
                return null;
            }
        }
    }, {
        Sort: Sort
    });

});
