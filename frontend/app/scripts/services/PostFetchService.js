'use strict';
define(['frontend', 'services/RestFulResponseFactory', 'services/LinkParserService'], function(frontend) {

  frontend.service('PostFetchService', function(RestFulResponse, LinkParserService, $q) {

    this.searchPosts = function(query, category, age, orderBy, pageSize, pageNumber) {
      return this.$$fetchPosts('/posts', query, category, age, orderBy, pageSize, pageNumber);
    }

    this.fetchPosts = function (path, orderBy, pageSize, pageNumber) {
      return this.$$fetchPosts(path, null, null, null, orderBy, pageSize, pageNumber);
    }

    this.$$fetchPosts = function(path, query, category, age, orderBy, pageSize, pageNumber) {

      let queryParams = {
        query: query,
        postCategory: category,
        postAge: age,
        orderBy: orderBy,
        pageSize: pageSize ? pageSize : 5,
        pageNumber: pageNumber ? pageNumber : 0
      };

      return $q((resolve, reject) => {
        RestFulResponse.all(path).getList(queryParams).then((postResponse) => {

          let paginationParams = null;
          let linkHeader = postResponse.headers('Link');
          let posts = postResponse.data;

          // Si no hay Link => no habia contenido => no me interesa paginar nada
          if(linkHeader){
            paginationParams = LinkParserService.parse(linkHeader);
          }

          resolve({collection: posts, paginationParams: paginationParams});

        }).catch((response) => reject({status: response.status, message: 'PostFetchService: FetchPost'}));
      });
    }
  });
});
