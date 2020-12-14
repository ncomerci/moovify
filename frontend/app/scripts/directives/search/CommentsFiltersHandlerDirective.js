'use strict';
define(['frontend', 'services/DynamicOptionsService'], function (frontend) {

  frontend.directive('commentsFiltersHandlerDirective', function (DynamicOptionsService, $location) {
    return {
      restrict: 'E',
      scope: {
        filterParams: '='
      },
      link: function (scope) {

        scope.supportedValues = null;

        DynamicOptionsService.getOptions('/comments').then(function(optionArray) {
          scope.supportedValues = {};
          optionArray.forEach(function(opt){ scope.supportedValues[opt.name] = opt.values });
        }).catch(function() { $location.path('/404') }); //con location.path redirijo

      },
      controller: function ($scope) {

        $scope.orderMap = {
          "newest": "{{'NEWEST' | translate }}",
          "oldest":"{{'OLDEST' | translate }}",
          "hottest":"{{'HOTTEST' | translate }}"
        }

        $scope.getOrder = function(option) {
          return $scope.orderMap[option];
        }
      },
      templateUrl:'resources/views/directives/search/commentFiltersHandlerDirective.html'
    };
  });
});
