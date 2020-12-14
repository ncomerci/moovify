'use strict';
define(['frontend', 'services/RestFulResponseFactory', 'services/LoginService'], function(frontend) {

  frontend.service('CommentInteractionService', function($q, RestFulResponse, LoggedUserFactory) {

    this.sendVote =  function(comment, value) {

      return $q(function(response, reject){
        RestFulResponse.withAuth(LoggedUserFactory.getLoggedUser()).then(function(Restangular) {
          Restangular.one('comments', comment.id).all('votes').customPUT({value: value}).then(function() {
            comment.userVote = value;
            response(comment);
          }).catch(reject);
        }).catch(reject);
      })
    }

    this.sendReply =  function(postId, commentId, newCommentBody) {

      var loggedUser = LoggedUserFactory.getLoggedUser();

      if(!loggedUser.logged){
        return $q.reject({message: 'User not logged'});
      }

      var postBody = {
        commentBody: newCommentBody,
        postId: postId,
        parentId: commentId,
        userId: loggedUser.id
      }

      return $q(function(resolve, reject){
        RestFulResponse.withAuth(LoggedUserFactory.getLoggedUser()).then(function(Restangular) {
          Restangular.all('comments').post(postBody).then(function(response) {

              var id = new URL(response.headers('Location')).pathname.match(/\d*$/);

              Restangular.one('comments', id).get().then(function(response) {
                resolve(response.data.plain());
              }).catch(reject);
            }).catch(reject);
          }).catch(reject);
      });
    }
  });
});
