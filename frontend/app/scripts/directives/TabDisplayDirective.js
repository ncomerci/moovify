'use strict';
define(['frontend'], function(frontend) {

  frontend.directive('tabDisplayDirective', function () {
    return {
      restrict: 'E',
      scope: {
        tabSelected: '=',
        tabs: '<'
      },
      templateUrl: 'resources/views/directives/tabDisplayDirective.html',
      controller: function ($scope) {
        $scope.changeType = function (type) {
          $scope.tabSelected = type;
        }
      }
    }
  })
});
