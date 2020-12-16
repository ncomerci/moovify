'use strict';
define(['frontend', 'services/LoginService', 'services/utilities/PageTitleService', 'services/fetch/MovieFetchService',
  'directives/PaginationHandlerDirective', 'directives/search/MoviesFiltersHandlerDirective', 'directives/listEntries/MovieListEntryDirective'], function(frontend) {

  var defaultPageSize = 5;

  function init(value, defaultVal){
    return value ? value : defaultVal;
  }

  frontend.directive('searchMoviesDirective', function($location, $routeParams) {
    return {

      restrict: 'E',

      scope: {
        query: '=',
        enabled: '<',
        refreshUrlFn: '='
      },
      link: function(scope){

        scope.movies = [];

        scope.paginationMutex = false;

        scope.firstSearchDone = false;

        scope.paginationParams = {
          currentPage: init(parseInt($routeParams.pageNumber), 0),
          pageSize: init(parseInt($routeParams.pageSize), defaultPageSize)
        };

        scope.query = scope.$parent.query;

        scope.filterParams = {
          movieCategory: init($routeParams.movieCategory, null),
          decade: init($routeParams.decade, null),
          orderBy: init($routeParams.orderBy, 'newest'),
        };

        scope.resetPagination = null;

        scope.queryParams = null;

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

      controller: function ($scope, MovieFetchService) {

        $scope.execSearch = function() {

          MovieFetchService.searchMovies($scope.query.value, $scope.filterParams.movieCategory,
            $scope.filterParams.decade, $scope.filterParams.orderBy,
            $scope.paginationParams.pageSize, $scope.paginationParams.currentPage).then(

            function(resp) {

              $scope.movies = resp.collection;
              $scope.paginationParams = resp.paginationParams;
              $scope.queryParams = resp.queryParams;

              if($scope.firstSearchDone)
                $scope.refreshUrlFn();
              else
                $scope.firstSearchDone = true;

              $scope.paginationMutex = false;
            }
          ).catch(function(){ $location.path('/404') });
        };

        $scope.refreshUrlFn = function() {
          if ($scope.queryParams !== null) {
            Object.keys($scope.queryParams)
              .forEach(function (paramKey) {
                $location.search(paramKey, $scope.queryParams[paramKey])
              });
          }
        };


      },
      templateUrl: 'resources/views/directives/search/searchMoviesDirective.html'
    };
  });

});
