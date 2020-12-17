'use strict';
define(['frontend', 'services/LoginService', 'services/utilities/PageTitleService', 'services/entities/PostService',
  'directives/PaginationHandlerDirective', 'directives/listEntries/PostListEntryDirective'], function(frontend) {

  function init(value, defaultVal){
    return value ? value : defaultVal;
  }

  frontend.directive('fetchPostsDirective', function($location, $routeParams) {
    return {

      restrict: 'E',

      scope: {
        order: '<',
        enabledEntities: '<',
        defaultPageSize: '<',
        path: '@',
        refreshUrlFn: '='

      },

      controller: function ($scope, PostService) {
        $scope.fetchPosts = function () {

          $scope.posts = null;

         PostService.fetchPosts($scope.path, $scope.enabledEntities, $scope.order,
            $scope.paginationParams.pageSize, $scope.paginationParams.currentPage).then(
            function (resp) {
              $scope.posts = resp.collection;
              $scope.paginationParams = resp.paginationParams;
              $scope.queryParams = resp.queryParams;

              if ($scope.firstSearchDone)
                $scope.refreshUrlFn();
              else
                $scope.firstSearchDone = true;

              $scope.paginationMutex = false;
            }
          ).catch(function () {
            $location.path('/500')
          });

        };

        $scope.refreshUrlFn = function () {
          if ($scope.queryParams !== null) {
            Object.keys($scope.queryParams)
              .forEach(function (paramKey) {
                $location.search(paramKey, $scope.queryParams[paramKey])
              });
          }
        };
      },

      link: function(scope) {

        scope.posts = null;

        scope.paginationMutex = false;

        scope.firstSearchDone = false;

        scope.paginationParams = {
          currentPage: init(parseInt($routeParams.pageNumber), 0),
          pageSize: init(parseInt($routeParams.pageSize), scope.defaultPageSize)
        };

        scope.queryParams = null;

        scope.resetPagination = null;

        // Execute first fetch
        scope.fetchPosts();

      },

      templateUrl: 'resources/views/directives/fetch/fetchPostsDirective.html'
    };
  });

});
