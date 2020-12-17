/* global paths */
'use strict';
require.config({
    baseUrl: '/paw-2020b-3/resources/scripts',
    paths: {
        angular: '../../bower_components/angular/angular',
        'angular-route': '../../bower_components/angular-route/angular-route',
        'angular-translate': '../../bower_components/angular-translate/angular-translate',
        'angular-sanitize': '../../bower_components/angular-sanitize/angular-sanitize',
        'es5-shim': '../../bower_components/es5-shim/es5-shim',
        jquery: '../../bower_components/jquery/dist/jquery',
        json3: '../../bower_components/json3/lib/json3',
        lodash: '../../bower_components/lodash/dist/lodash',
        moment: '../../bower_components/moment/moment',
        marked: '../../bower_components/marked/lib/marked',
        requirejs: '../../bower_components/requirejs/require',
        restangular: '../../bower_components/restangular/dist/restangular',
        'js-joda': '../../bower_components/js-joda/dist/js-joda',
        uikit: '../../static_dependencies/uikit/js/uikit',
        uikiticons: '../../static_dependencies/uikit/js/uikit-icons',
        'uikit-icons': '../../static_dependencies/uikit/js/uikit-icons',
        iconify: '../../static_dependencies/iconify/iconify',
        easymde: '../../static_dependencies/easymde/easyMde',
        purify: '../../bower_components/dompurify/dist/purify',
        'js-joda-timezone': '../../bower_components/js-joda-timezone/dist/js-joda-timezone',
        dompurify: '../../bower_components/dompurify/dist/purify'
    },
    shim: {
        angular: {
            deps: [
                'jquery'
            ],
            exports: 'angular'
        },
        'angular-route': {
            deps: [
                'angular'
            ]
        },
        uikiticons: {
            deps: [
                'uikit'
            ]
        },
        'angular-translate': {
            deps: [
                'angular'
            ]
        },
        'angular-sanitize': {
            deps: [
                'angular'
            ]
        }
    },
    packages: [

    ]
});

if (paths) {
    require.config({
        paths: paths
    });
}

require([
        'angular',
        'frontend',
        'controllers/IndexCtrl'
    ],
    function() {
        angular.bootstrap(document, ['frontend']);
    }
);
