'use strict';
define(['frontend', 'services/DisplayService', 'services/LoginService'], function(frontend) {

  frontend.directive('userListEntryDirective', function (DisplayService, LoggedUserFactory) {
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
      }
    }
  });
});
