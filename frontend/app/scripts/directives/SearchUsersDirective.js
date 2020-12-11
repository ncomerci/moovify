'use strict';
define(['frontend', 'services/LoginService', 'services/PageTitleService', 'services/UserFetchService',
  'directives/PaginationHandlerDirective', 'directives/UserFiltersHandlerDirective'], function(frontend) {

  const defaultPageSize = 5;

  function init(value, defaultVal){
    return value ? value : defaultVal;
  }

  frontend.directive('searchUsersDirective', function($location, $routeParams) {
    return {

      restrict: 'E',

      scope: {
        query: '=',
        enabled: '@'
      },

      controller: function ($scope, UserFetchService) {

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

      },

      link: function(scope) {

        scope.users = [];

        scope.paginationParams = {currentPage: init(parseInt($routeParams.pageNumber), 0), pageSize: init(parseInt($routeParams.pageSize), defaultPageSize)};

        scope.query = scope.$parent.query;

        scope.filterParams = {
          role: init($routeParams.role, null),
          orderBy: init($routeParams.orderBy, 'newest'),
          enabled: scope.enabled,
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

          let newRole = newParams.role !== oldParams.role;
          let newOrderBy = newParams.orderBy !== oldParams.orderBy;

          if(newRole || newOrderBy) {

            if(scope.resetPagination)
              scope.resetPagination();

            scope.execSearch();
          }

          // Execute first search
          scope.execSearch();

        });

      },

      templateUrl: 'views/directives/searchUsersDirective.html'
    };
  });

});
