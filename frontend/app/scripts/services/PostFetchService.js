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
      let queryParams = {
        query: query,
        orderBy: orderBy,
        pageSize: pageSize ? pageSize : 5,
        pageNumber: pageNumber ? pageNumber : 0
      };

      console.log(queryParams);

      // Optional Params
      if(category)
        queryParams.postCategory = category;

      if(age)
        queryParams.postAge = age;

      if(enabled !== null)
        queryParams.enabled = enabled;

      return $q((resolve, reject) => {
        RestFulResponse.all(path).getList(queryParams).then((postResponse) => {

          let paginationParams = null;
          let linkHeader = postResponse.headers('Link');
          let posts = postResponse.data;

          // Si no hay Link => no habia contenido => no me interesa paginar nada
          if(linkHeader){
            paginationParams = LinkParserService.parse(linkHeader);
          }

          resolve({collection: posts, paginationParams: paginationParams, queryParams: queryParams});

        }).catch((response) => reject({status: response.status, message: 'PostFetchService: FetchPost'}));
      });
    }
  });
});
