'use strict';
define(['frontend'], function(frontend) {

    frontend.directive('listMovieDisplay', function() {
        return {
            restrict: 'E',
            scope: {
                movie: '=?'
            },
            templateUrl: 'views/directives/listMovieDisplay.html'
        };
    });

});