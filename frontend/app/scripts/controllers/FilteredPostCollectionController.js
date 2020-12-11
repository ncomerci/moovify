'use strict';
define(['frontend', 'services/LoginService', 'services/PageTitleService', 'services/PostFetchService',
  'directives/PaginationHandlerDirective', 'directives/PostsFiltersHandlerDirective'], function(frontend) {

  const defaultPageSize = 5;

  function init(value, defaultVal){
    return value ? value : defaultVal;
  }

  frontend.controller('FilteredPostCollectionController', function ($scope, PostFetchService, $location, $routeParams) {
    $scope.posts = [];

    $scope.paginationParams = {currentPage: init(parseInt($routeParams.pageNumber), 0), pageSize: init(parseInt($routeParams.pageSize), defaultPageSize)};
    $scope.query = $scope.$parent.query;
    $scope.filterParams = {
      postCategory: init($routeParams.postCategory, null),
      postAge: init($routeParams.postAge, null),
      orderBy: init($routeParams.orderBy, 'newest'),
      enabled: true
    };
    $scope.resetPagination = null;

    $scope.execSearch = () => PostFetchService.searchPosts(
      $scope.query.value, $scope.filterParams.postCategory, $scope.filterParams.postAge, $scope.filterParams.enabled,
      $scope.filterParams.orderBy, $scope.paginationParams.pageSize, $scope.paginationParams.currentPage).then(
        resp => {
          $scope.posts = resp.collection;
          $scope.paginationParams = resp.paginationParams;

          // Refresh URL
          Object.entries(resp.queryParams).forEach(([param, value]) => $location.search(param, value));
        }
      ).catch(() => $location.path('/404')); // TODO: Add 500 page

    // Execute first search
    $scope.execSearch();

    $scope.$watch('query.value', (newParam, oldParam) => {

      if(newParam === oldParam)
        return;

      if($scope.resetPagination)
        $scope.resetPagination();

      $scope.execSearch();
    }, true);

    $scope.$watchCollection('filterParams', (newParams, oldParams) => {

      let newPostCategory = newParams.postCategory !== oldParams.postCategory;
      let newPostAge = newParams.postAge !== oldParams.postAge;
      let newOrderBy = newParams.orderBy !== oldParams.orderBy;

      if(newPostCategory || newPostAge || newOrderBy) {

        if($scope.resetPagination)
          $scope.resetPagination();

        $scope.execSearch();
      }
    });
  });
});
