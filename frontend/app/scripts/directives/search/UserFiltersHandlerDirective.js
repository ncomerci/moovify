'use strict';
define(['frontend', 'services/DynamicOptionsService'], function(frontend) {

  frontend.directive('userFiltersHandlerDirective', function(DynamicOptionsService, $location) {
    return {
      restrict: 'E',
      scope: {
        filterParams: '='
      },
      link: function(scope) {

        scope.supportedValues = null;

        DynamicOptionsService.getOptions('/users').then(function(optionArray) {
          scope.supportedValues = {};
          optionArray.forEach(function(opt) { scope.supportedValues[opt.name] = opt.values });
        }).catch(function() { $location.path('/404') }); // TODO: A 500

      },
      controller: function ($scope) {
        $scope.orderMap = {
          "newest": "{{'NEWEST' | translate }}",
          "oldest": "{{'OLDEST' | translate }}",
          "followers": "{{'FOLLOWERS' | translate }}",
          "votes": "{{'VOTES' | translate }}",
          "username": "{{ 'USERNAME' | translate }}"
        }

        $scope.roleMap = {
          "user": "{{'USER' | translate }}",
          "admin": "{{'ADMIN' | translate }}"
        }

        $scope.getOrder = function(option){
          return $scope.orderMap[option];
        }

        $scope.getRole = function (option){
          return $scope.roleMap[option];
        }
      },
      templateUrl: 'resources/views/directives/search/userFiltersHandlerDirective.html'
    };
  });

});
