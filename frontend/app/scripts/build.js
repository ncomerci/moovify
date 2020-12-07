/* global paths */
'use strict';
require.config({
    baseUrl: '/scripts',
    paths: {
        angular: '../../bower_components/angular/angular',
        'angular-route': '../../bower_components/angular-route/angular-route',
        'angular-translate': '../../bower_components/angular-translate/angular-translate',
        'es5-shim': '../../bower_components/es5-shim/es5-shim',
        jquery: '../../bower_components/jquery/dist/jquery',
        json3: '../../bower_components/json3/lib/json3',
        lodash: '../../bower_components/lodash/dist/lodash',
        moment: '../../bower_components/moment/moment',
        requirejs: '../../bower_components/requirejs/require',
        restangular: '../../bower_components/restangular/dist/restangular',
        uikit: '../../bower_components/uikit/dist/js/uikit',
        uikiticons: '../../bower_components/uikit/dist/js/uikit-icons',
        bootstrap: '../../bower_components/bootstrap/dist/js/bootstrap',
        affix: '../../bower_components/bootstrap-sass-official/assets/javascripts/bootstrap/affix',
        alert: '../../bower_components/bootstrap-sass-official/assets/javascripts/bootstrap/alert',
        button: '../../bower_components/bootstrap-sass-official/assets/javascripts/bootstrap/button',
        carousel: '../../bower_components/bootstrap-sass-official/assets/javascripts/bootstrap/carousel',
        collapse: '../../bower_components/bootstrap-sass-official/assets/javascripts/bootstrap/collapse',
        dropdown: '../../bower_components/bootstrap-sass-official/assets/javascripts/bootstrap/dropdown',
        tab: '../../bower_components/bootstrap-sass-official/assets/javascripts/bootstrap/tab',
        transition: '../../bower_components/bootstrap-sass-official/assets/javascripts/bootstrap/transition',
        scrollspy: '../../bower_components/bootstrap-sass-official/assets/javascripts/bootstrap/scrollspy',
        modal: '../../bower_components/bootstrap-sass-official/assets/javascripts/bootstrap/modal',
        tooltip: '../../bower_components/bootstrap-sass-official/assets/javascripts/bootstrap/tooltip',
        popover: '../../bower_components/bootstrap-sass-official/assets/javascripts/bootstrap/popover',
        'uikit-icons': '../../bower_components/uikit/dist/js/uikit-icons'
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
