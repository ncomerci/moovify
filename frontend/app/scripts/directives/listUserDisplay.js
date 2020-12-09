'use strict';
define(['frontend'], function(frontend) {

    frontend.directive('listUserDisplay', function() {
        return {
            restrict: 'E',
            scope: {
                user: '=?'
            },
            templateUrl: 'views/directives/listUserDisplay.html'
        };
    });

});