'use strict';
define(['frontend', 'services/TimeService'], function(frontend) {

  frontend.directive('prettyDateDirective', function ($locale, TimeService) {

    return {
      restrict: 'E',

      scope: {
        creationDate: '<'
      },
      template: '<ng-pluralize count="getTimeVar()" when="getTimeForm()"></ng-pluralize>',
      controller : function ($scope) {

        $scope.getTimeVar = function (){
          return TimeService.getTimeVar($scope.creationDate);
        }

        $scope.getTimeForm = function(){
          return TimeService.getTimeForm($scope.creationDate, $locale.id);
        }
      }
    }
  });
});
