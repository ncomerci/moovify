define(['frontend', 'directives/TabDisplayDirective', 'directives/search/SearchPostsDirective',
  'directives/search/SearchUsersDirective','directives/search/SearchCommentsDirective'
], function(frontend) {

    'use strict';
    frontend.controller('AdminController', function($scope, $routeParams, $location, $httpParamSerializer) {

      $scope.showingValues = [
        { value: 'posts', message: 'POST_TAB_DISPLAY' },
        { value: 'comments', message: 'COMMENT_TAB_DISPLAY' },
        { value: 'users', message: 'USER_TAB_DISPLAY' }
      ];

      $scope.query = {
        value: $routeParams.query ? $routeParams.query : ''
      };

      $scope.showing = {
        value: $routeParams.showing ? $routeParams.showing : $scope.showingValues[0].value
      };

      $scope.postDisabledUrl = null;
      $scope.commentsDisabledtUrl = null;
      $scope.usersDisabledUrl = null;

      $scope.getQueryUrl = function (url, params) {
        var serializedParams = $httpParamSerializer(params);

        if (serializedParams.length > 0) {
          url += ((url.indexOf('?') === -1) ? '?' : '&') + serializedParams;
        }

        return url;
      }

      $scope.$watch('showing.value', function(newParam, oldParam, scope) {

        if(newParam !== oldParam) {

          $location.search({ showing: scope.showing.value });

          if(newParam === 'posts' && scope.postDisabledUrl !== null) {
            scope.postDisabledUrl();
          }
          else if(newParam === 'comments' && scope.commentsDisabledtUrl !== null) {
            scope.commentsDisabledtUrl();
          }
          else if(newParam === 'users' && scope.usersDisabledUrl !== null) {
            scope.usersDisabledUrl();
          }
        }
      }, true);

    });
});
