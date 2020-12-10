'use strict';
define(['frontend'], function(frontend) {


  const supportedValues = {
    postCategory: ['debate', 'watchlist'],
    postAge: ['day', 'week'],
    orderBy: ['newest', 'oldest']
  }

  frontend.directive('postsFiltersHandlerDirective', function() {
    return {
      restrict: 'E',
      scope: {
        filterParams: '='
      },
      link: function(scope, element, attrs) {
          scope.getSupportedValues = function (type) {
          return supportedValues[type];
        }
      },
      templateUrl: 'views/directives/postFiltersHandlerDirective.html'
    };
  });

});
