'use strict';
define(['frontend', 'services/DynamicOptionsService'], function(frontend) {

  frontend.directive('postsFiltersHandlerDirective', function(DynamicOptionsService, $location) {
    return {
      restrict: 'E',
      scope: {
        filterParams: '='
      },
      link: function(scope) {

        scope.supportedValues = null;


        DynamicOptionsService.getOptions('/posts').then(function(optionArray) {
          scope.supportedValues = {};
          optionArray.forEach(function(opt){ scope.supportedValues[opt.name] = opt.values });
        }).catch(function() { $location.path('/500') });

      },
      controller: function($scope){

        $scope.categoryMap = {
          "watchlist": "{{'WATCHLIST' | translate }}",
          "critique":"{{'CRITIQUE' | translate }}",
          "debate":"{{'DEBATE' | translate }}",
          "news":"{{'NEWS' | translate }}"
        }

        $scope.ageMap = {
          "pastYear": "{{'PAST_YEAR' | translate }}",
          "pastMonth":"{{'PAST_MONTH' | translate }}",
          "pastWeek":"{{'PAST_WEEK' | translate }}",
          "pastDay":"{{'PAST_DAY' | translate }}"
        }

        $scope.orderMap = {
          "newest": "{{'NEWEST' | translate }}",
          "oldest":"{{'OLDEST' | translate }}",
          "hottest":"{{'HOTTEST' | translate }}",
        }

        $scope.getCategory = function(option) {
          return $scope.categoryMap[option];
        }

        $scope.getAge = function(option) {
          return $scope.ageMap[option];
        }

        $scope.getOrder = function(option) {
          return $scope.orderMap[option];
        }

      },
      templateUrl: 'resources/views/directives/search/postFiltersHandlerDirective.html'
    };
  });

});
