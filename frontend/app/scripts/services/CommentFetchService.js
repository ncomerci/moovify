'use strict';
define(['frontend', 'services/RestFulResponseFactory', 'services/LoginService'], function(frontend) {

  frontend.service('CommentFetchService', function (RestFulResponse, $q, LoggedUserFactory) {

    // devolver comments con sus comments hijos cargados.

    this.getPostCommentsWithUserVote = function (postId, userId, depth, orderBy, pageSize, pageNumber) {

      var queryParams = {orderBy: orderBy, pageSize: pageSize, pageNumber: pageNumber};

      return $q(function(resolve, reject) {

        RestFulResponse.withAuthIfPossible(LoggedUserFactory.getLoggedUser())
          .then(function (Restangular){
            Restangular.one('posts', postId).all('comments').getList(queryParams).then(function (response){
              handleCommentResponse(response, userId, depth, orderBy, pageSize, pageNumber, resolve, reject);
            }).catch(reject);
          }).catch(reject);

      });
    };

    this.getCommentCommentsWithUserVote = function (commentId, userId, depth, orderBy, pageSize, pageNumber){
      return getCommentCommentsWithUserVoteInternal(commentId, userId, depth, orderBy, pageSize, pageNumber)
    }

    function getCommentCommentsWithUserVoteInternal(commentId, userId, depth, orderBy, pageSize, pageNumber) {
      var queryParams = {orderBy: orderBy, pageSize: pageSize, pageNumber: pageNumber};

      return $q(function(resolve, reject) {

        RestFulResponse.withAuthIfPossible(LoggedUserFactory.getLoggedUser()).then(function (Restangular){
          Restangular.one('comments', commentId).all('children').getList(queryParams).then(function (response) {
            handleCommentResponse(response, userId, depth, orderBy, pageSize, pageNumber, resolve, reject);
          }).catch(reject);
        }).catch(reject);

      });
    }

    function handleCommentResponse(response, userId, depth, orderBy, pageSize, pageNumber, resolve, reject) {

      var comments = response.data.plain();

      var promiseArray = comments.map(function (comment) {
        return loadChildCommentsAndUserVotePromise(comment, userId, depth, orderBy, pageSize, pageNumber);
      });

      Promise.all(promiseArray).then(resolve).catch(reject);
    }

    function loadChildCommentsAndUserVotePromise(comment, userId, depth, orderBy, pageSize, pageNumber) {

      return new Promise(function(resolve, reject) {

        var votePromise = null;
        var childComments = null;

        // if(userId && comment.enabled) {
        //   votePromise = RestFulResponse.noAuth().one('comments', comment.id).one('votes', userId).get();
        // }

        if(depth > 0) {
          childComments = getCommentCommentsWithUserVoteInternal(comment.id, userId, depth - 1, orderBy, pageSize, pageNumber);
        }

        Promise.all([votePromise, childComments]).then(function (responseArray){

            var userVote = responseArray[0];
            var comments = responseArray[1];

            if(userVote) {
              comment.userVote = userVote.data.plain();
            }

            if(comments) {
              comment.children = comments;
            }
            else {
              comment.children = [];
            }

            resolve(comment);

        }).catch(reject);
      })
    }

  });
});
