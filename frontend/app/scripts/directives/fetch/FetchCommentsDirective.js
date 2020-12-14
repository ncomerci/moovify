'use strict';
define(['frontend', 'services/LoginService', 'services/utilities/PageTitleService', 'services/fetch/CommentFetchService',
  'directives/PaginationHandlerDirective', 'directives/listEntries/CommentListEntryDirective'], function(frontend) {

  function init(value, defaultVal){
    return value ? value : defaultVal;
  }

  frontend.directive('fetchCommentsDirective', function($location, $routeParams) {
    return {

      restrict: 'E',

      scope: {
        order: '<',
        enabled: '<',
        defaultPageSize: '<',
        path: '@',
        refreshUrlFn: '='
      },

      controller: function ($scope, CommentFetchService) {

        $scope.fetchComments = function() {

          CommentFetchService.fetchComments($scope.path, $scope.enabled, $scope.order,
            $scope.paginationParams.pageSize, $scope.paginationParams.currentPage).then(

            function(resp) {
              $scope.comments = resp.collection;
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

        scope.comments = [];

        scope.paginationMutex = false;

        scope.firstSearchDone = false;

        scope.paginationParams = {
          currentPage: init(parseInt($routeParams.pageNumber), 0),
          pageSize: init(parseInt($routeParams.pageSize), scope.defaultPageSize)
        };

        scope.resetPagination = null;

        scope.queryParams = null;

        // Execute first fetch
        scope.fetchComments();

      },

      templateUrl: 'resources/views/directives/fetch/fetchCommentsDirective.html'
    };
  });

});