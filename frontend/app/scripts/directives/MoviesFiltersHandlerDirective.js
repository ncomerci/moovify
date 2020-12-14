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

        $scope.categoryMap = {
          "action": "{{'ACTION' | translate }}",
          "adventure": "{{'ADVENTURE' | translate }}",
          "animation": "{{'ANIMATION' | translate }}",
          "comedy": "{{'COMEDY' | translate }}",
          "crime": "{{'CRIME' | translate }}",
          "documentary":"{{'DOCUMENTARY' | translate }}",
          "drama": "{{'DRAMA' | translate }}",
          "family": "{{'FAMILY' | translate }}",
          "fantasy": "{{'FANTASY' | translate }}",
          "history": "{{'HISTORY' | translate }}",
          "horror": "{{'HORROR' | translate }}",
          "music": "{{'MUSIC' | translate }}",
          "mystery": "{{'MYSTERY' | translate }}",
          "romance": "{{'ROMANCE' | translate }}",
          "scienceFiction": "{{'SCIENCE_FICTION' | translate }}",
          "tvMovie": "{{'TV_MOVIE' | translate }}",
          "thriller": "{{'THRILLER' | translate }}",
          "war": "{{'WAR' | translate }}",
          "western": "{{'WESTERN' | translate }}"
        }

        $scope.getCategory = function(option) {
          return $scope.categoryMap[option];
        }

        $scope.getOrder = function(option) {
          return $scope.orderMap[option];
        }
      },
      templateUrl:'resources/views/directives/movieFiltersHandlerDirective.html'
      };
    });
});

