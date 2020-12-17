'use strict';
define(['frontend', 'services/LoginService', 'services/entities/UserService'], function(frontend) {

  frontend.directive('minUserListEntryDirective', function() {
    return {
      restrict: 'E',
      scope: {
        user: '='
      },
      templateUrl: 'resources/views/directives/listEntries/minUserListEntryDirective.html',
      controller: function ($scope, UserService) {
        $scope.isAdmin = UserService.userHasRole($scope.user, 'ADMIN');
      }
    }
  });
});
