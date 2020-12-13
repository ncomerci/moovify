'use strict';
define(['frontend', 'services/RestFulResponseFactory', 'services/LinkParserService'], function(frontend) {

  frontend.service('MovieFetchService', function (RestFulResponse, LinkParserService, $q){

    this.searchMovies = function(query, category, age, enabled, orderBy, pageSize, pageNumber) {
      return fetchMovies('/movies', query, category, age, enabled, orderBy, pageSize, pageNumber);
    }

    this.fetchMovies = function(path, enabled, orderBy, pageSize, pageNumber) {
      return fetchMovies(path, null, null, null, enabled, orderBy, pageSize, pageNumber);
    }

    function fetchMovies(path, query, category, decade, enabled, orderBy, pageSize, pageNumber) {

      var queryParams = {
        query: query,
        orderBy: orderBy,
        pageSize: pageSize ? pageSize : 5,
        pageNumber: pageNumber ? pageNumber : 0
      };

      if(category) {
        queryParams.movieCategory = category;
      }

      if(decade) {
        queryParams.decade = decade;
      }

      if(enabled) {
        queryParams.enabled = enabled;
      }

      return $q(function(resolve, reject) {
        RestFulResponse.noAuth().all(path).getList(queryParams).then(function(movieResponse) { //hago la query a la Api con queryparams de json

          var paginationParams = null;
          var linkHeader = movieResponse.headers('link'); //pido los headers link
          var movies = movieResponse.data;

          if(linkHeader) {
            paginationParams = LinkParserService.parse(linkHeader); //si tengo los convierto en datos de paginacion
          }

        resolve({collection: movies, paginationParams: paginationParams, queryParams: queryParams}); //devuelvo la informacion que junte e n el servicio

        }).catch(function(response) { reject({status: response.status, message: 'MovieFetchService: FetchMovie'}) });

      });

    }
  });
});
