'use strict';
define(['frontend'], function(frontend) {

  frontend.directive('tabDisplayDirective', function () {
    return {
      restrict: 'E',
      scope: {
        contentType: '=',
        tabs: '=',
        changeTypeFn: '&'
      },
      templateUrl: 'resources/views/directives/tabDisplayDirective.html',
      link: function (scope) {
        scope.changeTypeFn = scope.changeTypeFn();
      }
    }
  })
});
