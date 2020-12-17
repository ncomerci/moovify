'use strict';
define(['frontend', 'services/DisplayService', 'services/entities/UserService', 'services/LoginService'], function(frontend) {

  frontend.directive('userListEntryDirective', function (DisplayService, UserService, LoggedUserFactory, $q) {
    return {
      restrict: 'E',
      scope: {
        user: '=',
        adminControls: '<',
        removeUserFn:'&'
      },
      link: function (scope) {
        if(scope.removeUserFn)
          scope.removeUserFn = scope.removeUserFn();
      },
      controller: function ($scope, $q) {
        $scope.getYear = function (releaseDate) {
          return DisplayService.getYear(releaseDate);
        }

        $scope.avatar = null;

        $scope.loggedUser = LoggedUserFactory.getLoggedUser();

        $scope.isAdmin = UserService.userHasRole($scope.user, 'ADMIN');

        $scope.recoverUser = function () {
          UserService.recoverUser($scope.user).then(function (user) {
            $scope.removeUserFn(user);
          }).catch();
        }
      },
      templateUrl: 'resources/views/directives/listEntries/userListEntryDirective.html',
    }
  });
});
