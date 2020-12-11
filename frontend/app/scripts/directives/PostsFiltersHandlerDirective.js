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

        DynamicOptionsService.getOptions('/posts').then((optionArray) => {
          scope.supportedValues = {};
          optionArray.forEach(opt => scope.supportedValues[opt.name] = opt.options);
        }).catch(() => $location.path('/404'));

      },
      templateUrl: 'views/directives/postFiltersHandlerDirective.html'
    };
  });

});
