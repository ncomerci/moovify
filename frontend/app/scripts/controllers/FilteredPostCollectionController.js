'use strict';
define(['frontend', 'services/LoginService', 'services/PageTitleService', 'services/PostFetchService',
  'directives/PaginationHandlerDirective', 'directives/PostsFiltersHandlerDirective'], function(frontend) {

  const basePaginationParams = {
    pageSize: 5,
    currentPage: 0
  }

  function init(value, defaultVal){
    return value ? value : defaultVal;
  }

  frontend.controller('FilteredPostCollectionController', function ($scope, PostFetchService, $location, $routeParams) {
    $scope.posts = [];
    $scope.paginationParams = basePaginationParams;
    $scope.query = $scope.$parent.query;
    $scope.filterParams = {
      postCategory: init($routeParams.postCategory, ''),
      postAge: init($routeParams.postAge, ''),
      orderBy: init($routeParams.orderBy, 'newest')
    };

    $scope.execSearch = () => PostFetchService.searchPosts(
      $scope.query.value, $scope.filterParams.postCategory, $scope.filterParams.postAge, $scope.filterParams.orderBy,
      $scope.paginationParams.pageSize, $scope.paginationParams.currentPage).then(
        resp => {
          $scope.posts = resp.collection;
          $scope.paginationParams = resp.paginationParams;
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

      let newPageSize = newParams.pageSize !== oldParams.pageSize;
      let newPageNumber = newParams.currentPage !== oldParams.currentPage;

      if(newPageSize || newPageNumber){
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

      let newPostCategory = newParams.postCategory !== oldParams.postCategory;
      let newPostAge = newParams.postAge !== oldParams.postAge;
      let newOrderBy = newParams.orderBy !== oldParams.orderBy;

      if(newPostCategory) {
        $location.search('postCategory', newParams.postCategory);
      }
      if(newPostAge) {
        $location.search('postAge', newParams.postAge);
      }
      if(newOrderBy) {
        $location.search('orderBy', newParams.orderBy);
      }

      if(newPostCategory || newPostAge || newOrderBy) {
        $scope.resetPagination();
        $scope.execSearch();
      }
    });
  });
});
