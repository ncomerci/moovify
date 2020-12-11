'use strict';
define(['frontend', 'services/LoginService', 'services/PageTitleService', 'services/MovieFetchService',
  'directives/PaginationHandlerDirective', 'directives/MoviesFiltersHandlerDirective'], function(frontend) {

  const defaultPageSize = 5;

  function init(value, defaultVal){
    return value ? value : defaultVal;
  }

  frontend.directive('searchMoviesDirective', function($location, $routeParams) {
    return {

      restrict: 'E',

      scope: {
        query: '=',
        enabled: '@'
      },

      controller: function ($scope, MovieFetchService) {

        $scope.execSearch = () => MovieFetchService.searchMovies($scope.query.value, $scope.filterParams.movieCategory,
          $scope.filterParams.decade, $scope.filterParams.enabled, $scope.filterParams.orderBy,
          $scope.paginationParams.pageSize, $scope.paginationParams.currentPage).then(
          resp => {

            $scope.movies = resp.collection;
            $scope.paginationParams = resp.paginationParams;

            // Refresh URL
            Object.entries(resp.queryParams).forEach(([param, value]) => $location.search(param, value));
          }
        ).catch(() => $location.path('/404'));

      },

      link: function(scope){

        scope.movies = [];

        scope.paginationParams = {currentPage: init(parseInt($routeParams.pageNumber), 0), pageSize: init(parseInt($routeParams.pageSize), defaultPageSize)};

        scope.query = scope.$parent.query;

        scope.filterParams = {
          movieCategory: init($routeParams.movieCategory, null),
          decade: init($routeParams.decade, null),
          orderBy: init($routeParams.orderBy, 'newest'),
          enabled: true
        };

        scope.resetPagination = null;

        scope.$watch('query.value', (newParam, oldParam, scope) => {

          if(newParam === oldParam)
            return;

          if(scope.resetPagination)
            scope.resetPagination();

          scope.execSearch();
        }, true);

        scope.$watchCollection('filterParams', (newParams, oldParams, scope) => {

          let newMovieCategory = newParams.movieCategory !== oldParams.movieCategory;
          let newDecade = newParams.decade !== oldParams.decade;
          let newOrderBy = newParams.orderBy !== oldParams.orderBy;

          if(newMovieCategory || newDecade || newOrderBy) {

            if(scope.resetPagination)
              scope.resetPagination();

            scope.execSearch();
          }
        });

        // Execute first search
        scope.execSearch();

      },

      templateUrl: 'views/directives/searchMoviesDirective.html'
    };
  });

});
