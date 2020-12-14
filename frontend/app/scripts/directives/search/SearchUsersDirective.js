'use strict';
define(['frontend', 'services/LoginService', 'services/utilities/PageTitleService', 'services/fetch/UserFetchService',
  'directives/PaginationHandlerDirective', 'directives/search/UserFiltersHandlerDirective', 'directives/listEntries/UserListEntryDirective'], function(frontend) {

  var defaultPageSize = 5;

  function init(value, defaultVal){
    return value ? value : defaultVal;
  }

  frontend.directive('searchUsersDirective', function($location, $routeParams) {
    return {

      restrict: 'E',
      scope: {
        query: '=',
        enabled: '@',
        refreshUrlFn: '='
      },

      controller: function ($scope, UserFetchService) {

        $scope.execSearch = function() {

          UserFetchService.searchUsers(
            $scope.query.value, $scope.filterParams.role, $scope.filterParams.enabled, $scope.filterParams.orderBy,
            $scope.paginationParams.pageSize, $scope.paginationParams.currentPage).then(

            function(resp) {
              $scope.users = resp.collection;
              $scope.paginationParams = resp.paginationParams;
              $scope.queryParams = resp.queryParams;

              if($scope.firstSearchDone)
                $scope.refreshUrlFn();
              else
                $scope.firstSearchDone = true;

              $scope.paginationMutex = false;
            }
          ).catch(function() { $location.path('/404') });
        };

        $scope.refreshUrlFn = function() {
          Object.keys($scope.queryParams)
            .forEach(function(paramKey) { $location.search(paramKey, $scope.queryParams[paramKey]) });
        };

      },

      link: function(scope) {

        scope.users = [];

        scope.paginationMutex = false;

        scope.firstSearchDone = false;

        scope.paginationParams = {
          currentPage: init(parseInt($routeParams.pageNumber), 0),
          pageSize: init(parseInt($routeParams.pageSize), defaultPageSize)
        };

        scope.query = scope.$parent.query;

        scope.filterParams = {
          role: init($routeParams.role, null),
          orderBy: init($routeParams.orderBy, 'newest'),
          enabled: scope.enabled
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

          var newRole = newParams.role !== oldParams.role;
          var newOrderBy = newParams.orderBy !== oldParams.orderBy;

          if(newRole || newOrderBy) {

            if(scope.resetPagination)
              scope.resetPagination();

            scope.execSearch();
          }

          // Execute first search
          scope.execSearch();

        });

      },

      templateUrl: 'resources/views/directives/search/searchUsersDirective.html'
    };
  });

});
