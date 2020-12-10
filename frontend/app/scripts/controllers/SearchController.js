'use strict';
define(['frontend', 'services/SearchService', 'directives/paginatedDisplay', 'controllers/FilteredPostCollectionController'], function(frontend) {

    frontend.controller('SearchController', function($scope, $routeParams, $route, $location, SearchService) {

        console.log("Inicializando!");
        //
        const searchOptionsValuesByType = {
            posts: {
                postCategory: ['all', 'debate'],
                postAge: ['alltime', '24hs'],
                sortCriteria: ['newest', 'hottest']
            },
            movies: {
                movieCategory: ['all', 'action'],
                decade: ['alltime', '2010s'],
                sortCriteria: ['newest', 'oldest']
            },
            users: {
                role: ['all', 'user'],
                sortCriteria: ['newest', 'oldest']
            }
        };

        // const searchOptionsByType = {
        //     posts: {
        //         postCategory: $routeParams.postCategory ? $routeParams.postCategory : searchOptionsValuesByType.posts.postCategory[0],
        //         postAge: $routeParams.postAge ? $routeParams.postAge : searchOptionsValuesByType.posts.postAge[0],
        //         sortCriteria: $routeParams.sortCriteria ? $routeParams.sortCriteria : searchOptionsValuesByType.posts.sortCriteria[0]
        //     },
        //     movies: {
        //         movieCategory: $routeParams.movieCategory ? $routeParams.movieCategory : searchOptionsValuesByType.movies.movieCategory[0],
        //         decade: $routeParams.decade ? $routeParams.decade : searchOptionsValuesByType.movies.decade[0],
        //         sortCriteria: $routeParams.sortCriteria ? $routeParams.sortCriteria : searchOptionsValuesByType.movies.sortCriteria[0]
        //     },
        //     users: {
        //         role: $routeParams.role ? $routeParams.role : searchOptionsValuesByType.users.role[0],
        //         sortCriteria: $routeParams.sortCriteria ? $routeParams.sortCriteria : searchOptionsValuesByType.users.sortCriteria[0]
        //     }
        // };

        $scope.getOptionsValue = (type, optionName) => searchOptionsValuesByType[type][optionName];

        $scope.query = {
          value: $routeParams.query ? $routeParams.query : ''
        }

        $scope.searchOptions = {
            contentType: $routeParams.contentType,
        //     query: $routeParams.query ? $routeParams.query : '',
        //     params: searchOptionsByType[$routeParams.contentType],
        //     paginationParams: {
        //         size: 5,
        //         page: 0
        //     }
        };
        // $scope.$watch('searchOptions.query', function (){
        //   $scope.$apply();
        // })
        // $scope.execSearch = (newType) => {

        //     if(newType){
        //         $scope.searchOptions.params = searchOptionsByType[newType];
        //     }

        //     console.log($scope.searchOptions.contentType,
        //         Object.assign({query: $scope.searchOptions.query}, $scope.searchOptions.params)
        //     );
        // };

        // $scope.paginatedAns = {
        //     collection: [],
        //     paginationParams: {
        //         size: 5,
        //         page: 0
        //     }
        // }
    });

});
