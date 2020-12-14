'use strict';
define(['frontend', 'services/RestFulResponseFactory', 'services/LinkParserService', 'services/CommentFetchService', 'services/LoginService'], function(frontend) {

  frontend.service('PostFetchService', function(RestFulResponse, LinkParserService, CommentFetchService, LoggedUserFactory, $q) {

    this.searchPosts = function(query, category, age, enabled, orderBy, pageSize, pageNumber) {
      return internalFetchPosts('/posts', query, category, age, enabled, orderBy, pageSize, pageNumber);
    }

    this.fetchPosts = function (path, enabled, orderBy, pageSize, pageNumber) {
      return internalFetchPosts(path, null, null, null, enabled, orderBy, pageSize, pageNumber);
    }

    this.fetchFullPost = function (postId, userId) {

      return $q(function(resolve, reject) {

        RestFulResponse.withAuthIfPossible(LoggedUserFactory.getLoggedUser()).then(function(Restangular) {
          Restangular.one('posts', postId).get().then(function (response) {

            var post = response.data.plain();

            if(userId) {
              RestFulResponse.noAuth().one('posts', postId).one('votes', userId).get().then(function(response) {
                post.userVote = response.data.plain();
                resolve(post);
              }).catch(reject);
            }
            else {
              resolve(post);
            }

          }).catch(reject);
        }).catch(reject);
      });
    }

    function internalFetchPosts(path, query, category, age, enabled, orderBy, pageSize, pageNumber) {

      // Obligatory params
      var queryParams = {
        orderBy: orderBy,
        pageSize: pageSize ? pageSize : 5,
        pageNumber: pageNumber ? pageNumber : 0
      };

      if(query)
        queryParams.query = query;

      // Optional Params
      if(category)
        queryParams.postCategory = category;

      if(age)
        queryParams.postAge = age;

      if(enabled !== null)
        queryParams.enabled = enabled;

      return $q(function(resolve, reject) {
        RestFulResponse.withAuthIfPossible(LoggedUserFactory.getLoggedUser()).then(function (Restangular) {

        Restangular.all(path).getList(queryParams).then(function(postResponse) {

          var paginationParams = {lastPage: 0, pageSize: queryParams.pageSize, currentPage: queryParams.pageNumber};
          var linkHeader = postResponse.headers('Link');
          var posts = postResponse.data;

          // Si no hay Link -> no habia contenido -> no me interesa paginar nada
          if(linkHeader){
            paginationParams.lastPage = LinkParserService.parse(linkHeader);
          }

          resolve({collection: posts, paginationParams: paginationParams, queryParams: queryParams});

        }).catch(function(response) { reject({status: response.status, message: 'PostFetchService: FetchPost'})
        });
      }).catch(reject);
    });
    }
  });
});
