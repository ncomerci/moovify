'use strict';
define(['frontend', 'services/RestFulResponseFactory', 'services/LinkParserService'], function(frontend) {

  frontend.service('PostFetchService', function(RestFulResponse, LinkParserService, $q) {

    this.searchPosts = function(query, category, age, enabled, orderBy, pageSize, pageNumber) {
      return fetchPosts('/posts', query, category, age, enabled, orderBy, pageSize, pageNumber);
    }

    this.fetchPosts = function (path, enabled, orderBy, pageSize, pageNumber) {
      return fetchPosts(path, null, null, null, enabled, orderBy, pageSize, pageNumber);
    }

    function fetchPosts(path, query, category, age, enabled, orderBy, pageSize, pageNumber) {

      // Obligatory params
      var queryParams = {
        query: query,
        orderBy: orderBy,
        pageSize: pageSize ? pageSize : 5,
        pageNumber: pageNumber ? pageNumber : 0
      };

      // Optional Params
      if(category)
        queryParams.postCategory = category;

      if(age)
        queryParams.postAge = age;

      if(enabled !== null)
        queryParams.enabled = enabled;

      return $q(function(resolve, reject) {
        RestFulResponse.noAuth().all(path).getList(queryParams).then(function(postResponse) {

          var paginationParams = {pageSize: queryParams.pageSize, lastPage: 0};
          var linkHeader = postResponse.headers('Link');
          var posts = postResponse.data;

          // Si no hay Link -> no habia contenido -> no me interesa paginar nada
          if(linkHeader){
            paginationParams = LinkParserService.parse(linkHeader);
          }

          resolve({collection: posts, paginationParams: paginationParams, queryParams: queryParams});

        }).catch(function(response) { reject({status: response.status, message: 'PostFetchService: FetchPost'}) });
      });
    }
  });
});
