'use strict';
define(['frontend', 'services/DisplayService'], function(frontend) {

  frontend.directive('movieListEntryDirective', function (DisplayService) {
    return {
      restrict: 'E',
      scope: {
        movie: '='
      },
      templateUrl: 'resources/views/directives/movieListEntryDirective.html',
      controller: function ($scope) {
        $scope.getYear = function (releaseDate) {
          return DisplayService.getYear(releaseDate);
        }
      }
    }
  });
});
