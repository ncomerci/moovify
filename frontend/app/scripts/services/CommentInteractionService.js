'use strict';
define(['frontend', 'services/utilities/RestFulResponseFactory', 'services/LoginService'], function(frontend) {

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

    this.sendReply = function(comment, newCommentBody) {

    return $q(function(resolve, reject){
      RestFulResponse.withAuth(LoggedUserFactory.getLoggedUser()).then(function(Restangular) {
        comment.all('children').post({body: newCommentBody}).then(function(response) {
            Restangular.oneUrl('comments', response.headers('Location')).get().then(function(response) {
              resolve(response.data);
            }).catch(reject);
          }).catch(reject);
        }).catch(reject);
    });
    }
  });
});
