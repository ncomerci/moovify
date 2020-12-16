'use strict';
define(['frontend', 'services/LoginService', 'services/utilities/PageTitleService', 'services/fetch/UserFetchService',
  'directives/PaginationHandlerDirective', 'directives/listEntries/UserListEntryDirective'], function(frontend) {

  function init(value, defaultVal){
    return value ? value : defaultVal;
  }

  frontend.directive('fetchUsersDirective', function($location, $routeParams) {
    return {

      restrict: 'E',

      scope: {
        order: '<',
        enabled: '<',
        defaultPageSize: '<',
        path: '@',
        refreshUrlFn: '='
      },

      controller: function ($scope, UserFetchService) {

        $scope.fetchUsers = function() {

          UserFetchService.fetchUsers($scope.path, $scope.enabled, $scope.order,
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
          if ($scope.queryParams !== null) {
            Object.keys($scope.queryParams)
              .forEach(function (paramKey) {
                $location.search(paramKey, $scope.queryParams[paramKey])
              });
          }
        };
      },

      link: function(scope) {

        scope.users = [];

        scope.paginationMutex = false;

        scope.firstSearchDone = false;

        scope.paginationParams = {
          currentPage: init(parseInt($routeParams.pageNumber), 0),
          pageSize: init(parseInt($routeParams.pageSize), scope.defaultPageSize)
        };

        scope.resetPagination = null;

        scope.queryParams = null;

        // Execute first fetch
        scope.fetchUsers();

      },

      templateUrl: 'resources/views/directives/fetch/fetchUsersDirective.html'
    };
  });

});
