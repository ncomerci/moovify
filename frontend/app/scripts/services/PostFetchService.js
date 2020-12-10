'use strict';
define(['frontend', 'services/RestFulResponseFactory', 'services/LinkParserService'], function(frontend) {

  frontend.service('PostFetchService', function(RestFulResponse, LinkParserService) {

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
        sortCriteria: orderBy,
        pageSize: pageSize ? pageSize : 5,
        pageNumber: pageNumber ? pageNumber : 0
      };

      // TODO error handling
      return new Promise((resolve, reject) => {

        RestFulResponse.all(path).getList(queryParams).then((postResponse) => {

          if(postResponse.status !== 200){
            reject(postResponse);
            return;
          }

          let paginationParams = LinkParserService.parse(postResponse.headers('Link'));

          let posts = postResponse.data;

          let promises = this.$$getMoviesPromiseArray(posts);

          Promise.all(promises).then((response) => {
            resolve({posts: response, paginationParams: paginationParams});
          })
        }).catch(reject);
      });
    }

    this.$$getMoviesPromiseArray = function (posts) {

      return posts.map(post => new Promise(
        (resolve, reject) => RestFulResponse.one('posts', post.id).getList('movies').then((response) => {

          if(response.status !== 200)
            reject(response);

          post.movies = response.data;
          resolve(post);

        }).catch(reject)
      ));
    }

  });
});
