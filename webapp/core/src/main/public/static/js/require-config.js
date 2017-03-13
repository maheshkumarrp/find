/*
 * Copyright 2014-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

require.config({
    paths: {
        css: '../css',
        'about-page': '../bower_components/hp-autonomy-about-page/src',
        backbone: '../bower_components/backbone/backbone',
        bootstrap: '../bower_components/bootstrap/dist/js/bootstrap',
        bowser: '../bower_components/bjoska.bowser/bowser',
        'bootstrap-datetimepicker': '../bower_components/eonasdan-bootstrap-datetimepicker/src/js/bootstrap-datetimepicker',
        'databases-view': '../bower_components/hp-autonomy-js-databases-view/src',
        'datatables.net': '../bower_components/datatables.net/js/jquery.dataTables',
        'datatables.net-bs': '../bower_components/datatables.net-bs/js/dataTables.bootstrap',
        'datatables.net-fixedColumns': '../bower_components/datatables.net-fixedcolumns/js/dataTables.fixedColumns',
        'd3': '../bower_components/d3/d3',
        i18n: '../bower_components/requirejs-i18n/i18n',
        'peg': '../bower_components/pegjs/peg-0.10.0',
        'fieldtext': '../bower_components/hp-autonomy-fieldtext-js/src',
        'parametric-refinement': '../bower_components/hp-autonomy-js-parametric-refinement/src',
        iCheck: '../bower_components/iCheck/icheck',
        chosen: '../bower_components/chosen/chosen.jquery',
        metisMenu: '../bower_components/metisMenu/dist/metisMenu',
        jquery: '../bower_components/jquery/jquery',
        'js-whatever': '../bower_components/hp-autonomy-js-whatever/src',
        json2: '../bower_components/json/json2',
        'login-page': '../bower_components/hp-autonomy-login-page/src',
        leaflet: '../bower_components/leaflet/dist/leaflet-src',
        'Leaflet.awesome-markers': '../bower_components/Leaflet.awesome-markers/dist/leaflet.awesome-markers',
        'leaflet.markercluster': '../bower_components/leaflet.markercluster/dist/leaflet.markercluster-src',
        moment: '../bower_components/moment/moment',
        'moment-timezone-with-data': '../bower_components/moment-timezone/builds/moment-timezone-with-data',
        Raphael: '../bower_components/raphael/raphael',
        settings: '../bower_components/hp-autonomy-settings-page/src',
        slider: '../bower_components/seiyria-bootstrap-slider/dist',
        text: '../bower_components/requirejs-text/text',
        sunburst: '../bower_components/hp-autonomy-sunburst/src',
        topicmap: '../bower_components/hp-autonomy-topic-map/src',
        underscore: '../bower_components/underscore/underscore',
        typeahead: '../bower_components/corejs-typeahead/dist/typeahead.jquery',
        'flot': '../bower_components/Flot/jquery.flot',
        'flot.time': '../bower_components/Flot/jquery.flot.time'
    },
    shim: {
        'backbone': {
            exports: 'Backbone'
        },
        bootstrap: ['jquery'],
        'bootstrap-datetimepicker': ['jquery'],
        chosen: ['jquery'],
        d3: {
            exports: 'd3'
        },
        iCheck: ['jquery'],
        peg: {
            exports: 'PEG'
        },
        underscore: {
            exports: '_'
        },
        'Leaflet.awesome-markers': ['leaflet'],
        'leaflet.markercluster': ['leaflet'],
        'flot': ['jquery'],
        'flot.time': ['flot']
    }
});
