'use strict';
define(['frontend', 'services/LoginService', 'services/PageTitleService', 'services/MovieFetchService',
  'directives/PaginationHandlerDirective', 'directives/MoviesFiltersHandlerDirective'], function(frontend) {

  var defaultPageSize = 5;

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

        $scope.execSearch = function() {

          MovieFetchService.searchMovies($scope.query.value, $scope.filterParams.movieCategory,
            $scope.filterParams.decade, $scope.filterParams.enabled, $scope.filterParams.orderBy,
            $scope.paginationParams.pageSize, $scope.paginationParams.currentPage).then(

            function(resp) {

              $scope.movies = resp.collection;
              $scope.paginationParams = resp.paginationParams;

              // Refresh URL
              Object.keys(resp.queryParams).forEach(function(paramKey){  $location.search(paramKey, resp.queryParams[paramKey]) });
            }
          ).catch(function(){ $location.path('/404') });
        }

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

        scope.$watch('query.value', function(newParam, oldParam, scope) {

          if(newParam === oldParam)
            return;

          if(scope.resetPagination)
            scope.resetPagination();

          scope.execSearch();
        }, true);

        scope.$watchCollection('filterParams', function(newParams, oldParams, scope) {

          var newMovieCategory = newParams.movieCategory !== oldParams.movieCategory;
          var newDecade = newParams.decade !== oldParams.decade;
          var newOrderBy = newParams.orderBy !== oldParams.orderBy;

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
