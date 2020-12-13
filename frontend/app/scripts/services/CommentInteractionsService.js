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

    this.sendReply =  function(newComment) {

      return $q(function(response, reject){
        RestFulResponse.withAuth(LoggedUserFactory.getLoggedUser()).then(function(Restangular) {
          Restangular.all('comments').post(newComment).then(response).catch(reject);
        }).catch(reject);
      });
    }
  });
});
