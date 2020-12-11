'use strict';
define(['frontend', 'services/LoginService', 'services/PageTitleService', 'services/MovieFetchService',
  'directives/PaginationHandlerDirective', 'directives/MoviesFiltersHandlerDirective'], function(frontend) {

  const basePaginationParams = {
    pageSize: 5,
    currentPage: 0
  }

  function init(value, defaultVal){
    return value ? value : defaultVal;
  }

  frontend.controller('FilteredMovieCollectionController', function ($scope, MovieFetchService, $location, $routeParams) {
    $scope.movies = [];
    $scope.paginationParams = basePaginationParams;
    $scope.query = $scope.$parent.query;
    $scope.filterParams = {
      movieCategory: init($routeParams.movieCategory, null),
      decade: init($routeParams.decade, null),
      orderBy: init($routeParams.orderBy, 'newest'),
      enabled: true
    };

    $scope.execSearch = () => MovieFetchService.searchMovies($scope.query.value, $scope.filterParams.movieCategory,
      $scope.filterParams.decade, $scope.filterParams.enabled, $scope.filterParams.orderBy,
      $scope.paginationParams.pageSize, $scope.paginationParams.currentPage).then(
        resp => {

          $scope.movies = resp.collection;
          $scope.paginationParams = resp.paginationParams;

          $location.search(resp.queryParams);
          $location.search('type', 'movies');
        }
    ).catch(() => $location.path('/404'));

    $scope.resetPagination = () => {

      if($scope.paginationParams === null){
        $scope.paginationParams = {
          pageSize: basePaginationParams.pageSize
        }
      }
      $scope.paginationParams.currentPage = basePaginationParams.currentPage;
    }

    $scope.execSearch();

    $scope.$watchCollection('paginationParams', (newParams, oldParams) => {
      if(!newParams || !oldParams){
        return;
      }
      console.log(newParams.currentPage);
      console.log(oldParams.currentPage);
      let newPageSize = newParams.pageSize !== oldParams.pageSize;
      let newPageNumber = newParams.currentPage !== oldParams.currentPage;

      if(newPageNumber || newPageSize){

        if(newPageSize){
          $scope.resetPagination();
        }
        $scope.execSearch();
      }
    });

    $scope.$watch('query.value', (newQueryVal, oldQueryVal) => {
      if(newQueryVal !== oldQueryVal){
        $scope.resetPagination();
        $scope.execSearch();
      }
    });

    $scope.$watchCollection('filterParams', (newParams, oldParams) => {
      console.log('filter params triggered');
      let newMovieCategory = newParams.movieCategory !== oldParams.movieCategory;
      let newDecade = newParams.decade !== oldParams.decade;
      let newOrderBy = newParams.orderBy !== oldParams.orderBy;

      if(newMovieCategory || newDecade || newOrderBy) {
        $scope.resetPagination();
        $scope.execSearch();
      }
    });
  });
});
