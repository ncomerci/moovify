'use strict';
define(['frontend', 'services/DisplayService'], function(frontend) {

  frontend.directive('postListEntryDirective', function(DisplayService) {
    return {
      restrict: 'E',
      scope: {
        post: '='
      },
      templateUrl: 'resources/views/directives/postListEntryDirective.html',
      controller: function ($scope) {
        $scope.getAgeMessage = function (creationDate) {
          return DisplayService.getAgeMessageCode(creationDate);
        }
      }
    }

  });

});
