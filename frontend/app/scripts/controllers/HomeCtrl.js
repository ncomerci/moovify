'use strict';
define(['frontend', 'services/utilities/PageTitleService', 'services/LoginService', 'directives/TabDisplayDirective',
  'directives/fetch/FetchPostsDirective', 'services/fetch/UserFetchService',
  'directives/listEntries/MinUserListEntryDirective'], function(frontend) {

	frontend.controller('HomeCtrl', function($scope, PageTitle, $routeParams, $location, UserFetchService) {

    PageTitle.setTitle('HOME_TITLE');

    $scope.showingValues = [
      { value: 'hottestPosts', message: 'POST_ORDER_HOTTEST' },
      { value: 'newestPosts', message: 'POST_ORDER_NEWEST' }
      ];

    if($scope.loggedUser.logged) {
      $scope.showingValues.push({ value: 'myFeed', message: 'INDEX_MY_FEED' });
    }

    $scope.$watch('loggedUser.logged', function(newParam, oldParam, scope) {
      if(oldParam === true && newParam === false) {
        scope.showingValues.pop();

        if(scope.showing.value === 'myFeed') {
          scope.showing.value = 'hottestPosts';
        }
      }
    });

    $scope.showing = {
      value: $routeParams.showing ? $routeParams.showing : $scope.showingValues[0].value
    };

    if($scope.showing.value === 'myFeed' && !$scope.loggedUser.logged) {
      $scope.showing.value = 'hottestPosts';
      $location.search('showing', $scope.showing.value);
    }

    $scope.setHottestUrl = null;
    $scope.setNewestUrl = null;
    $scope.setFeedUrl = null;

    $scope.$watch('showing.value', function(newParam, oldParam, scope) {

      if(newParam !== oldParam) {

        $location.search({ showing: scope.showing.value });

        if(newParam === 'hottestPosts' && scope.setHottestUrl !== null) {
          scope.setHottestUrl();
        }
        else if(newParam === 'newestPosts' && scope.setNewestUrl !== null) {
          scope.setNewestUrl();
        }
        else if(newParam === 'myFeed' && scope.loggedUser.logged && scope.setFeedUrl !== null) {
          scope.setFeedUrl();
        }

      }

    }, true);

    $scope.users = null;

    var userPageSize = 15;
    var userOrder = "votes";

    // Execute first fetch
    UserFetchService.fetchUsers("/users", true, userOrder, userPageSize, 0).then(
      function(resp) {
        $scope.users = resp.collection;
      }

    ).catch(function() { $location.path('/404') });

	});
});
