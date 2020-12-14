'use strict';
define(['frontend', 'services/DisplayService', 'services/UserService', 'services/LoginService', 'services/utilities/RestFulResponseFactory'], function(frontend) {

  frontend.directive('userListEntryDirective', function (DisplayService, UserService, LoggedUserFactory, RestFulResponse, $q) {
    return {
      restrict: 'E',
      scope: {
        user: '=',
        adminControls: '<',
        removeUserFn:'&'
      },
      link: function (scope) {
        scope.removeUserFn = scope.removeUserFn();
      },
      controller: function ($scope, $q) {
        $scope.getYear = function (releaseDate) {
          return DisplayService.getYear(releaseDate);
        }

        $scope.loggedUser = LoggedUserFactory.getLoggedUser();

        $scope.isAdmin = function (user) {
          return UserService.userHasRole(user, 'ADMIN');
        }

        $scope.recoverUser = function () {
          return $q(function (resolve, reject) {
            RestFulResponse.withAuthIfPossible($scope.loggedUser).then(function (Restangular) {
              Restangular.one('users', $scope.user.id).all('enabled').doPUT().then(function () {
                $scope.removeUserFn($scope.user);
                resolve($scope.user);
              }).catch(reject);
            }).catch(reject);
          });
        }
      },
      templateUrl: 'resources/views/directives/listEntries/userListEntryDirective.html',
    }
  });
});
