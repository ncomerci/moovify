'use strict';
define(['frontend', 'services/LoginService', 'services/utilities/PageTitleService', 'services/fetch/PostFetchService',
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
        enabled: '@'
      },
      controller: function ($scope, PostFetchService) {

        $scope.execSearch = function() {

          PostFetchService.searchPosts(
            $scope.query.value, $scope.filterParams.postCategory, $scope.filterParams.postAge, $scope.filterParams.enabled,
            $scope.filterParams.orderBy, $scope.paginationParams.pageSize, $scope.paginationParams.currentPage).then(

            function(resp) {
              $scope.posts = resp.collection;
              $scope.paginationParams = resp.paginationParams;

              // Refresh URL
              Object.keys(resp.queryParams)
                .forEach(function(paramKey) { $location.search(paramKey, resp.queryParams[paramKey]) });

              $scope.paginationMutex = false;
            }
          ).catch(function() { $location.path('/404') });
        } // TODO: Add 500 page

      },

      link: function (scope) {

        scope.posts = [];

        scope.paginationMutex = false;

        scope.paginationParams = {
          currentPage: init(parseInt($routeParams.pageNumber), 0),
          pageSize: init(parseInt($routeParams.pageSize), defaultPageSize)
        };

        scope.query = scope.$parent.query;

        scope.filterParams = {
          postCategory: init($routeParams.postCategory, null),
          postAge: init($routeParams.postAge, null),
          orderBy: init($routeParams.orderBy, 'newest'),
          enabled: true
        };

        scope.resetPagination = null;

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

      templateUrl: 'resources/views/directives/search/searchPostsDirective.html'
    };
  });
});
