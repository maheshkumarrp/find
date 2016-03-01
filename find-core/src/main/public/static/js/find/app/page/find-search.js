/*
 * Copyright 2014-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

define([
    'js-whatever/js/base-page',
    'backbone',
    'find/app/model/dates-filter-model',
    'parametric-refinement/selected-values-collection',
    'find/app/configuration',
    'find/app/model/indexes-collection',
    'find/app/model/documents-collection',
    'find/app/model/parametric-collection',
    'find/app/model/comparisons/comparison-documents-collection',
    'find/app/page/search/input-view',
    'find/app/page/search/tabbed-search-view',
    'find/app/model/saved-searches/saved-query-collection',
    'find/app/model/saved-searches/saved-snapshot-collection',
    'find/app/util/model-any-changed-attribute-listener',
    'find/app/util/merge-collection',
    'find/app/model/saved-searches/saved-search-model',
    'find/app/model/query-text-model',
    'find/app/model/document-model',
    'find/app/page/search/cluster/cluster2d',
    'find/app/page/search/company-links',
    'find/app/page/search/index-links',
    'find/app/page/search/document/document-detail-view',
    'find/app/util/database-name-resolver',
    'find/app/router',
    'find/app/vent',
    'find/app/util/search-data-util',
    'fieldtext/js/field-text-parser',
    'i18n!find/nls/bundle',
    'underscore',
    'text!find/templates/app/page/find-search.html'
], function(BasePage, Backbone, DatesFilterModel, SelectedParametricValuesCollection, configuration, IndexesCollection, DocumentsCollection, ParametricCollection,
            ComparisonDocumentsCollection, InputView, TabbedSearchView, SavedQueryCollection, SavedSnapshotCollection,
            addChangeListener, MergeCollection, SavedSearchModel, QueryTextModel, DocumentModel, Cluster2d, companyLinks, indexLinks, DocumentDetailView,
            databaseNameResolver, router, vent, searchDataUtil, parser, i18n, _, template) {

    'use strict';

    var CLUSTER = '__cluster__'

    var reducedClasses = 'reverse-animated-container col-md-offset-1 col-lg-offset-2 col-xs-12 col-sm-12 col-md-10 col-lg-8';
    var expandedClasses = 'animated-container col-sm-offset-0 col-md-offset-3 col-lg-offset-3 col-md-6 col-lg-6 col-xs-12 col-sm-12';
    var QUERY_TEXT_MODEL_ATTRIBUTES = ['inputText', 'relatedConcepts'];

    var html = _.template(template)({
        i18n: i18n,
        logoBasePath: '../static-' + configuration().commit + '/img/logos/',
        companyLinks: companyLinks,
        indexLinks: indexLinks
    });
    
    function selectInitialIndexes(indexesCollection) {
        var privateIndexes = indexesCollection.reject({domain: 'PUBLIC_INDEXES'});
        var selectedIndexes;

        if (privateIndexes.length > 0) {
            selectedIndexes = privateIndexes;
        } else {
            selectedIndexes = indexesCollection.models;
        }

        return _.map(selectedIndexes, function(indexModel) {
            return indexModel.pick('domain', 'name');
        });
    }

    return BasePage.extend({
        className: 'search-page',

        // Abstract
        ServiceView: null,
        ComparisonView: null,
        documentDetailOptions: null,

        initialize: function() {
            this.savedQueryCollection = new SavedQueryCollection();
            this.savedQueryCollection.fetch({remove: false});

            this.savedSnapshotCollection = new SavedSnapshotCollection();
            this.savedSnapshotCollection.fetch({remove: false});

            this.savedSearchCollection = new MergeCollection([], {
                collections: [this.savedQueryCollection, this.savedSnapshotCollection]
            });

            this.indexesCollection = new IndexesCollection();
            this.parametricCollection = new ParametricCollection();

            this.indexesCollection
                .fetch()
                .done(_.bind(function() {
                    var databases = searchDataUtil.buildIndexes(this.indexesCollection.map(function (model) {
                        return model.pick('domain', 'name');
                    }));

                    this.parametricCollection.fetch({
                        data: {
                            databases: databases,
                            queryText: '*',
                            fieldText: ''
                        }
                    });
                }, this));
            
            this.selectedTabModel = new Backbone.Model({
                selectedSearchCid: null
            });

            // Model representing search bar text and related concepts
            this.searchModel = new QueryTextModel();

            // Model mapping saved search cids to query state
            this.queryStates = new Backbone.Model();

            this.clusterView = new Cluster2d({
                searchModel: this.searchModel
            });

            // Map of saved search cid to ServiceView
            this.serviceViews = {};

            this.listenTo(this.selectedTabModel, 'change', this.selectContentView);

            this.listenTo(this.searchModel, 'change', function() {
                // Bind search model to routing
                vent.navigate(this.generateURL(), {trigger: false});

                if (this.searchModel.get('inputText')) {
                    this.expandedState();
                }

                // Create a tab if the user has run a search but has no open tabs
                if (this.selectedTabModel.get('selectedSearchCid') === null && this.searchModel.get('inputText')) {
                    this.createNewTab(this.searchModel.get('inputText'));
                }
            });

            this.listenTo(this.savedSearchCollection, 'add', function (model) {
                if (this.selectedTabModel.get('selectedCid') === null) {
                    this.selectedTabModel.set('selectedCid', model.cid);
                }
            });

            this.listenTo(this.savedSearchCollection, 'remove', function(savedSearch) {
                var cid = savedSearch.cid;
                this.serviceViews[cid].view.remove();
                this.queryStates.unset(cid);
                delete this.serviceViews[cid];

                if (this.selectedTabModel.get('selectedSearchCid') === cid) {
                    var lastModel = this.savedQueryCollection.last();

                    if (lastModel) {
                        this.selectedTabModel.set('selectedSearchCid', lastModel.cid);
                    } else {
                        // If the user closes their last tab, run a search for *
                        this.createNewTab();
                    }
                }
            });

            this.inputView = new InputView({model: this.searchModel});

            this.tabView = new TabbedSearchView({
                savedSearchCollection: this.savedSearchCollection,
                model: this.selectedTabModel,
                queryStates: this.queryStates
            });

            this.listenTo(this.tabView, 'startNewSearch', this.createNewTab);

            router.on('route:emptySearch', this.reducedState, this);

            // Bind routing to search model
            router.on('route:search', function(text) {
                this.removeDocumentDetailView();

                this.searchModel.set({
                    inputText: text || '',
                    relatedConcepts: []
                });

                this.$('.service-view-container').addClass('hide');
                this.$('.query-service-view-container').removeClass('hide');
            }, this);

            router.on('route:documentDetail', function () {
                this.expandedState();
                this.$('.service-view-container').addClass('hide');
                this.$('.document-detail-service-view-container').removeClass('hide');

                this.removeDocumentDetailView();

                var options = this.documentDetailOptions.apply(this, arguments);
                this.populateDocumentModelForDetailView(options);
            }, this);
        },

        render: function() {
            this.$el.html(html);

            this.inputView.setElement(this.$('.input-view-container')).render();
            this.tabView.setElement(this.$('.search-tabs-container')).render();

            if (this.selectedTabModel.get('selectedSearchCid') === null) {
                this.reducedState();
            } else {
                this.expandedState();
            }

            _.each(this.serviceViews, function(data) {
                this.$('.query-service-view-container').append(data.view.$el);
                data.view.render();
            }, this);


            this.$('.query-service-view-container').append(this.clusterView.$el);
            this.clusterView.render();

            this.selectContentView();

            this.$el.on('click', '.suggested-query', _.bind(this.onSuggestedQuery, this))
            this.$el.on('click', '.link-2dcluster', _.bind(this.selectClusterMap, this))
        },

        selectClusterMap: function() {
            this.searchModel.set('selectedSearchCid', CLUSTER)
        },

        onSuggestedQuery: function(evt){
            var info = $(evt.currentTarget).data();

            this.searchModel.set({
                selectedSearchCid: null,
                inputText: info.queryText || '*',
                relatedConcepts: []
            });

            var sel = this.searchModel.get('selectedSearchCid');
            var view = this.serviceViews[sel].view;

            if (info.indexes) {
                var indexes = info.indexes.split(';');
                view.queryModel.set('indexes', indexes, {quiet: !!info.match})
                view.indexesView.selectedDatabasesCollection.reset(indexes.map(function(name){
                    return { domain: '', name: name }
                }), {
                    quiet: !!info.match
                })
            }

            if (info.dateRange) {
                view.dateView.datesFilterModel.set('dateRange', info.dateRange)
            }

            if (info.match) {
                view.indexesView.updateCheckedOptions()
                view.parametricView.selectedParametricValues.reset(
                    info.match.split(';').map(function(str){
                        var pair = str.split('=');
                        return {field: pair[0], value: pair[1]}
                    })
                )
            }
        },

        createNewTab: function(queryText) {
            var newSearch = new SavedSearchModel({
                queryText: queryText || '*',
                relatedConcepts: [],
                title: i18n['search.newSearch'],
                type: SavedSearchModel.Type.QUERY
            });

            this.savedQueryCollection.add(newSearch);
            this.selectedTabModel.set('selectedSearchCid', newSearch.cid);
        },

        selectContentView: function() {
            var cid = this.selectedTabModel.get('selectedSearchCid');

            _.each(this.serviceViews, function(data) {
                data.view.$el.addClass('hide');
                this.stopListening(data.queryTextModel);
            }, this);

            if (this.searchChangeCallback) {
                this.stopListening(this.searchModel, 'change', this.searchChangeCallback);
                this.searchChangeCallback = null;
            }

            this.clusterView.$el.addClass('hide')

            if (cid === CLUSTER) {
                this.$('.nav.saved-search-tabs-list > li').removeClass('active');
                this.clusterView.$el.removeClass('hide')
            }
            else if (cid) {
                var viewData;
                var savedSearchModel = this.savedSearchCollection.get(cid);
                var searchType = savedSearchModel.get('type');

                if (this.serviceViews[cid]) {
                    viewData = this.serviceViews[cid];
                } else {
                    var queryTextModel = new QueryTextModel(savedSearchModel.toQueryTextModelAttributes());

                    var documentsCollection = searchType === SavedSearchModel.Type.QUERY ? new DocumentsCollection() : new ComparisonDocumentsCollection([], {
                        stateMatchIds: savedSearchModel.get('stateTokens')
                    });

                    var queryState = {
                        queryTextModel: queryTextModel,
                        datesFilterModel: new DatesFilterModel(savedSearchModel.toDatesFilterModelAttributes()),
                        selectedParametricValues: new SelectedParametricValuesCollection(savedSearchModel.toSelectedParametricValues())
                    };

                    var initialSelectedIndexes;
                    var savedSelectedIndexes = savedSearchModel.toSelectedIndexes();

                    // TODO: Check if the saved indexes still exist?
                    if (savedSelectedIndexes.length === 0 && searchType === SavedSearchModel.Type.QUERY) {
                        if (this.indexesCollection.isEmpty()) {
                            initialSelectedIndexes = [];

                            this.listenToOnce(this.indexesCollection, 'sync', function() {
                                queryState.selectedIndexes.set(selectInitialIndexes(this.indexesCollection));
                            });
                        } else {
                            initialSelectedIndexes = selectInitialIndexes(this.indexesCollection);
                        }
                    } else {
                        initialSelectedIndexes = savedSelectedIndexes;
                    }

                    queryState.selectedIndexes = new IndexesCollection(initialSelectedIndexes);

                    this.queryStates.set(cid, queryState);

                    this.serviceViews[cid] = viewData = {
                        queryTextModel: queryTextModel,
                        documentsCollection: documentsCollection,
                        view: new this.ServiceView({
                            indexesCollection: this.indexesCollection,
                            documentsCollection: documentsCollection,
                            selectedTabModel: this.selectedTabModel,
                            savedSearchCollection: this.savedSearchCollection,
                            savedSnapshotCollection: this.savedSnapshotCollection,
                            savedQueryCollection: this.savedQueryCollection,
                            queryState: queryState,
                            savedSearchModel: savedSearchModel,
                            comparisonSuccessCallback: _.bind(this.comparisonSuccessCallback, this),
                            parametricCollection: this.parametricCollection
                        })
                    };

                    this.$('.query-service-view-container').append(viewData.view.$el);
                    viewData.view.render();
                }

                if (searchType === SavedSearchModel.Type.QUERY) {
                    // Bind the tab content to the search bar
                    addChangeListener(this, viewData.queryTextModel, QUERY_TEXT_MODEL_ATTRIBUTES, function() {
                        this.searchModel.set(viewData.queryTextModel.pick(QUERY_TEXT_MODEL_ATTRIBUTES));
                    });

                    this.searchModel.set(viewData.queryTextModel.pick(QUERY_TEXT_MODEL_ATTRIBUTES));

                    // Bind the search bar to the tab content
                    this.searchChangeCallback = addChangeListener(this, this.searchModel, QUERY_TEXT_MODEL_ATTRIBUTES, function() {
                        viewData.queryTextModel.set(this.searchModel.pick(QUERY_TEXT_MODEL_ATTRIBUTES));
                    });
                } else {
                    // Don't bind the search bar to the tab content for snapshots; clear the search bar instead
                    this.searchModel.set({
                        inputText: '',
                        relatedConcepts: []
                    });

                    this.searchChangeCallback = addChangeListener(this, this.searchModel, QUERY_TEXT_MODEL_ATTRIBUTES, function() {
                        this.createNewTab(this.searchModel.get('inputText'));
                    });
                }

                viewData.view.$el.removeClass('hide');
            }
        },

        generateURL: function() {
            var inputText = this.searchModel.get('inputText');

            if(inputText) {
                return 'find/search/query/' + encodeURIComponent(inputText);
            } else {
                return 'find/search/query';
            }
        },

        // Run fancy animation from large central search bar to main search page
        expandedState: function() {
            this.$('.find').removeClass(reducedClasses).addClass(expandedClasses);

            this.$('.service-view-container').addClass('hide');
            this.$('.query-service-view-container').removeClass('hide');
            this.$('.app-logo').addClass('hide');
            this.$('.hp-logo-footer').addClass('hide');
            this.$('.suggested-query-row').addClass('hide')

            this.removeDocumentDetailView();

            // TODO: somebody else needs to own this
            $('.find-banner-container').removeClass('reduced navbar navbar-static-top').find('>').removeClass('hide');
            $('.container-fluid, .find-logo-small').removeClass('reduced');
            this.inputView.unFocus();
        },

        // Set view to initial state (large central search bar)
        reducedState: function() {
            this.$('.find').removeClass(expandedClasses).addClass(reducedClasses);

            this.$('.service-view-container').addClass('hide');
            this.$('.app-logo').removeClass('hide');
            this.$('.hp-logo-footer').removeClass('hide');
            this.$('.suggested-query-row').removeClass('hide');

            this.removeDocumentDetailView();

            // TODO: somebody else needs to own this
            $('.find-banner-container').addClass('reduced navbar navbar-static-top').find('>').addClass('hide');
            $('.container-fluid, .find-logo-small').addClass('reduced');
            this.inputView.focus();
        },

        // If we already have the document model in one of our collections, then don't bother fetching it
        populateDocumentModelForDetailView: function (options) {
            new DocumentModel().fetch({
                data: {
                    reference: options.reference,
                    database: options.database
                },
                success: _.bind(this.renderDocumentDetail, this)
            });
        },

        renderDocumentDetail: function(model) {
            this.documentDetailView = new DocumentDetailView({
                backUrl: this.generateURL(),
                model: model,
                indexesCollection: this.indexesCollection
            });

            this.$('.document-detail-service-view-container').append(this.documentDetailView.$el);
            this.documentDetailView.render();
        },

        removeDocumentDetailView: function() {
            if (this.documentDetailView) {
                this.documentDetailView.remove();
                this.stopListening(this.documentDetailView);
                this.documentDetailView = null;
            }
        },

        clearComparison: function() {
            if(this.comparisonView) {
                // Setting the element to nothing prevents the containing element from being removed when the view is removed
                this.comparisonView.setElement();
                this.comparisonView.remove();
                this.stopListening(this.comparisonView);
                this.comparisonView = null;
            }
        },

        comparisonSuccessCallback: function(model, searchModels) {
            this.clearComparison();

            this.$('.service-view-container').addClass('hide');
            this.$('.comparison-service-view-container').removeClass('hide');

            this.comparisonView = new this.ComparisonView({
                model: model,
                searchModels: searchModels,
                escapeCallback: _.bind(this.comparisonEscapeCallback, this)
            });

            this.comparisonView.setElement(this.$('.comparison-service-view-container')).render();
        },

        comparisonEscapeCallback: function() {
            this.clearComparison();

            this.$('.service-view-container').addClass('hide');
            this.$('.query-service-view-container').removeClass('hide');
        }
    });
});
