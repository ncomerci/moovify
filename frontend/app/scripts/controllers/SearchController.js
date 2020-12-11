'use strict';
define(['frontend', 'directives/paginatedDisplay', 'controllers/FilteredPostCollectionController'], function(frontend) {

    frontend.controller('SearchController', function($scope, $routeParams, $route, $location) {

        console.log("Inicializando!");

        $scope.getOptionsValue = (type, optionName) => searchOptionsValuesByType[type][optionName];

        $scope.query = {
          value: $routeParams.query ? $routeParams.query : ''
        }

        $scope.$watch('searchOptions.contentType', function (newVal, oldVal) {
          if(newVal !== oldVal){
            $location.search({});
            $location.search('type', $scope.searchOptions.contentType);
            $location.search('query', $scope.query.value);
          }
        });

        $scope.$watch('query.value', function (newQuery) {
          if(newQuery){
            $location.search('query', newQuery);
          }
        });

        $scope.searchOptions = {
            contentType: $routeParams.contentType ? $routeParams.contentType : 'posts',
        };
    });

});
