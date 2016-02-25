/*
 * Copyright 2016 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

define([
    'backbone',
    'underscore',
    'jquery',
    'js-whatever/js/list-view',
    'js-whatever/js/filtering-collection',
    'find/app/page/search/filters/parametric/parametric-field-view',
    'find/app/util/model-any-changed-attribute-listener',
    'fieldtext/js/field-text-parser',
    'parametric-refinement/display-collection',
    'i18n!find/nls/bundle',
    'text!find/templates/app/page/search/filters/parametric/parametric-view.html'
], function(Backbone, _, $, ListView, FilteringCollection, FieldView, addChangeListener, parser, DisplayCollection, i18n, template) {

    return Backbone.View.extend({
        template: _.template(template)({i18n: i18n}),

        events: {
            'click [data-field] [data-value]': function(e) {
                var $target = $(e.currentTarget);
                var $field = $target.closest('[data-field]');

                var attributes = {
                    field: $field.attr('data-field'),
                    value: $target.attr('data-value')
                };

                if (this.selectedParametricValues.get(attributes)) {
                    this.selectedParametricValues.remove(attributes);
                } else {
                    this.selectedParametricValues.add(attributes);
                }
            }
        },

        initialize: function(options) {
            this.queryModel = options.queryModel;

            this.selectedParametricValues = options.queryState.selectedParametricValues;

            this.parametricCollection = options.parametricCollection;

            this.model = new Backbone.Model({processing: false, error: false});
            this.listenTo(this.model, 'change:processing', this.updateProcessing);
            this.listenTo(this.model, 'change:error', this.updateError);

            this.displayCollection = new DisplayCollection([], {
                parametricCollection: new FilteringCollection([], {
                    collection: this.parametricCollection,
                    modelFilter: function(model, filters){
                        return model.get('name') !== 'autn_date';
                    }
                }),
                selectedParametricValues: this.selectedParametricValues
            });

            this.fieldNamesListView = new ListView({
                collection: this.displayCollection,
                ItemView: FieldView
            });

            function fetch() {
                this.parametricCollection.reset();
                this.model.set({processing: true, error: false});
                var $parametric = this.$('.parametric-empty');
                $parametric.addClass('hide');

                if(!this.queryModel.get('queryText') || this.queryModel.get('indexes').length === 0) {
                    this.model.set('processing', false);
                } else {
                    this.parametricCollection.fetch({
                        data: {
                            databases: this.queryModel.get('indexes'),
                            queryText: this.queryModel.get('queryText'),
                            fieldText: this.queryModel.get('fieldText'),
                            minDate: this.queryModel.getIsoDate('minDate'),
                            maxDate: this.queryModel.getIsoDate('maxDate'),
                            datePeriod: 'hour'
                        },
                        error: _.bind(function (collection, xhr) {
                            if (xhr.status !== 0) {
                                // The request was not aborted, so there isn't another request in flight
                                this.model.set({error: true, processing: false});
                            }
                        }, this),
                        success: _.bind(function (results) {
                            if (results.length === 0) {
                                this.$('.parametric-empty').removeClass('hide');
                            }
                            this.model.set({processing: false});
                        }, this)
                    });
                }
            }

            addChangeListener(this, this.queryModel, ['queryText', 'indexes', 'fieldText', 'minDate', 'maxDate'], fetch);
            fetch.call(this);
        },

        render: function() {
            this.$el.html(this.template).prepend(this.fieldNamesListView.render().$el);

            this.$errorMessage = this.$('.parametric-error');
            this.$processing = this.$('.parametric-processing-indicator');

            this.updateError();
            this.updateProcessing();

            return this;
        },

        updateProcessing: function() {
            if (this.$processing) {
                this.$processing.toggleClass('hide', !this.model.get('processing'));
            }
        },

        updateError: function() {
            if (this.$errorMessage) {
                this.$errorMessage.toggleClass('hide', !this.model.get('error'));
            }
        }
    });

});
