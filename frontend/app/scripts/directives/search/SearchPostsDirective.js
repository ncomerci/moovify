'use strict';
define(['frontend', 'services/LoginService', 'services/utilities/PageTitleService', 'services/entities/PostService',
  'directives/PaginationHandlerDirective', 'directives/search/PostsFiltersHandlerDirective', 'directives/listEntries/PostListEntryDirective'], function(frontend) {

  var defaultPageSize = 5;

  function init(value, defaultVal){
    return value ? value : defaultVal;
  }

  frontend.directive('searchPostsDirective', function($location, $routeParams) {
    return {

      restrict: 'E',

      scope: {
        query: '=',
        enabled: '<',
        refreshUrlFn: '=',
        adminControls:'<'
      },
      link: function (scope) {

        scope.posts = null;

        scope.paginationMutex = false;

        scope.firstSearchDone = false;

        scope.paginationParams = {
          currentPage: init(parseInt($routeParams.pageNumber), 0),
          pageSize: init(parseInt($routeParams.pageSize), defaultPageSize)
        };

        scope.query = scope.$parent.query;

        scope.filterParams = {
          postCategory: init($routeParams.postCategory, null),
          postAge: init($routeParams.postAge, null),
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

          var newPostCategory = newParams.postCategory !== oldParams.postCategory;
          var newPostAge = newParams.postAge !== oldParams.postAge;
          var newOrderBy = newParams.orderBy !== oldParams.orderBy;

          if (newPostCategory || newPostAge || newOrderBy) {

            if (scope.resetPagination)
              scope.resetPagination();

            scope.execSearch();
          }
        });

        // Execute first search
        scope.execSearch();

      },
      controller: function ($scope, PostService) {

        $scope.execSearch = function() {

          $scope.posts = null;

          PostService.searchPosts(
            $scope.query.value, $scope.filterParams.postCategory, $scope.filterParams.postAge, $scope.filterParams.enabled,
            $scope.filterParams.orderBy, $scope.paginationParams.pageSize, $scope.paginationParams.currentPage).then(

            function(resp) {
              $scope.posts = resp.collection;
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

        $scope.removePost = function (post) {
          var index = $scope.posts.indexOf(post);
          if (index > -1) {
            $scope.posts.splice(index, 1);
          }
        }

      },
      templateUrl: 'resources/views/directives/search/searchPostsDirective.html'
    };
  });
});
