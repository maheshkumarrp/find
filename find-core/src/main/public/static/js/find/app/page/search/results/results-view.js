define([
    'backbone',
    'jquery',
    'underscore',
    'find/app/model/document-model',
    'find/app/model/promotions-collection',
    'find/app/model/similar-documents-collection',
    'find/app/util/popover',
    'find/app/util/view-server-client',
    'find/app/util/document-mime-types',
    'js-whatever/js/escape-regex',
    'text!find/templates/app/page/search/results-popover.html',
    'text!find/templates/app/page/search/popover-message.html',
    'text!find/templates/app/page/search/results/results-view.html',
    'text!find/templates/app/page/search/results/results-container.html',
    'text!find/templates/app/page/colorbox-controls.html',
    'text!find/templates/app/page/loading-spinner.html',
    'text!find/templates/app/page/view/media-player.html',
    'text!find/templates/app/page/view/view-document.html',
    'text!find/templates/app/page/search/results/entity-label.html',
    'moment',
    'i18n!find/nls/bundle',
    'i18n!find/nls/indexes',
    'colorbox'
], function(Backbone, $, _, DocumentModel, PromotionsCollection, SimilarDocumentsCollection, popover,
            viewClient, documentMimeTypes, escapeRegex, popoverTemplate, popoverMessageTemplate, template, resultsTemplate,
            colorboxControlsTemplate, loadingSpinnerTemplate, mediaPlayerTemplate, viewDocumentTemplate, entityTemplate,
            moment, i18n, i18n_indexes) {

    /** Whitespace OR character in set bounded by [] */
    var boundaryChars = '\\s|[,.-:;?\'"!\\(\\)\\[\\]{}]';
    /** Start of input OR boundary chars */
    var startRegex = '(^|' + boundaryChars + ')';
    /** End of input OR boundary chars */
    var endRegex = '($|' + boundaryChars + ')';

    var mediaTypes = ['audio', 'video'];

    var getContentTypeClass = function(model) {
        var contentType = model.get('contentType') || '';

        var matchedType = _.find(documentMimeTypes, function(mimeType) {
            return Boolean(_.find(mimeType.typeRegex, function(regex) {
                return regex().test(contentType);
            }));
        });

        return matchedType.className;
    };

    var $window = $(window);
    var SIZE = '90%';

    var onResize = function() {
        $.colorbox.resize({width: SIZE, height: SIZE});
    };

    return Backbone.View.extend({
        //to be overridden
        generateErrorMessage: _.noop,

        template: _.template(template),
        loadingTemplate: _.template(loadingSpinnerTemplate)({i18n: i18n, large: true}),
        resultsTemplate: _.template(resultsTemplate),
        popoverMessageTemplate: _.template(popoverMessageTemplate),
        messageTemplate: _.template('<div class="result-message span10"><%-message%> </div>'),
        errorTemplate: _.template('<li class="error-message span10"><span><%-feature%>: </span><%-error%></li>'),
        mediaPlayerTemplate: _.template(mediaPlayerTemplate),
        popoverTemplate: _.template(popoverTemplate),
        entityTemplate: _.template(entityTemplate),
        viewDocumentTemplate: _.template(viewDocumentTemplate),

        events: {
            'click .entity-text': function(e) {
                var $target = $(e.target);
                var queryText = $target.attr('data-title');
                this.queryModel.set('queryText', queryText);
            }
        },

        initialize: function(options) {
            _.bindAll(this, 'handlePopover');

            this.queryModel = options.queryModel;
            this.entityCollection = options.entityCollection;
            this.indexesCollection = options.indexesCollection;

            this.documentsCollection = options.documentsCollection;
            this.promotionsCollection = new PromotionsCollection();

            this.listenTo(this.queryModel, 'change refresh', function() {
                if (!_.isEmpty(this.queryModel.get('indexes'))) {
                    this.documentsCollection.fetch({
                        data: {
                            auto_correct: this.queryModel.get('autoCorrect'),
                            text: this.queryModel.get('queryText'),
                            max_results: 30,
                            summary: 'context',
                            index: this.queryModel.get('indexes'),
                            field_text: this.queryModel.get('fieldText'),
                            min_date: this.queryModel.getIsoDate('minDate'),
                            max_date: this.queryModel.getIsoDate('maxDate'),
                            sort: this.queryModel.get('sort')
                        },
                        reset: false
                    }, this);

                    // TODO: Move out of if statement when HOD allows fetching promotions without indexes
                    this.promotionsCollection.fetch({
                        data: {
                            auto_correct: this.queryModel.get('autoCorrect'),
                            text: this.queryModel.get('queryText'),
                            max_results: 30, // TODO maybe less?
                            summary: 'context',
                            index: this.queryModel.get('indexes'),
                            field_text: this.queryModel.get('fieldText'),
                            min_date: this.queryModel.getIsoDate('minDate'),
                            max_date: this.queryModel.getIsoDate('maxDate'),
                            sort: this.queryModel.get('sort')
                        },
                        reset: false
                    }, this);

                    this.promotionsFinished = false;
                    this.$('.main-results-content .promotions').empty();

                    this.resultsFinished = false;
                    this.$loadingSpinner.removeClass('hide');
                    this.toggleError(false);
                    this.$('.main-results-content .error .error-list').empty();
                    this.$('.main-results-content .results').empty();
                } else {
                    this.$loadingSpinner.addClass('hide');
                    this.$('.main-results-content .results').html(this.messageTemplate({message: i18n_indexes["search.error.noIndexes"]}));
                }
            });
        },

        clearLoadingSpinner: function() {
            if (this.resultsFinished && this.promotionsFinished) {
                this.$loadingSpinner.addClass('hide');
            }
        },

        render: function() {
            this.$el.html(this.template({i18n:i18n}));

            this.$loadingSpinner = $(this.loadingTemplate);

            this.$el.prepend(this.$loadingSpinner);

            /*promotions content content*/
            this.listenTo(this.promotionsCollection, 'add', function(model) {
                this.formatResult(model, true);
            });

            this.listenTo(this.promotionsCollection, 'sync', function() {
                this.promotionsFinished = true;
                this.clearLoadingSpinner();
            });

            this.listenTo(this.promotionsCollection, 'error', function(collection, xhr) {
                this.promotionsFinished = true;
                this.clearLoadingSpinner();

                this.$('.main-results-content .promotions').append(this.handleError(i18n['app.feature.promotions'], xhr));
            });

            /*main results content*/
            this.listenTo(this.documentsCollection, 'add', function(model) {
                this.formatResult(model, false);
            });

            this.listenTo(this.documentsCollection, 'sync', function(model, resp, opts) {
                this.resultsFinished = true;
                this.clearLoadingSpinner();

                if (this.searchMoreCollection) {
                    this.searchMoreCollection.destroy && this.searchMoreCollection.destroy();
                    this.searchMoreCollection = null;
                }

                if (this.documentsCollection.isEmpty()) {
                    var params = opts.data;
                    var extra = _.reject(this.indexesCollection.pluck('id'), function(val){ return _.contains(params.index, val)});

                    var msg = extra.length ? i18n["search.noResults.searchMore"] : i18n["search.noResults"];
                    var $resultsEl = $(this.messageTemplate({message: msg})).appendTo(this.$('.main-results-content .results'));

                    if (extra.length) {
                        var sc = this.searchMoreCollection = this.documentsCollection.clone();
                        var messages = [];

                        var recursiveFetch = _.bind(function(){
                            var nextIndex = extra.pop();

                            nextIndex && this.searchMoreCollection.fetch({
                                data: _.extend({}, params, {
                                    auto_correct: false,
                                    summary: 'off',
                                    index: nextIndex,
                                    count_only: true
                                }),
                                success: _.bind(function(models, resp){
                                    if (sc === this.searchMoreCollection) {
                                        resp.totalResults && messages.push(i18n["search.noResults.searchIndex"](nextIndex.replace( /.*:/, ''), resp.totalResults));

                                        var $newEl = $(this.messageTemplate({
                                            message: messages.length ? i18n["search.noResults.searchFound"](
                                                messages.join(', '),
                                                extra.length ? ' ...' : ''
                                            ): extra.length ? i18n["search.noResults.searchMore"] : i18n["search.noResults"]}
                                        ));
                                        $resultsEl.replaceWith($newEl);
                                        $resultsEl = $newEl;

                                        extra.length && recursiveFetch();
                                    }
                                }, this)
                            })
                        }, this);

                        recursiveFetch()
                    }
                }
            });

            this.listenTo(this.documentsCollection, 'error', function(collection, xhr) {
                this.resultsFinished = true;
                this.clearLoadingSpinner();

                this.$('.main-results-content .results').append(this.handleError(i18n['app.feature.search'], xhr));
            });

            this.listenTo(this.entityCollection, 'reset', function() {
                if (!this.entityCollection.isEmpty()) {
                    this.documentsCollection.each(function(document) {
                        var summary = this.addLinksToSummary(document.get('summary'));

                        this.$('[data-reference="' + document.get('reference') + '"] .result-summary').html(summary);
                    }, this);

                    this.promotionsCollection.each(function(document) {
                        var summary = this.addLinksToSummary(document.get('summary'));

                        this.$('[data-reference="' + document.get('reference') + '"] .result-summary').html(summary);
                    }, this);
                }
            });

            /*colorbox fancy button override*/
            $('#colorbox').append(_.template(colorboxControlsTemplate));
            $('.nextBtn').on('click', this.handleNextResult);
            $('.prevBtn').on('click', this.handlePrevResult);
        },

        handlePrevResult: function() {
            $.colorbox.prev();
        },

        handleNextResult: function() {
            $.colorbox.next();
        },

        colorboxArguments: function(options) {
            var args = {
                current: '{current} of {total}',
                height: '70%',
                iframe: false,
                rel: 'results',
                width: '70%',
                onClosed: function() {
                    $window.off('resize', onResize);
                },
                onComplete: _.bind(function() {
                    $('#cboxPrevious, #cboxNext').remove(); //removing default colorbox nav buttons

                    var $viewServerPage = $('.view-server-page');

                    $viewServerPage.on('load', function() {
                        $('.view-server-loading-indicator').addClass('hidden');
                        $('.view-server-page').removeClass('hidden');
                    });

                    // Adding the source attribute after the colorbox has loaded prevents the iframe from loading
                    // a very quick response (such as an error) before the listener is attached
                    $viewServerPage.attr("src", options.href);

                    $window.resize(onResize);
                }, this)
            };

            var contentType = options.model.get('contentType') || '';

            var media = _.find(mediaTypes, function(mediaType) {
                return contentType.indexOf(mediaType) === 0;
            });

            var url = options.model.get('url');

            if (media && url) {
                args.html = this.mediaPlayerTemplate({
                    media: media,
                    url: url,
                    offset: options.model.get('offset')
                });
            } else {
                args.html = this.viewDocumentTemplate({
                    src: options.href,
                    i18n: i18n,
                    model: options.model,
                    arrayFields: DocumentModel.ARRAY_FIELDS,
                    dateFields: DocumentModel.DATE_FIELDS,
                    fields: ['domain', 'index', 'reference']
                });
            }

            return args;
        },

        formatResult: function(model, isPromotion) {
            var reference = model.get('reference');
            var summary = this.addLinksToSummary(model.get('summary'));

            var href;

            if (model.get('promotionType') === 'STATIC_CONTENT_PROMOTION') {
                href = viewClient.getStaticContentPromotionHref(reference);
            } else {
                href = viewClient.getHref(reference, model.get('index'), model.get('domain'));
            }

            var $newResult = $(this.resultsTemplate({
                i18n: i18n,
                title: model.get('title'),
                reference: reference,
                href: href,
                summary: summary,
                promotion: isPromotion,
                thumbnailUrl: model.get('thumbnailUrl'),
                date: model.has('date') ? model.get('date').fromNow() : null,
                contentType: getContentTypeClass(model)
            }));

            if (isPromotion) {
                this.$('.main-results-content .promotions').append($newResult);
            } else {
                this.$('.main-results-content .results').append($newResult);
            }

            $newResult.find('.result-header').colorbox(this.colorboxArguments({model: model, href: href}));

            $newResult.find('.dots').click(function (e) {
                e.preventDefault();
                $newResult.find('.result-header').trigger('click'); //dot-dot-dot triggers the colorbox event
            });

            popover($newResult.find('.similar-documents-trigger'), 'focus', this.handlePopover);
        },

        addLinksToSummary: function(summary) {
            // Find highlighted query terms
            var queryTextRegex = /<Find-IOD-QueryText-Placeholder>(.*?)<\/Find-IOD-QueryText-Placeholder>/g;
            var queryText = [];
            var resultsArray;
            while ((resultsArray = queryTextRegex.exec(summary)) !==null) {
                queryText.push(resultsArray[1]);
            }

            // Protect us from XSS (but leave injected highlight tags alone)
            var otherText = summary.split(/<Find-IOD-QueryText-Placeholder>.*?<\/Find-IOD-QueryText-Placeholder>/);
            var escapedSummaryElements = [];
            escapedSummaryElements.push(_.escape(otherText[0]));
            for (var i = 0; i < queryText.length; i++) {
                escapedSummaryElements.push('<span class="search-text">' + _.escape(queryText[i]) + '</span>');
                escapedSummaryElements.push(_.escape(otherText[i + 1]));
            }
            var escapedSummary = escapedSummaryElements.join('');

            // Create an array of the entity titles, longest first
            var entities = this.entityCollection.map(function(entity) {
                return {
                    text: entity.get('text'),
                    id: _.uniqueId('Find-IOD-Entity-Placeholder')
                };
            }).sort(function(a, b) {
                return b.text.length - a.text.length;
            });

            // Loop through entities, replacing each with a unique id to prevent later replaces finding what we've
            // changed here and messing things up badly
            _.each(entities, function(entity) {
                escapedSummary = this.replaceBoundedText(escapedSummary, entity.text, entity.id);
            }, this);

            // Loop through entities again, replacing text with labels
            _.each(entities, function(entity) {
                escapedSummary = this.replaceTextWithLabel(escapedSummary, entity.id, {
                    elementType: 'a',
                    replacement: entity.text,
                    elementClasses: 'entity-text entity-label label clickable'
                })
            }, this);

            return escapedSummary;
        },

        /**
         * Finds a string that's bounded by [some regex stuff] and replaces it with something else.
         * Used as part 1 of highlighting text in result summaries.
         * @param text  The text to search in
         * @param textToFind  The text to search for
         * @param replacement  What to replace textToFind with
         * @returns {string|XML|void}  `text`, but with replacements made
         */
        replaceBoundedText: function(text, textToFind, replacement) {
            return text.replace(new RegExp(startRegex + escapeRegex(textToFind) + endRegex, 'gi'), '$1' + replacement + '$2');
        },


        /**
         * @typedef EntityTemplateOptions
         * @property elementType {string} The html element type the text should be in
         * @property replacement {string} The text of the element
         * @property elementClasses {string} The classes to apple to the html element defined in elementType
         */
        /**
         * Finds a string and replaces it with an HTML label.
         * Used as part 2 of highlighting text in results summaries.
         * @param {string} text  The text to search in
         * @param {string} textToFind  The text to replace with a label
         * @param {EntityTemplateOptions} templateOptions A hash of options to configure the template
         * @returns {string|XML|*}  `text`, but with replacements made
         */
        replaceTextWithLabel: function(text, textToFind, templateOptions) {
            var label = this.entityTemplate(templateOptions);

            return text.replace(new RegExp(startRegex + textToFind + endRegex, 'g'), '$1' + label + '$2');
        },

        handlePopover: function($content, $target) {
            var collection = new SimilarDocumentsCollection([], {
                indexes: this.queryModel.get('indexes'),
                reference: $target.closest('[data-reference]').attr('data-reference')
            });

            collection.fetch({
                error: _.bind(function() {
                    $content.html(this.popoverMessageTemplate({message: i18n['search.similarDocuments.error']}));
                }, this),
                success: _.bind(function() {
                    if (collection.isEmpty()) {
                        $content.html(this.popoverMessageTemplate({message: i18n['search.similarDocuments.none']}));
                    } else {
                        $content.html('<ul class="list-unstyled"></ul>');
                        _.each(collection.models, function(model) {
                            var listItem = $(this.popoverTemplate({
                                title: model.get('title'),
                                summary: model.get('summary').trim().substring(0, 100) + '...'
                            }));
                            var reference = model.get('reference');
                            var href;
                            if (model.get('promotionType') === 'STATIC_CONTENT_PROMOTION') {
                                href = viewClient.getStaticContentPromotionHref(reference);
                            } else {
                                href = viewClient.getHref(reference, model.get('index'), model.get('domain'));
                            }
                            $(listItem).find('a').colorbox(this.colorboxArguments({model: model, href: href}));
                            $content.find('ul').append(listItem);
                        }, this);
                    }
                }, this)
            });
        },

        handleError: function(feature, xhr) {
            this.toggleError(true);
            var message = this.generateErrorMessage(xhr);

            var messageTemplate = this.errorTemplate({feature: feature, error: message});
            this.$('.main-results-content .error .error-list').append(messageTemplate);
        },

        toggleError: function(on) {
            this.$('.main-results-content .promotions').toggleClass('hide', on);
            this.$('.main-results-content .results').toggleClass('hide', on);
            this.$('.main-results-content .error').toggleClass('hide', !on);
        }
    });
});
