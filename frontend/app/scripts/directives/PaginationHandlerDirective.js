'use strict';
define(['frontend'], function(frontend) {

  frontend.directive('paginationHandlerDirective', function() {
    return {
      restrict: 'E',
      scope: {
        paginationParams: '='
      },
      templateUrl: 'views/directives/paginationHandlerDirective.html'
    };
  });

});
