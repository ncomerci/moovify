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
          optionArray.forEach(function(opt) { scope.supportedValues[opt.name] = opt.options });
        }).catch(function() { $location.path('/404') }); // TODO: A 500

      },
      templateUrl: 'views/directives/userFiltersHandlerDirective.html'
    };
  });

});
