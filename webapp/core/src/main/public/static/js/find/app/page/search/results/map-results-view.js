define([
    'backbone',
    'underscore',
    'jquery',
    'find/app/configuration',
    'find/app/page/search/results/field-selection-view',
    'find/app/page/search/results/map-view',
    'i18n!find/nls/bundle',
    'find/app/model/documents-collection',
    'find/app/page/search/results/add-links-to-summary',
    'text!find/templates/app/page/search/results/map-results-view.html',
    'text!find/templates/app/page/search/results/map-popover.html',
    'text!find/templates/app/page/loading-spinner.html',
    'find/app/vent'

], function (Backbone, _, $, configuration, FieldSelectionView, MapView, i18n, DocumentsCollection, addLinksToSummary, template, popoverTemplate, loadingSpinnerTemplate, vent) {

    'use strict';

    return Backbone.View.extend({
        template: _.template(template),
        popoverTemplate: _.template(popoverTemplate),
        loadingTemplate: _.template(loadingSpinnerTemplate)({i18n: i18n, large: false}),
        markers: [],

        events: {
            'click .map-show-more': function () {
                this.fetchDocumentCollection()
            },
            'click .map-popup-title': function (e) {
                vent.navigateToDetailRoute(this.documentsCollection.get(e.currentTarget.getAttribute('cid')));
            },
            'click .map-pptx': function(e){
                e.preventDefault();
                this.mapResultsView.exportPPT('Showing field ' + this.fieldSelectionView.model.get('displayName'))
            }
        },

        initialize: function (options) {
            this.locationFields = configuration().map.locationFields;
            this.resultsStep = options.resultsStep;
            this.allowIncrement = options.allowIncrement;

            this.queryModel = options.queryModel;

            this.documentsCollection = new DocumentsCollection();
            this.model = new Backbone.Model({
                loading: false,
                text: ''
            });

            this.fieldSelectionView = new FieldSelectionView({
                model: this.model,
                name: 'FieldSelectionView',
                fields: this.locationFields.map(function (locationField) {
                    return {
                        id: locationField.displayName,
                        displayName: locationField.displayName
                    }
                }),
                allowEmpty: false
            });

            this.mapResultsView = new MapView({addControl: false});

            this.listenTo(this.model, 'change:loading', this.toggleLoading);

            this.listenTo(this.documentsCollection, 'add', function (model) {
                const locations = model.get('locations');
                const location = _.findWhere(locations, {displayName: this.model.get('field')});
                if (location) {
                    const longitude = location.longitude;
                    const latitude = location.latitude;
                    const title = model.get('title');
                    const popover = this.popoverTemplate({
                        title: title,
                        i18n: i18n,
                        summary: addLinksToSummary(model.get('summary')),
                        cidForClickRouting: model.cid
                    });
                    const marker = this.mapResultsView.getMarker(latitude, longitude, this.getIcon(), title, popover);
                    this.markers.push(marker);
                }
            });

            this.listenTo(this.documentsCollection, 'sync', _.bind(function () {
                if (!_.isEmpty(this.markers)) {
                    this.mapResultsView.addMarkers(this.markers, true);
                    this.mapResultsView.loaded();
                }
                this.$('.map-results-count').html(this.getResultsNoHTML());
                this.model.set('loading', false);
            }, this));

            this.listenTo(this.queryModel, 'change', this.reloadMarkers);
            this.listenTo(this.model, 'change:field', this.reloadMarkers);
        },

        render: function () {
            this.$el.html(this.template({
                showMore: i18n['search.resultsView.map.show.more']
            }));
            this.mapResultsView.setElement(this.$('.location-results-map')).render();
            this.$el.prepend(this.fieldSelectionView.$el);
            this.$loadingSpinner = $(this.loadingTemplate);
            this.$loadMoreButton = this.$('.map-show-more');
            if (!this.allowIncrement) {
                this.$loadMoreButton.addClass('hide disabled');
            }
            this.fieldSelectionView.$el.after(this.$loadMoreButton);
            this.$('.map-loading-spinner').html(this.$loadingSpinner);

            this.toggleLoading();
            this.$loadMoreButton.prop('disabled', true);

            this.fieldSelectionView.render();
        },

        getResultsNoHTML: function () {
            if (this.documentsCollection.isEmpty()) {
                return i18n['search.resultsView.amount.shown.no.results'];
            } else {
                return this.allowIncrement ?
                    i18n['search.resultsView.amount.shown'](1, this.documentsCollection.length, this.documentsCollection.totalResults) :
                    i18n['search.resultsView.amount.shown.no.increment'](this.resultsStep, this.documentsCollection.totalResults);
            }
        },

        reloadMarkers: function () {
            this.clearMarkers();
            this.fetchDocumentCollection();
        },

        clearMarkers: function () {
            this.documentsCollection.reset();
            this.$('.map-results-count').empty();
            this.mapResultsView.clearMarkers(true);
            this.markers = [];
        },

        getIcon: function () {
            const locationField = _.findWhere(this.locationFields, {displayName: this.model.get('field')});
            return this.mapResultsView.getIcon(locationField.iconName, locationField.iconColor, locationField.markerColor);
        },

        toggleLoading: function () {
            if (this.$loadingSpinner) {
                this.$loadMoreButton.prop('disabled', this.documentsCollection.length === this.documentsCollection.totalResults || this.model.get('loading') || !this.model.get('field'));
                this.$loadingSpinner.toggleClass('hide', !this.model.get('loading'));
            }
        },

        getFetchOptions: function (selectedField) {
            const locationField = _.findWhere(this.locationFields, {displayName: selectedField});

            const latitudeFieldsInfo = configuration().fieldsInfo[locationField.latitudeField];
            const longitudeFieldsInfo = configuration().fieldsInfo[locationField.longitudeField];

            const latitudesFieldsString = latitudeFieldsInfo.names.join(':');
            const longitudeFieldsString = longitudeFieldsInfo.names.join(':');

            const exists = 'EXISTS{}:' + latitudesFieldsString + ' AND EXISTS{}:' + longitudeFieldsString;

            const newFieldText = this.queryModel.get('fieldText') ? this.queryModel.get('fieldText') + ' AND ' + exists : exists;

            return {
                data: {
                    text: this.queryModel.get('queryText'),
                    start: this.allowIncrement ? this.documentsCollection.length + 1 : 1,
                    max_results: this.allowIncrement ? this.documentsCollection.length + this.resultsStep : this.resultsStep,
                    indexes: this.queryModel.get('indexes'),
                    field_text: newFieldText,
                    min_date: this.queryModel.get('minDate'),
                    max_date: this.queryModel.get('maxDate'),
                    sort: 'relevance',
                    summary: 'context',
                    queryType: 'MODIFIED'
                },
                remove: false,
                reset: false
            };
        },

        fetchDocumentCollection: function () {
            const selectedField = this.model.get('field');

            if (selectedField) {
                this.model.set('loading', true);
            } else {
                this.model.set('loading', false);
                this.toggleLoading();
                return;
            }

            const options = this.getFetchOptions(selectedField);

            if (options) {
                this.documentsCollection.fetch(options)
            }
        }
    });
});
