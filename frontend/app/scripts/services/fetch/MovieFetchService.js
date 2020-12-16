'use strict';
define(['frontend', 'services/utilities/RestFulResponseFactory', 'services/utilities/LinkParserService'], function(frontend) {

  frontend.service('MovieFetchService', function (RestFulResponse, LinkParserService, $q){

    this.searchMovies = function(query, category, age, orderBy, pageSize, pageNumber) {
      return fetchMovies('/movies', query, category, age, orderBy, pageSize, pageNumber);
    }

    this.fetchMovies = function(path, orderBy, pageSize, pageNumber) {
      return fetchMovies(path, null, null, null, orderBy, pageSize, pageNumber);
    }

    this.fetchMovieById = function (movieId) {
      return $q(function (resolve, reject) {
        RestFulResponse.noAuth().one('movies', movieId).get().then(function (response) {

          var movie = response.data.plain();
          resolve(movie);
        }).catch(reject);

      });
    }

    function fetchMovies(path, query, category, decade, orderBy, pageSize, pageNumber) {

      var queryParams = {
        orderBy: orderBy,
        pageSize: pageSize ? pageSize : 5,
        pageNumber: pageNumber ? pageNumber : 0
      };

      if(query)
        queryParams.query = query;

      if(category) {
        queryParams.movieCategory = category;
      }

      if(decade) {
        queryParams.decade = decade;
      }

      return $q(function(resolve, reject) {
        RestFulResponse.noAuth().all(path).getList(queryParams).then(function(movieResponse) { //hago la query a la Api con queryparams de json

          var paginationParams = {lastPage: 0, pageSize: queryParams.pageSize, currentPage: queryParams.pageNumber};
          var linkHeader = movieResponse.headers('link'); //pido los headers link
          var movies = movieResponse.data;

          if(linkHeader) {
            paginationParams.lastPage = LinkParserService.parse(linkHeader); //si tengo los convierto en datos de paginacion
          }

        resolve({collection: movies, paginationParams: paginationParams, queryParams: queryParams}); //devuelvo la informacion que junte e n el servicio

        }).catch(function(response) { reject({status: response.status, message: 'MovieFetchService: FetchMovie'}) });

      });

    }
  });
});
