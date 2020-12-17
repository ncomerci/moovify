'use strict';
define(['frontend', 'services/entities/MovieService', 'services/PostCreateModalService'], function(frontend) {

  frontend.directive('postCreateModalDirective', function(MovieService, PostCreateModalService, $location) {
    return {
      restrict: 'E',
      scope: {
        moviesTitles: '=',
        moviesList: '=',
        movieMap: '=',
        tagMap: '=',
        createPostBtnPressed: '=',
        createPostForm: '=',
        tagsConstraints: '=',
        postObject: '=',
        postCreateFn: '&'
      },
      link: function(scope) {

        scope.movieMap = {};
        scope.tagMap = {};
        scope.postCreateFn = scope.postCreateFn();


        MovieService.fetchMovies('/movies',
          null, 1100, 0).then(function (resp) {
            resp.collection.plain().forEach(function(movie) {
              scope.moviesList[movie.title + ' - ' + movie.releaseDate.substring(0, movie.releaseDate.indexOf("-"))] = movie.id;
            });
            scope.moviesTitles = Object.keys(scope.moviesList);
          }).catch(function () { $location.path('/404'); });
      },
      controller: function ($scope) {
        $scope.addMovie = function (movieName) {
          PostCreateModalService.addMovie(movieName, $scope.movieMap, $scope.moviesList);
          $scope.post.movie = "";
          $scope.moviesTitles = Object.keys($scope.moviesList);
          $scope.moviesBadges = Object.entries($scope.movieMap);
        }

        $scope.unselectMovie = function (movieName, movieId) {
          delete $scope.movieMap[movieName];
          $scope.moviesList[movieName] = movieId;
          $scope.moviesTitles = Object.keys($scope.moviesList);
          $scope.moviesBadges = Object.entries($scope.movieMap);
        }

        $scope.addTag = function (tag) {
          PostCreateModalService.addTag(tag, $scope.tagMap);
          $scope.tagsBadges = Object.keys($scope.tagMap);
          $scope.post.tag = "";
        }

        $scope.unselectTag = function (tag) {
          delete $scope.tagMap[tag];
          $scope.tagsBadges = Object.keys($scope.tagMap);
        }

        $scope.validateMovieMin = function () {
          return $scope.createPostBtnPressed && Object.keys($scope.movieMap).length <= 0;
        }

        $scope.validateMovieMax = function () {
          return $scope.createPostBtnPressed && Object.keys($scope.movieMap).length > 20;
        }

        $scope.validateMovieMax = function () {
          return $scope.createPostBtnPressed && Object.keys($scope.tagMap).length  > 5;
        }
      },
      templateUrl: 'resources/views/directives/postCreateModalDirective.html'
    };
  });

});
