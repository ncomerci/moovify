'use strict';
define(['frontend', 'services/DisplayService'], function(frontend) {

  frontend.directive('postListEntryDirective', function(DisplayService) {
    return {
      restrict: 'E',
      scope: {
        post: '='
      },
      templateUrl: 'resources/views/directives/listEntries/postListEntryDirective.html',
      controller: function ($scope) {
        $scope.getAgeMessage = function (creationDate) {
          return DisplayService.getAgeMessageCode(creationDate);
        }
      }
    }

  });

});
