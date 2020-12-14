'use strict';
define(['frontend', 'services/DisplayService', 'services/UserService', 'services/LoginService'], function(frontend) {

  frontend.directive('userListEntryDirective', function (DisplayService, UserService, LoggedUserFactory) {
    return {
      restrict: 'E',
      scope: {
        user: '='
      },
      templateUrl: 'resources/views/directives/listEntries/userListEntryDirective.html',
      controller: function ($scope) {
        $scope.getYear = function (releaseDate) {
          return DisplayService.getYear(releaseDate);
        }

        $scope.loggedUser = LoggedUserFactory.getLoggedUser();

        $scope.isAdmin = function (user){
          return UserService.userHasRole(user, 'ADMIN');
        }
      }
    }
  });
});
