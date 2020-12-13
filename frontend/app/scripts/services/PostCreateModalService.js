'use strict';
define(['frontend'], function(frontend) {

  frontend.service('PostCreateModalService', function () {

    this.addMovie = function (movieName, movieMap, moviesList) {

      if(Object.keys(movieMap).length > 20)
        return;

      var movieId = moviesList[movieName];

      if(!movieId)
        return;

      if(movieMap.hasOwnProperty(movieName))
        return;

      movieMap[movieName] = movieId;

      delete moviesList[movieName];

    }

    this.addTag = function (tag, tagMap) {

      if(tag === undefined || Object.keys(tagMap).length >= 5 || tagMap.hasOwnProperty(tag) ){
        return;
      }
      tagMap[tag] = '';
    }
  });
});
