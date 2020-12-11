'use strict';
define(['frontend', 'services/UserFetchService',
  'directives/PaginationHandlerDirective', 'directives/UserFiltersHandlerDirective'], function(frontend) {

  const basePaginationParams = {
    pageSize: 5,
    currentPage: 0
  }

  function init(value, defaultVal){
    return value ? value : defaultVal;
  }

  frontend.controller('FilteredUserCollectionController', function ($scope, UserFetchService, $location, $routeParams) {
    $scope.users = [];
    $scope.paginationParams = basePaginationParams;
    $scope.query = $scope.$parent.query;
    $scope.filterParams = {
      role: init($routeParams.role, null),
      orderBy: init($routeParams.orderBy, 'newest'),
      enabled: true
    };

    $scope.execSearch = () => UserFetchService.searchUsers(
      $scope.query.value, $scope.filterParams.role, $scope.filterParams.enabled, $scope.filterParams.orderBy,
      $scope.paginationParams.pageSize, $scope.paginationParams.currentPage).then(
      resp => {
        $scope.users = resp.collection;
        $scope.paginationParams = resp.paginationParams;

        // Refresh URL
        $location.search(resp.queryParams);
        $location.search('type', 'user')
      }
    ).catch(() => $location.path('/404')); // TODO: Add 500 page

    // TODO: No se puede encargar la directiva de paginacion?
    $scope.resetPagination = () => {

      if($scope.paginationParams === null){
        $scope.paginationParams = {
          pageSize: basePaginationParams.pageSize
        }
      }

      $scope.paginationParams.currentPage = basePaginationParams.currentPage;
    }

    $scope.execSearch();

    // TODO: Si le pasaramos la funcion a ejecutar execSearch, esto tambien lo podria hacer la directive de paginacion
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

      let newRole = newParams.role !== oldParams.role;
      let newOrderBy = newParams.orderBy !== oldParams.orderBy;

      if(newRole || newOrderBy) {
        $scope.resetPagination();
        $scope.execSearch();
      }
    });
  });
});
