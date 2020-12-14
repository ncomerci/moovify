'use strict';
define(['frontend', 'services/DisplayService', 'services/UserService'], function(frontend) {

  frontend.directive('postListEntryDirective', function(DisplayService, UserService) {
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

        $scope.isAdmin = function (user){
          return UserService.userHasRole(user, 'ADMIN');
        }
      }
    }

  });

});
