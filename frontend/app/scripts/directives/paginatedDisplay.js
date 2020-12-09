'use strict';
define(['frontend', 'directives/listUserDisplay', 'directives/listMovieDisplay'], function(frontend) {

    frontend.directive('paginatedDisplay', function() {
        return {
            restrict: 'E',
            transclude: true,
            scope: {
                type: '=?',
                collection: '=?',
                paginationParams: '=?'
            },
            templateUrl: 'views/directives/paginatedDisplay.html'
        };
    });

});