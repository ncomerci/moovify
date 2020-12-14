'use strict';
define(['frontend', 'services/utilities/RestFulResponseFactory', 'services/LoginService'], function(frontend) {

  frontend.service('CommentFetchService', function (RestFulResponse, $q, LoggedUserFactory) {

    // devolver comments con sus comments hijos cargados.

    this.getPostCommentsWithUserVote = function (postId, depth, orderBy, pageSize, pageNumber) {

      var queryParams = {orderBy: orderBy, pageSize: pageSize, pageNumber: pageNumber};

      return $q(function(resolve, reject) {

        RestFulResponse.withAuthIfPossible(LoggedUserFactory.getLoggedUser())
          .then(function (Restangular){
            Restangular.one('posts', postId).all('comments').getList(queryParams).then(function (response){
              handleCommentResponse(response, depth, orderBy, pageSize, pageNumber, resolve, reject);
            }).catch(reject);
          }).catch(reject);

      });
    };

    this.getCommentCommentsWithUserVote = function (commentId, depth, orderBy, pageSize, pageNumber){

      if(depth)
        depth = 0

      if(!orderBy)
        orderBy = 'hottest';

      if(!pageSize)
        pageSize = 5;

      if(!pageNumber)
        pageNumber = 0;

      return getCommentCommentsWithUserVoteInternal(commentId, depth, orderBy, pageSize, pageNumber)
    }

    function getCommentCommentsWithUserVoteInternal(commentId, depth, orderBy, pageSize, pageNumber) {

      var queryParams = {orderBy: orderBy, pageSize: pageSize, pageNumber: pageNumber};

      return $q(function(resolve, reject) {

        RestFulResponse.withAuthIfPossible(LoggedUserFactory.getLoggedUser()).then(function (Restangular){
          Restangular.one('comments', commentId).all('children').getList(queryParams).then(function (response) {
            handleCommentResponse(response, depth, orderBy, pageSize, pageNumber, resolve, reject);
          }).catch(reject);
        }).catch(reject);

      });
    }

    function handleCommentResponse(response, depth, orderBy, pageSize, pageNumber, resolve, reject) {

      var comments = response.data;

      var promiseArray;

      if(depth > 0) {
        promiseArray = comments.map(function (comment) {
          return loadChildComments(comment, depth, orderBy, pageSize, pageNumber);
        });
        comments.forEach(function (comment) {
          comment.childrenFetched = true;
        });
      }
      else {
        comments.forEach(function (comment) {
          comment.childrenFetched = false;
        });
        promiseArray = [$q.resolve(comments)];
      }

      Promise.all(promiseArray).then().catch(console.log);

      resolve(comments);

    }

    function loadChildComments(comment, depth, orderBy, pageSize, pageNumber) {

      return $q(function(resolve, reject) {

        getCommentCommentsWithUserVoteInternal(comment.id, depth - 1, orderBy, pageSize, pageNumber)
          .then(function (response) {

            if (response) {
              comment.children = response;
            }
            else {
              comment.children = [];
            }

            comment.loadingChildren = false;

            resolve(comment);

          }).catch(reject);

      });
    }
  });
});
