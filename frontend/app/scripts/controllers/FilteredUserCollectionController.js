'use strict';
define(['frontend', 'services/UserFetchService',
  'directives/PaginationHandlerDirective', 'directives/UserFiltersHandlerDirective'], function(frontend) {

  const defaultPageSize = 5;

  function init(value, defaultVal){
    return value ? value : defaultVal;
  }

  frontend.controller('FilteredUserCollectionController', function ($scope, UserFetchService, $location, $routeParams) {
    $scope.users = [];

    $scope.paginationParams = {currentPage: init(parseInt($routeParams.pageNumber), 0), pageSize: init(parseInt($routeParams.pageSize), defaultPageSize)};
    $scope.query = $scope.$parent.query;
    $scope.filterParams = {
      role: init($routeParams.role, null),
      orderBy: init($routeParams.orderBy, 'newest'),
      enabled: true,
    };
    $scope.resetPagination = null;

    $scope.execSearch = () => UserFetchService.searchUsers(
      $scope.query.value, $scope.filterParams.role, $scope.filterParams.enabled, $scope.filterParams.orderBy,
      $scope.paginationParams.pageSize, $scope.paginationParams.currentPage).then(
      resp => {
        $scope.users = resp.collection;
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

      let newRole = newParams.role !== oldParams.role;
      let newOrderBy = newParams.orderBy !== oldParams.orderBy;

      if(newRole || newOrderBy) {

        if($scope.resetPagination)
          $scope.resetPagination();

        $scope.execSearch();
      }
    });
  });
});
