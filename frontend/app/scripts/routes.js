'use strict';

define([], function() {
    return {
        defaultRoutePath: '/404',
        routes: {
            '/': {
                templateUrl: 'resources/views/home.html',
                controller: 'HomeCtrl'
            },
            '/404': {
                templateUrl: 'resources/404.html'
            },
            '/search': {
                templateUrl: 'resources/views/search/search.html',
                controller: 'SearchController'
            },
            '/login': {
                templateUrl: 'resources/views/login/login.html',
                controller: 'LoginCtrl'
            },
            '/signup': {
              templateUrl: 'resources/views/login/signup.html',
              controller: 'SignupCtrl'
            },
            '/user': {
                templateUrl: 'resources/views/user/userProfile.html',
                controller: 'profileCtrl'
            },
            '/posts/create': {
                templateUrl: 'resources/views/post/postCreate.html',
                controller: 'PostCreateController'
            },
            '/posts/:id': {
                templateUrl: 'resources/views/post/PostViewController.html',
                controller: 'PostViewController'
            },
            '/movie/:id': {
                templateUrl: 'resources/views/movie/movieView.html',
                controller: 'MovieViewController'
            },
            '/admin': {
                templateUrl: 'resources/views/admin/admin.html',
                controller: 'AdminController'
            }
            /* ===== yeoman hook ===== */
            /* Do not remove these commented lines! Needed for auto-generation */
        }
    };
});
