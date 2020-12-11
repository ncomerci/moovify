'use strict';
define(['frontend', 'directives/paginatedDisplay', 'controllers/FilteredPostCollectionController',
    'controllers/FilteredUserCollectionController', 'controllers/FilteredMovieCollectionController'], function(frontend) {

    frontend.controller('SearchController', function($scope, $routeParams) {

        console.log("Inicializando!");

        $scope.getOptionsValue = (type, optionName) => searchOptionsValuesByType[type][optionName];

        $scope.query = {
          value: $routeParams.query ? $routeParams.query : ''
        }
        // TODO: Ask if correct to delete
        // $scope.$watch('searchOptions.contentType', function (newVal, oldVal) {
        //   if(newVal !== oldVal){
        //     $location.search({});
        //     $location.search('type', $scope.searchOptions.contentType);
        //     $location.search('query', $scope.query.value);
        //   }
        // });

        $scope.searchOptions = {
            contentType: $routeParams.contentType ? $routeParams.contentType : 'posts',
        };
    });

});
