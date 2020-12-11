'use strict';
define(['frontend', 'services/DynamicOptionsService'], function(frontend) {

  frontend.directive('moviesFiltersHandlerDirective', function (DynamicOptionsService, $location) {
    return {
      restrict: 'E',
      scope: {
        filterParams: '=' //el nombre del atributo es igual al valor
      },
      link: function(scope) { //link es una funcion que sirve para modificar el DOM

        scope.supportedValues = null;

        DynamicOptionsService.getOptions('/movies').then((optionArray) => {
          scope.supportedValues = {};
          optionArray.forEach(opt => scope.supportedValues[opt.name] = opt.options);
        }).catch(() => $location.path('/404')); //con location.path redirijo

      },
      templateUrl:'views/directives/movieFiltersHandlerDirective.html'
      };
    });
});

