/*
 * Copyright 2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

define([
    'js-whatever/js/substitution'
], function(substitution) {
    'use strict';

    return substitution({
        'about.app.version': 'Version',
        'about.copyright': "Find © Copyright 2014-2017 Hewlett Packard Enterprise Development Company, L.P.",
        'about.foss': 'FOSS Acknowledgements',
        'about.lib.name': 'Library Name',
        'about.lib.version': 'Version',
        'about.lib.licence': 'License',
        'about.search': 'Search',
        'about.tagLine': 'Handcrafted in Cambridge.',
        'about.versionString': '{0} commit {1}',
        'app.about': 'About',
        'app.apply': 'Apply',
        'app.backToSearch': 'Back to search',
        'app.cancel': 'Cancel',
        'app.compare': 'Compare',
        'app.conceptBoxPlaceholder': 'Add a Concept',
        'app.delete': 'Delete\u2026',
        'app.button.delete': 'Delete',
        'app.exportToCsv': 'Export Results to CSV\u2026',
        'app.exportToCsv.modal.title': 'Select the fields you would like to export',
        'app.button.exportCsv': 'Export CSV',
        'app.from': 'From',
        'app.loading': 'Loading\u2026',
        'app.logout': 'Logout',
        'app.less': "less",
        'app.more': "more",
        'app.name': "HPE Find",
        'app.ok': 'OK',
        'app.rename': 'Rename',
        'app.reset': 'Reset',
        'app.roles': 'Roles',
        'app.seeAll': 'See All',
        'app.seeMore': 'See More',
        'app.search': 'Search',
        'app.searchPlaceholder': 'What do you want to find?',
        'app.settings': 'Settings',
        'app.unfiltered': 'Unfiltered',
        'app.unknown': 'Unknown',
        'app.until': 'Until',
        'app.user': 'User',
        'app.users': 'Users',
        'dashboards': 'Dashboards',
        'dashboards.widget.notFound': 'Widget of type "{0}" could not be found. Please check your configuration and try again',
        'dashboards.widget.lastRefresh.nextRefresh': 'Next refresh at',
        'dashboards.widget.lastRefresh.refreshing': 'Refreshing {0} of {1} widgets',
        'dashboards.widget.lastRefresh.timeLastUpdated': 'Last updated at',
        'dashboards.widget.sunburst.noResults': 'The query did not match any documents',
        'dashboards.widget.sunburst.legend.hiddenValues': 'Too many entries',
        'dashboards.widget.sunburst.legend.noValues': 'No values to display',
        'datepicker.language': 'en',
        'default.title': 'Page Unavailable',
        'default.message': "We can't find the page you requested.",
        'default.button': 'Return to Search',
        'error.message.default': 'An error has occurred.',
        'error.default.contactSupport': 'Please contact support.',
        'error.details': 'Error details: {0}',
        'error.UUID': 'Error UUID: {0}',
        'error.unknown': 'Unknown error.',
        'login.defaultLogin': 'Details for the default login can be found in your config.json file',
        'login.error.auth': 'Please check your username and password.',
        'login.error.connection': 'There was an error authenticating with your Community server. Please check if your Community server is available.',
        'login.error.nonadmin': 'This user does not have admin permissions.',
        'login.important': 'Important',
        'login.infoDefaultLogin': 'This contains a default username (displayed below) and password.',
        'login.infoPasswordCopyPaste': 'You can copy and paste the password into the field below.',
        'login.infoSearchConfig': 'Using your favorite text editor, search config.json for "defaultLogin", in the "login" section.',
        'login.moreInfo': 'More',
        'login.newCredentials': 'Login with new credentials',
        'login.login': 'Login',
        'login.title': 'Login to {0}',
        'old.browser.chrome': 'Latest version of Chrome',
        'old.browser.edge': 'Latest version of Microsoft Edge',
        'old.browser.firefox': 'Latest version of Firefox',
        'old.browser.ie': 'Internet Explorer 11',
        'old.browser.supportedBrowsers': 'Please use one of the following supported browsers:',
        'old.browser.title': 'Browser not supported',
        'old.browser.unsupported': 'It looks like your browser is not supported by this app.',
        'placeholder.hostname': 'hostname',
        'placeholder.ip': 'IP',
        'placeholder.port': 'port',
        'powerpoint.export.single': 'Single slide',
        'powerpoint.export.multiple': 'Multislide',
        'powerpoint.export.labels': 'Labels',
        'powerpoint.export.padding': 'Padding',
        'search.answeredQuestion.question': 'Question: ',
        'search.answeredQuestion.answer': 'Answer: ',
        'search.alsoSearchingFor': 'Also searching for',
        'search.concepts': 'Concepts',
        'search.concepts.empty': 'No concepts selected',
        'search.databases': 'Databases',
        'search.dates': 'Dates',
        'search.dates.timeInterval.CUSTOM': 'Custom',
        'search.dates.timeInterval.WEEK': 'Last Week',
        'search.dates.timeInterval.MONTH': 'Last Month',
        'search.dates.timeInterval.YEAR': 'Last Year',
        'search.dates.timeInterval.NEW': 'Since Last Search',
        'search.dates.timeInterval.new.description': 'Show results since you last used this filter or changed the search',
        'search.document.authors': 'Authors',
        'search.document.contentType': 'Content Type',
        'search.document.sourceType': 'Source Type',
        'search.document.date': 'Date',
        'search.document.dateModified': 'Date Modified',
        'search.document.dateCreated': 'Date Created',
        'search.document.detail.expand': 'Expand Preview',
        'search.document.detail.openOriginal': 'Open Original',
        'search.document.detail.highlightQueryTerms': 'Highlight Query Terms',
        'search.document.detail.tabs.authors': 'Authors',
        'search.document.detail.tabs.location': 'Location',
        'search.document.detail.tabs.location.latitude': 'Latitude',
        'search.document.detail.tabs.location.longitude': 'Longitude',
        'search.document.detail.tabs.metadata': 'Metadata',
        'search.document.detail.tabs.metadata.noAdvanced': 'This document has no advanced metadata fields',
        'search.document.detail.tabs.metadata.showAdvanced': 'Show advanced',
        'search.document.detail.tabs.metadata.hideAdvanced': 'Hide advanced',
        'search.document.detail.tabs.similarDocuments': 'Similar documents',
        'search.document.detail.tabs.similarDates': 'Similar dates',
        'search.document.detail.tabs.similarDates.pickMessage': 'Set a time window using the sliders',
        'search.document.detail.tabs.similarDates.after': 'After Document',
        'search.document.detail.tabs.similarDates.before': 'Before Document',
        'search.document.detail.tabs.similarDates.temporalSummaryHtml': 'Between <strong>{0}</strong> before and <strong>{1}</strong> after the document',
        'search.document.detail.tabs.similarSources': 'Similar sources',
        'search.document.detail.tabs.transcript': 'Transcript',
        'search.document.domain': 'Domain',
        'search.document.openInNewTab': 'Open in New Tab',
        'search.document.reference': 'Reference',
        'search.document.staticContent': 'Static Content',
        'search.document.summary': 'Summary',
        'search.document.title': 'Title',
        'search.document.thumbnail': 'Thumbnail',
        'search.document.thumbnailUrl': 'Thumbnail URL',
        'search.document.url': 'URL',
        'search.document.mmapUrl': 'MMAP URL',
        'search.document.weight': 'Weight',
        'search.editConcept.save': 'Save',
        'search.editConcept.cancelSave': 'Cancel',
        'search.error.promotions': 'An error occurred while retrieving promotions',
        'search.error.relatedConcepts': 'Error: could not retrieve Related Concepts',
        'search.error.parametric': 'An error occurred while retrieving additional filters',
        'search.filters': 'Filters',
        'search.filters.applied': 'Filters applied',
        'search.filters.filter': 'Filter\u2026',
        'search.filters.empty': 'No filters matched',
        'search.filters.removeAll': 'remove all',
        'search.indexes': 'Indexes',
        'search.indexes.all': 'All',
        'search.indexes.publicIndexes': 'Public Indexes',
        'search.indexes.privateIndexes': 'Private Indexes',
        'search.indexes.empty': 'Waiting for Indexes\u2026',
        'search.newSearch': 'New Search',
        'search.numericParametricFields': 'Numeric Parametric Fields',
        'search.numericParametricFields.error': 'Failed to load data',
        'search.numericParametricFields.noValues': 'No values',
        'search.numericParametricFields.noMax': 'No Max',
        'search.numericParametricFields.noMin': 'No Min',
        'search.numericParametricFields.reset': 'Reset',
        'search.numericParametricFields.tooltip': 'Range: {0} \u2013 {1}\nCount: {2}',
        'search.noResults': 'No results found',
        'search.noMoreResults': 'No more results found',
        'search.parametricFilters.modal.empty': 'No parametric values',
        'search.parametricFilters.modal.title': 'Select parametric filters',
        'search.parametricFields': 'Parametric Fields',
        'search.parametric.empty': 'No parametric fields found',
        'search.preview': 'Preview',
        'search.preview.previewMode': 'Preview Mode',
        'search.preview.mmap': 'Explore in MMAP',
        'search.preview.selectDocument': 'Select a document from the list to preview',
        'search.relatedConcepts': 'Related Concepts',
        'search.relatedConcepts.topResults.error': 'An error occurred while fetching top results',
        'search.relatedConcepts.topResults.none': 'No top results found',
        'search.relatedConcepts.notLoading': 'The list of indexes has not yet been retrieved',
        'search.relatedConcepts.none': 'There are no related concepts',
        'search.results': 'results',
        'search.results.pagination.of': 'of',
        'search.results.pagination.showing': 'Showing',
        'search.results.pagination.to': 'to',
        'search.resultsSort': 'Sort',
        'search.resultsSort.date': 'by date',
        'search.resultsSort.relevance': 'by relevance',
        'search.resultsView.list': 'List',
        'search.resultsView.topic-map': 'Topic Map',
        'search.resultsView.sunburst': 'Sunburst',
        'search.resultsView.sunburst.error.noParametricValues': 'Could not display Sunburst View: your search returned no parametric values',
        'search.resultsView.sunburst.error.query': 'Error: could not display Sunburst View',
        'search.resultsView.sunburst.error.noDependentParametricValues': 'There are too many parametric fields to display in Sunburst View',
        'search.resultsView.sunburst.error.noSecondFieldValues': 'There are no documents with values for both fields. Showing results for only first field.',
        'search.resultsView.sunburst.others': 'Others',
        'search.resultsView.sunburst.breakdown.by': 'Breakdown by {0}',
        'search.resultsView.map': 'Map',
        'search.resultsView.map.show.more': 'Show More',
        'search.resultsView.table': 'Table',
        'search.resultsView.table.count': 'Count',
        'search.resultsView.table.error.query': 'Error: could not display Table View',
        'search.resultsView.table.error.noDependentParametricValues': 'There are too many parametric fields to display in Table View',
        'search.resultsView.table.info': 'Showing _START_ to _END_ of _TOTAL_ entries', // see https://datatables.net/reference/option/language.info
        'search.resultsView.table.infoFiltered': '(filtered from _MAX_ total entries)', // see https://datatables.net/reference/option/language.infoFiltered
        'search.resultsView.table.lengthMenu': 'Show _MENU_ entries', // see https://datatables.net/reference/option/language.lengthMenu
        'search.resultsView.table.next': 'Next',
        'search.resultsView.table.noneHeader': 'NONE',
        'search.resultsView.table.previous': 'Previous',
        'search.resultsView.table.searchInResults': 'Search in Results',
        'search.resultsView.table.zeroRecords': 'No matching records found',
        'search.resultsView.table.breakdown.by': 'Breakdown by {0}',
        'search.resultsView.amount.shown': 'Showing <strong>{0}</strong> to <strong>{1}</strong> of <strong>{2}</strong> results',
        'search.resultsView.amount.shown.no.increment': 'Showing the top <strong>{0}</strong> results of <strong>{1}</strong>',
        'search.resultsView.amount.shown.no.results': 'There are no results with the location field selected',
        'search.answeredQuestion': 'Answered question',
        'search.answeredQuestion.systemName': 'Answered by {0}',
        'search.promoted': 'Promoted',
        'search.savedSearchControl.save': 'Save',
        'search.savedSearchControl.openEdit.create': 'Save query',
        'search.savedSearchControl.openEdit.edit': 'Save as query',
        'search.savedSearchControl.update': 'Save',
        'search.savedSearchControl.nameSearch': 'Name your search',
        'search.savedSearchControl.searchType.QUERY': 'Query',
        'search.savedSearchControl.searchType.SNAPSHOT': 'Snapshot',
        'search.savedSearchControl.cancelSave': 'Cancel',
        'search.savedSearchControl.error': 'Error: could not save search',
        'search.savedSearchControl.error.timeout': 'Timeout while trying to save current search. Try refining your query',
        'search.savedSearchControl.rename': 'Rename',
        'search.savedSearchControl.openAsSearch': 'Open as Query',
        'search.savedSearchControl.titleBlank': 'Title must not be blank',
        'search.savedSearchControl.nameAlreadyExists': 'Search with this name already exists',
        'search.savedSearchControl.nameEmptyOrWhitespace': 'Name must contain at least one printable character',
        'search.suggest.title': 'Similar results to "{0}"',
        'search.topicMap.empty': 'No topics found for this query',
        'search.topicMap.error': 'Error: could not retrieve topics for this search',
        'search.topicMap.fast': 'Fast',
        'search.topicMap.accurate': 'Accurate',
        'search.sunburst.title': 'Parametric Distribution',
        'search.sunburst.fieldPlaceholder.first': 'Select a field',
        'search.sunburst.fieldPlaceholder.second': 'Select a second field',
        'search.sunburst.tooSmall': 'There are an additional {0} values with document counts too small to display. Please refine your search.',
        'search.sunburst.missingValues': 'This area represents {0} search result(s) which contained no values for the parametric field {1}',
        'search.savedSearches': 'Searches',
        'search.savedSearches.confirm.deleteMessage': 'Are you sure you want to remove {0} saved search?',
        'search.savedSearches.confirm.deleteMessage.title': 'Delete saved search',
        'search.savedSearches.confirm.resetMessage': 'Are you sure you want to reset {0} saved search?',
        'search.savedSearches.confirm.resetMessage.title': 'Reset saved search',
        'search.savedSearches.deleteFailed': 'Error: could not delete the saved search',
        'search.selected': 'Selected',
        'search.similarDocuments': 'Similar documents',
        'search.similarDocuments.error': 'Error: could not fetch similar documents',
        'search.similarDocuments.none': 'No similar documents found',
        'search.spellCheck.showingResults': 'Showing results for',
        'search.spellCheck.searchFor': 'Search for',
        'settings.cancel': 'Cancel',
        'settings.cancel.message': 'All unsaved changes will be lost.',
        'settings.cancel.title': 'Revert settings?',
        'settings.close': 'Close',
        'settings.unload.confirm': 'You have unsaved settings!',
        'settings.adminUser': 'Admin User',
        'settings.adminUser.description': 'Configure the admin username and password for Find.',
        'settings.answerServer.description': 'Specify where Answer Server is located.',
        'settings.answerServer.enable': 'Enable Answer Server',
        'settings.answerServer.enabled': 'Answer Server is enabled',
        'settings.answerServer.disable': 'Disable Answer Server',
        'settings.answerServer.disabled': 'Answer Server is disabled',
        'settings.answerServer.loading': 'Loading\u2026',
        'settings.answerServer.title': 'Answer Server',
        'settings.community.description': "Community handles authentication for Find. We recommend using a dedicated Community server for Find and not using it for any other parts of your IDOL installation.  Your Community server will need an Agentstore server for data storage.",
        'settings.community.login.type': 'Login Type',
        'settings.community.login.fetchTypes': 'Test connection to retrieve available login types.',
        'settings.community.login.invalidType': 'You must test connection and choose a valid login type.',
        'settings.community.serverName': 'community',
        'settings.community.title': 'Community',
        'settings.content.description': 'Specify where your content is indexed.',
        'settings.content.title': 'Content',
        'settings.description': "This page is for editing the Find config file.  The config file location is stored in the Java system property {0}.  The current location is {1}.",
        'settings.iod.apiKey': 'API key',
        'settings.iod.application': 'Application',
        'settings.iod.domain': 'Domain',
        'settings.locale.title': 'Locale',
        'settings.locale.default': 'Default locale',
        'settings.logoutFromSettings': 'Logout from Settings',
        'settings.map': 'Mapping',
        'settings.map.attribution': 'Attribution',
        'settings.map.description': 'View location information. The tile server URL should contain {x}, {y}, and {z} variables',
        'settings.map.disable': 'Disable Mapping',
        'settings.map.disabled': 'Mapping is disabled',
        'settings.map.enable': 'Enable Mapping',
        'settings.map.enabled': 'Mapping is enabled',
        'settings.map.loading': 'Loading\u2026',
        'settings.map.results.step': 'Results to load each time',
        'settings.map.url': 'Tile Server URL Template',
        'settings.mmap': 'MMAP',
        'settings.mmap.description': 'View rich media with MMAP',
        'settings.mmap.disable': 'Disable MMAP',
        'settings.mmap.disabled': 'MMAP is disabled',
        'settings.mmap.enable': 'Enable MMAP',
        'settings.mmap.enabled': 'MMAP is enabled',
        'settings.mmap.loading': 'Loading\u2026',
        'settings.mmap.url': 'URL',
        'settings.savedSearches': 'Saved Searches',
        'settings.savedSearches.description': 'Configuration relating to saved searches',
        'settings.savedSearches.loading': 'Loading\u2026',
        'settings.savedSearches.polling.disable': 'Disable polling',
        'settings.savedSearches.polling.disabled': 'Polling for updates to saved searches is disabled',
        'settings.savedSearches.polling.enable': 'Enable polling',
        'settings.savedSearches.polling.enabled': 'Polling for updates to saved searches is enabled',
        'settings.savedSearches.polling.interval': 'Polling Interval (in minutes)',
        'settings.password': 'Password',
        'settings.password.description': 'Password will be stored encrypted',
        'settings.password.redacted': '(redacted)',
        'settings.powerpoint': 'PowerPoint',
        'settings.powerpoint.description': 'A custom two-slide PowerPoint .pptx file can be optionally provided; with a doughnut chart on the first slide and a line chart with a date x-axis and two numeric y-axes on the second slide. To reserve space for your own logos etc. on other visualizations, customize the margins below.',
        'settings.powerpoint.template.file': 'Template File',
        'settings.powerpoint.template.sample.download': 'Download Sample Template',
        'settings.powerpoint.template.file.default': 'default',
        'settings.powerpoint.template.file.validate': 'Test Template',
        'settings.powerpoint.template.error.TEMPLATE_FILE_NOT_FOUND': 'PowerPoint template file not found',
        'settings.powerpoint.template.error.TEMPLATE_INVALID': 'PowerPoint template file has invalid format',
        'settings.powerpoint.template.error.INVALID_MARGINS': 'Invalid margins supplied',
        'settings.powerpoint.template.margins': 'Margins',
        'settings.queryManipulation': 'Query Manipulation',
        'settings.queryManipulation.blacklist': 'Blacklist Name',
        'settings.queryManipulation.description': 'Enable query manipulation with QMS',
        'settings.queryManipulation.disable': 'Disable Query Manipulation',
        'settings.queryManipulation.disabled': 'Query Manipulation is disabled',
        'settings.queryManipulation.dictionary': 'Dictionary',
        'settings.queryManipulation.enable': 'Enable Query Manipulation',
        'settings.queryManipulation.enabled': 'Query Manipulation is enabled',
        'settings.queryManipulation.expandQuery': 'Enable synonyms',
        'settings.queryManipulation.index': 'Index',
        'settings.queryManipulation.loading': 'Loading\u2026',
        'settings.queryManipulation.typeaheadMode': 'Typeahead Mode',
        'settings.requiredFields': 'required fields',
        'settings.restoreChanges': 'Revert Changes',
        'settings.retry': 'Retry Save',
        'settings.save': 'Save Changes',
        'settings.save.confirm': 'Are you sure you want to save this configuration?',
        'settings.save.confirm.title': 'Confirm Save',
        'settings.save.saving': 'Saving configuration. Please wait\u2026',
        'settings.save.retypePassword': '(you may need to re-type your password)',
        'settings.save.success': 'Success!',
        'settings.save.success.message': 'Configuration has been saved.',
        'settings.save.errorThrown': 'Threw exception: ',
        'settings.save.failure': 'Error!',
        'settings.save.failure.validation.message': 'Validation error in',
        'settings.save.failure.and': 'and',
        'settings.save.failure.text': 'Would you like to retry?',
        'settings.save.unknown': 'Server returned error: ',
        'settings.statsserver.description': 'Send statistics to StatsServer for analysis',
        'settings.statsserver.disable': 'Disable Statistics Collection',
        'settings.statsserver.disabled': 'Statistics Collection is disabled',
        'settings.statsserver.enable': 'Enable Statistics Collection',
        'settings.statsserver.enabled': 'Statistics Collection is enabled',
        'settings.statsserver.title': 'StatsServer',
        'settings.statsserver.validation.CONNECTION_ERROR': 'An error occurred while contacting the StatsServer',
        'settings.statsserver.validation.INVALID_CONFIGURATION': 'The StatsServer is not configured correctly for HPE Find',
        'settings.test': 'Test Connection',
        'settings.test.ok': 'Connection OK',
        'settings.test.databaseBlank': 'Database must not be blank!',
        'settings.test.failed': 'Connection failed',
        'settings.test.failed.password': 'Connection failed (you may need to re-type your password)',
        'settings.test.hostBlank': 'Host name must not be blank!',
        'settings.test.passwordBlank': 'Password must not be blank!',
        'settings.test.portInvalid': 'Port must not be blank, and inside the range 1-65535 !',
        'settings.test.usernameBlank': 'Username must not be blank!',
        'settings.username': 'Username',
        'settings.view': 'View',
        'settings.view.connector': 'Connector',
        'settings.view.description': 'View documents by either a custom field, or using a connector',
        'settings.view.referenceFieldBlank': 'Reference Field must not be blank',
        'settings.view.referenceFieldLabel': 'Reference Field',
        'settings.view.referenceFieldPlaceholder': 'Enter Reference Field',
        'settings.view.viewingMode': 'Viewing Mode',
        'settings.CONNECTION_ERROR': 'An error occurred while contacting the ACI server',
        'settings.FETCH_PORT_ERROR': "An error occurred while fetching the details of the server's index and service ports",
        'settings.FETCH_SERVICE_PORT_ERROR': "An error occurred while fetching the details of the server's service port",
        'settings.INCORRECT_SERVER_TYPE': 'Incorrect server type. Make sure you are using one of {0}',
        'settings.INDEX_PORT_ERROR': "An error occurred while fetching the details of the server's index port",
        'settings.REQUIRED_FIELD_MISSING': 'One or more of the required fields is missing',
        'settings.REGULAR_EXPRESSION_MATCH_ERROR': 'The target server is of an incorrect type',
        'settings.SERVICE_AND_INDEX_PORT_ERROR': "The server's service or index ports could not be determined",
        'settings.SERVICE_PORT_ERROR': "The server's service port could not be determined",
        'users.password': 'Password',
        'users.admin': 'Admin',
        'users.noUsers': 'No users retrieved from Community.',
        'users.refresh': 'Refresh',
        'users.none': 'There are currently no admin users',
        'users.title': 'User Management',
        'users.button.create': 'Create',
        'users.button.createUser': 'Create User',
        'users.button.cancel': 'Close',
        'users.create': 'Create New Users',
        'users.delete': 'Delete',
        'users.delete.text': 'Are you sure?',
        'users.delete.confirm': 'Confirm',
        'users.delete.cancel': 'Cancel',
        'users.info.done': 'Done!',
        'users.info.createdMessage': 'User {0} successfully created.',
        'users.info.deletedMessage': 'User {0} successfully deleted.',
        'users.info.error': 'Error!',
        'users.info.creationFailedMessage': 'New user profile creation failed.',
        'users.password.confirm': 'Confirm Password',
        'users.password.error': 'Password must not be blank',
        'users.select.level': 'Select User Level:',
        'users.serverError': 'Server returned error.',
        'users.admin.role.add': 'Create role',
        'users.admin.role.add.title': 'Admin role required',
        'users.admin.role.add.description': 'This Community server does not have an admin role. Would you like to create one?',
        'wizard.last': 'Logout',
        'wizard.next': 'Next',
        'wizard.prev': 'Prev',
        'wizard.step.settings': 'Settings',
        'wizard.step.users': 'Users',
        'wizard.step.welcome': 'Welcome',
        'wizard.welcome': "Welcome to the Find configuration wizard",
        'wizard.welcome.helper': "This wizard will help you set up Find in two quick steps:",
        'wizard.welcome.step1': 'On the Settings page, configure your connection settings, then click Save',
        'wizard.welcome.step2': "On the Users page, create initial user accounts, then click Logout",
        'wizard.welcome.finish': 'After you complete the configuration wizard, you can start using Find'
    });
});
