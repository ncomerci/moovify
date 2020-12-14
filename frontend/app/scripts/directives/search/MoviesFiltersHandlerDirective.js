'use strict';
define(['frontend', 'services/DynamicOptionsService', 'services/utilities/MovieCategoryService'], function(frontend) {

  frontend.directive('moviesFiltersHandlerDirective', function (DynamicOptionsService, $location, MovieCategoriesService) {
    return {
      restrict: 'E',
      scope: {
        filterParams: '=' //el nombre del atributo es igual al valor
      },
      link: function(scope) { //link es una funcion que sirve para modificar el DOM

        scope.supportedValues = null;

        DynamicOptionsService.getOptions('/movies').then(function(optionArray) {
          scope.supportedValues = {};
          optionArray.forEach(function(opt){ scope.supportedValues[opt.name] = opt.values });
        }).catch(function() { $location.path('/404') }); //con location.path redirijo

      },
      controller: function ($scope) {

        $scope.orderMap = {
          "newest": "{{'NEWEST' | translate }}",
          "oldest":"{{'OLDEST' | translate }}",
          "mostPosts":"{{'MOST_POSTS' | translate }}",
          "title":"{{ 'TITLE' | translate }}"
        }

        $scope.getCategory = function(option) {
          return MovieCategoriesService.getMovieCategory(option);
        }

        $scope.getOrder = function(option) {
          return $scope.orderMap[option];
        }
      },
      templateUrl:'resources/views/directives/search/movieFiltersHandlerDirective.html'
      };
    });
});

