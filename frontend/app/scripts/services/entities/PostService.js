'use strict';
define(['frontend', 'services/utilities/RestFulResponseFactory', 'services/utilities/LinkParserService', 'services/LoginService'], function(frontend) {

  frontend.service('PostService', function(RestFulResponse, LinkParserService, LoggedUserFactory, $q) {

    this.searchPosts = function(query, category, age, enabled, orderBy, pageSize, pageNumber) {
      return internalFetchPosts('/posts', query, category, age, enabled, orderBy, pageSize, pageNumber);
    }

    this.fetchPosts = function (path, enabled, orderBy, pageSize, pageNumber) {
      return internalFetchPosts(path, null, null, null, enabled, orderBy, pageSize, pageNumber);
    }

    this.fetchPost = function (postId) {

      return $q(function(resolve, reject) {

        var loggedUser = LoggedUserFactory.getLoggedUser();

        RestFulResponse.withAuthIfPossible(loggedUser).then(function(Restangular) {
          Restangular.one('posts', postId).get().then(function (response) {
            resolve(response.data);
          }).catch(reject);
        }).catch(reject);
      });
    }

    this.createPost = function(post) {
      return $q(function (resolve, reject) {
        RestFulResponse.withAuth(LoggedUserFactory.getLoggedUser()).then(function (Restful) {
          Restful.all('posts').post(post).then(function (postResponse) {
              resolve(postResponse);
            }
          ).catch(function (err) {
            reject(err);
          });
        }).catch(reject);
      });
    }

    this.sendVote =  function(post, value) {

      return $q(function(response, reject){
        post.all('votes').customPUT({value: value}).then(function() {
          post.totalLikes -= post.userVote;
          post.userVote = value;
          post.totalLikes += post.userVote;
          response(post);
        }).catch(reject);
      });
    }

    this.toggleBookmark = function(post) {

      var loggedUser = LoggedUserFactory.getLoggedUser();

      return $q(function(response, reject){
        RestFulResponse.withAuth(loggedUser).then(function(Restangular) {

          var rest = Restangular.all('user').one('bookmarked', post.id);

          if(post.hasUserBookmarked){
            rest.remove().then(function() {
              post.hasUserBookmarked = false;
              response(post);
            }).catch(reject);
          }
          else {
            rest.customPUT({}).then(function() {
              post.hasUserBookmarked = true;
              response(post);
            }).catch(reject);
          }

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

        }).catch(function(response) { reject({status: response.status, message: 'PostService: FetchPost'})
        });
      }).catch(reject);
    });
    }
  });
});
