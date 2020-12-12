'use strict';

define([], function() {
    return {
        defaultRoutePath: '/404',
        routes: {
            '/': {
                templateUrl: '/views/home.html',
                controller: 'HomeCtrl',
            },
            '/404': {
                templateUrl: '/404.html',
                // controller: 'ErrorController'
            },
            '/search': {
                templateUrl: '/views/search/search.html',
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
