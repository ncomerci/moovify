'use strict';
define(['frontend', 'services/utilities/PageTitleService', 'directives/TabDisplayDirective', 'directives/fetch/FetchPostsDirective'], function(frontend) {

	frontend.controller('HomeCtrl', function($scope, PageTitle, $routeParams, $location) {

    PageTitle.setTitle('asdasd'); // TODO: cambiar key

    $scope.postOrders = [
      { value: 'hottest', message: 'POST_ORDER_HOTTEST' },
      { value: 'newest', message: 'POST_ORDER_NEWEST' }
      ];

    $scope.order = {
      value: $routeParams.orderBy ? $routeParams.orderBy : $scope.postOrders[0].value
    };

    $scope.$watch('order.value', function(newParam, oldParam, scope) {

      if(newParam !== oldParam)
        $location.search('orderBy', scope.order.value);

    }, true);

	});
});
