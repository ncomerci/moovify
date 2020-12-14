'use strict';
define(['frontend', 'services/DisplayService'], function (frontend) {

  frontend.directive('commentListEntryDirective', function (DisplayService){
    return {
      restrict: 'E',
      scope: {
        user: '='
      },
      templateUrl: 'resources/views/directives/listEntries/commentListEntryDirective.html',
      controller: function ($scope) {
          $scope.getBodyFormatted = function (body){
            return DisplayService.getBodyFormatted(body);
          }
          $scope.getAgeMessage = function (creationDate) {
            return DisplayService.getAgeMessageCode(creationDate);
          }
      }
    }
  });
});
