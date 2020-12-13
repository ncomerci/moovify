'use strict';
define(['frontend', 'uikit', 'directives/search/SearchMoviesDirective', 'directives/search/SearchUsersDirective',
  'directives/search/SearchPostsDirective', 'directives/tabDisplayDirective'], function(frontend) {

  var defaultType = 'posts';

    frontend.controller('SearchController', function($scope, $routeParams, $location) {

      console.log("Inicializando!");

      $scope.query = {
        value: $routeParams.query ? $routeParams.query : ''
      }

      $scope.searchOptions = {
        contentType: $routeParams.type
      }

      $scope.tabs = [
        {value:'posts', message:'POST_TAB_DISPLAY'},
        {value:'movies', message:'POST_TAB_DISPLAY'},
        {value:'users', message:"'POST_TAB_DISPLAY'"}
      ]

      $scope.changeType = function (type) {
        $scope.searchOptions.contentType = type;
      }

      if(!$scope.searchOptions.contentType) {
        $scope.searchOptions.contentType = defaultType;
        $location.search('type', defaultType);
      }

      $scope.$watch('searchOptions.contentType', function(newParam, oldParam, scope) {

        console.log(newParam, oldParam);
        if(newParam !== oldParam)
          $location.search('type', scope.searchOptions.contentType);

      }, true);

    });

});
