define(['frontend', 'services/RestFulResponseFactory', 'services/PostCategoriesService', 'services/MovieFetchService'], function(frontend) {

  'use strict';
  frontend.controller('PostCreateController', function($scope, PostCategoriesService, MovieFetchService, RestFulResponse, $location) {

    $scope.post = {};
    $scope.moviesTitles = [];
    $scope.createPostBtnPressed = false;
    $scope.easyMDE;

    PostCategoriesService.getPostCategories().then(function(optionArray) {
      $scope.postCategories = optionArray;
    }).catch(function() { $location.path('/404') });

    MovieFetchService.fetchMovies('/movies',
      null,null,100, 0).then(

      function(resp) {

        $scope.moviesList = new Map();

        Object.keys(resp.collection.plain()).forEach(function(paramKey) {
          $scope.moviesList.set(resp.collection[paramKey].title + ' - ' + resp.collection[paramKey].releaseDate, resp.collection[paramKey].id);
        });

        $scope.moviesList.keys().forEach(function(movie) {
          $scope.moviesTitles.push(movie);
        });

      }).catch(function() { $location.path('/404'); });


    $scope.titleConstraints = {
      pattern: /^[a-zA-Z ]*$/,
      minLen: 6,
      maxLen: 200
    }
    $scope.bodyConstraints = {
      minLen: 1,
      maxLen: 10000,
    }
    $scope.tagsConstraints = {
      pattern: /^[a-zA-Z ]*$/,
      maxLen: 50
    }
    $scope.createPost = function(post) {

      $scope.createPostBtnPressed = true;

      // if(
      //   !$scope.fieldIsNotValid('title') &&
      //   !$scope.fieldIsNotValid('category') &&
      //   !$scope.fieldIsNotValid('body')
      // ){
        console.log($scope.post);
        // console.log($scope.fieldIsNotValid('title'));

        post.tag = null;

        post.movie = null;

        var movies = post.movies.values();

        post.movies = [];

        movies.forEach(function(movie) { post.movies.push(movie); });

        var tags = post.tags.keys();

        post.tags = [];

        tags.forEach(function(tag) { post.tags.push(tag); });

        RestFulResponse.all('posts').post(post).then().catch(function(err) { console.log(err); });

    }

    $scope.addMovie = function () {
      var moviesSelected = document.getElementById('movies-selected')
      var movieName = $scope.post.movie;
      var movieId = $scope.moviesList.get($scope.post.movie);

      if(!movieId)
        return;

      if(!$scope.post.movies)
        $scope.post.movies = new Map();

      if($scope.post.movies.has(movieName))
        return;

      $scope.post.movies.set(movieName, movieId);

      $scope.post.movie = "";

      var movieBadge = createBadge(moviesSelected, movieName);

      movieBadge.closeElem.addEventListener('click',
        function() { unselectMovie( movieName, moviesSelected, movieBadge.badgeElem); }, false);
      }

    function unselectMovie(movieName, moviesSelected, movieBadgeElem){

      $scope.post.movies.delete(movieName);

      if(moviesSelected && movieBadgeElem)
        moviesSelected.removeChild(movieBadgeElem);

    }

    $scope.addTag = function (){
      var tag = $scope.post.tag;
      var tagsSelectedElem = document.getElementById('tags-selected');


      if(!$scope.post.tags)
        $scope.post.tags = new Map();

      if($scope.post.tags.has(tag))
        return;

      $scope.post.tags.set(tag, "");

      $scope.post.tag = "";

      var tagBadge = createBadge(tagsSelectedElem, tag);

      tagBadge.closeElem.addEventListener('click',
        function() { unselectTag(tag, tagsSelectedElem, tagBadge.badgeElem); }, false);
    }

    function unselectTag(tag, tagsSelectedElem, tagBadgeElem){
      $scope.post.tags.delete(tag);

      if(tagsSelectedElem && tagBadgeElem)
        tagsSelectedElem.removeChild(tagBadgeElem);

    }

    function createBadge(parentElem, text) {
      var closeElem = document.createElement("button");

      closeElem.setAttribute('class', 'uk-margin-small-left uk-light');
      closeElem.setAttribute('type', 'button');
      closeElem.setAttribute('uk-close', '');

      var badgeElem = document.createElement("span");

      badgeElem.setAttribute('class', 'uk-badge uk-primary disabled uk-padding-small uk-margin-small-right uk-margin-small-bottom');

      badgeElem.appendChild(document.createTextNode(text));
      badgeElem.appendChild(closeElem);

      parentElem.appendChild(badgeElem);

      // TODO: Esto es lo que querias hacer? Las propiedades de los objetos tienen que tener nombre si o si -Tobi
      return {badgeElem: badgeElem, closeElem: closeElem};
    }

    $scope.fieldRequired = function(field) {
      return $scope.createPostBtnPressed && $scope.createPostForm[field].$error.required !== undefined;
    }

    $scope.fieldIsNotValid = function(field) {
      console.log($scope.fieldRequired(field));
      return $scope.fieldRequired(field) || $scope.signupForm[field].$error.pattern ||
        $scope.signupForm[field].$error.minlength || $scope.signupForm[field].$error.maxlength;
    }

  });
});
