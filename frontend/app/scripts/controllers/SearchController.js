'use strict';
define(['frontend', 'directives/SearchMoviesDirective', 'directives/SearchUsersDirective',
  'directives/SearchPostsDirective',], function(frontend) {

    const defaultType = 'posts';

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

      $scope.$watch('searchOptions.contentType', (newParam, oldParam, scope) => {

        if(newParam !== oldParam)
          $location.search('type', scope.searchOptions.contentType);

      }, true);

    });

});
