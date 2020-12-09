'use strict';
define(['frontend'], function(frontend) {

    frontend.directive('paginationControls', function() {
        return {
            restrict: 'E',
            scope: {
                pagiantionParams: '=?'
            },
            templateUrl: 'views/directives/paginationControls.html'
        };
    });

});