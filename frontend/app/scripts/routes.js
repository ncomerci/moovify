'use strict';

define([], function() {
    return {
        defaultRoutePath: '/',
        routes: {
            '/': {
                templateUrl: '/views/home.html',
                controller: 'HomeCtrl',
            },
            '/search/:contentType': {
                templateUrl: '/views/search/SearchController.html',
                controller: 'SearchController'
            },
            '/login': {
                templateUrl: '/views/login/login.html',
                controller: 'LoginCtrl',
            },
            '/signup': {
              templateUrl: '/views/login/signup.html',
              controller: 'SignupCtrl',
            },
            /* ===== yeoman hook ===== */
            /* Do not remove these commented lines! Needed for auto-generation */
        }
    };
});
