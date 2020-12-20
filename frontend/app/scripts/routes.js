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
                templateUrl: 'resources/404.html',
                controller: 'errorCtrl'
            },
          '/500': {
                templateUrl: 'resources/500.html',
                controller: 'errorCtrl'
            },
            '/search': {
                templateUrl: 'resources/views/search/search.html',
                controller: 'SearchCtrl'
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
            '/user/:id': {
              templateUrl: 'resources/views/user/userProfile.html',
              controller: 'profileCtrl'
            },
            '/post/create': {
                templateUrl: 'resources/views/post/postCreate.html',
                controller: 'PostCreateCtrl'
            },
            '/post/:id': {
                templateUrl: 'resources/views/post/PostViewController.html',
                controller: 'PostViewCtrl'
            },
            '/movie/:id': {
                templateUrl: 'resources/views/movie/movieView.html',
                controller: 'MovieViewCtrl'
            },
            '/admin': {
                templateUrl: 'resources/views/admin/admin.html',
                controller: 'AdminCtrl'
            },
            '/resetPassword': {
                templateUrl: 'resources/views/user/resetPassword.html',
                controller: 'ResetPasswordCtrl'
            },
            '/updatePassword': {
                templateUrl: 'resources/views/user/updatePassword.html',
                controller: 'UpdatePasswordCtrl'
            },
            '/comment/:id': {
              templateUrl: 'resources/views/comment/commentViewController.html',
              controller: 'CommentViewCtrl'
            },
            /* ===== yeoman hook ===== */
            /* Do not remove these commented lines! Needed for auto-generation */
        }
    };
});
