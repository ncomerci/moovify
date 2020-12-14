'use strict';
define(['frontend', 'services/LoginService', 'services/utilities/PageTitleService', 'services/fetch/MovieFetchService',
  'directives/PaginationHandlerDirective', 'directives/listEntries/MovieListEntryDirective'], function(frontend) {

  function init(value, defaultVal){
    return value ? value : defaultVal;
  }

  frontend.directive('fetchMoviesDirective', function($location, $routeParams) {
    return {

      restrict: 'E',

      scope: {
        order: '<',
        enabled: '<',
        defaultPageSize: '<',
        path: '@'

      },

      controller: function ($scope, MovieFetchService) {

        $scope.fetchMovies = function() {

          MovieFetchService.fetchMovies($scope.path, $scope.enabled, $scope.order,
            $scope.paginationParams.pageSize, $scope.paginationParams.currentPage).then(

            function(resp) {
              $scope.movies = resp.collection;
              $scope.paginationParams = resp.paginationParams;

              Object.keys(resp.queryParams)
                .forEach(function(paramKey) { $location.search(paramKey, resp.queryParams[paramKey]) });

              $scope.paginationMutex = false;
            }

          ).catch(function() { $location.path('/404') });

        }

      },

      link: function(scope) {

        scope.movies = [];

        scope.paginationMutex = false;

        scope.paginationParams = {
          currentPage: init(parseInt($routeParams.pageNumber), 0),
          pageSize: init(parseInt($routeParams.pageSize), scope.defaultPageSize)
        };

        scope.resetPagination = null;

        // Execute first fetch
        scope.fetchUsers();

      },

      templateUrl: 'resources/views/directives/fetch/fetchMoviesDirective.html'
    };
  });

});
