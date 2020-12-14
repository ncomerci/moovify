'use strict';
define(['frontend', 'services/utilities/PageTitleService', 'directives/TabDisplayDirective', 'directives/fetch/FetchPostsDirective',
  'services/fetch/UserFetchService', 'directives/listEntries/MinUserListEntryDirective'], function(frontend) {

	frontend.controller('HomeCtrl', function($scope, PageTitle, $routeParams, $location, UserFetchService) {

    PageTitle.setTitle('Moovify'); // TODO: cambiar key

    $scope.postOrders = [
      { value: 'hottest', message: 'POST_ORDER_HOTTEST' },
      { value: 'newest', message: 'POST_ORDER_NEWEST' }
      ];

    $scope.order = {
      value: $routeParams.orderBy ? $routeParams.orderBy : $scope.postOrders[0].value
    };

    $scope.setHottestUrl = null;
    $scope.setHottestUrl = null;

    $scope.$watch('order.value', function(newParam, oldParam, scope) {

      if(newParam !== oldParam) {

        if(newParam === 'hottest' && scope.setHottestUrl !== null) {
          scope.setHottestUrl();
        }
        else if(newParam === 'newest' && scope.setNewestUrl !== null) {
          scope.setNewestUrl();
        }

      }

    }, true);

    $scope.users = [];

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
