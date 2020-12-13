'use strict';
define(['frontend', 'directives/search/SearchMoviesDirective', 'directives/search/SearchUsersDirective',
  'directives/search/SearchPostsDirective'], function(frontend) {

  var defaultType = 'posts';

    frontend.controller('SearchController', function($scope, $routeParams, $location) {

      console.log("Inicializando!");

      $scope.query = {
        value: $routeParams.query ? $routeParams.query : ''
      }

      $scope.searchOptions = {
        contentType: $routeParams.type
      };

      if(!$scope.searchOptions.contentType) {
        $scope.searchOptions.contentType = defaultType;
        $location.search('type', defaultType);
      }

      $scope.$watch('searchOptions.contentType', function(newParam, oldParam, scope) {

        if(newParam !== oldParam)
          $location.search('type', scope.searchOptions.contentType);

      }, true);

    });

});
