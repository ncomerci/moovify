define(['frontend', 'services/fetch/MovieFetchService', 'services/fetch/PostFetchService', 'services/DisplayService'
  ,'services/utilities/MovieCategoryService', 'directives/fetch/FetchPostsDirective'], function(frontend) {

  'use strict';
  frontend.controller('MovieViewController', function($scope, $routeParams, $httpParamSerializer, MovieFetchService, PostFetchService, DisplayService, MovieCategoriesService) {

    $scope.movie = null;
    $scope.posts = null;
    $scope.setNewUrl = null;
    $scope.postsPath = getPostsUrl($routeParams.id);

    $scope.getYear = function (date){
      if(!date)
        return;
      return DisplayService.getYear(date);
    }

    $scope.getMovieCategory = function (category){
      if(!category)
        return;
      return MovieCategoriesService.getMovieCategory(category);
    }
    function getPostsUrl (id) {
      return '/movies/' + id + '/posts';
    }

   $scope.getCategoryUrl = function (url, params) {
      var serializedParams = $httpParamSerializer(params);

      if (serializedParams.length > 0) {
        url += ((url.indexOf('?') === -1) ? '?' : '&') + serializedParams;
      }

      return url;
    }

    MovieFetchService.fetchMovieById($routeParams.id).then(function (movie) {
      $scope.movie = movie;
    }).catch(console.log);
  });
});
