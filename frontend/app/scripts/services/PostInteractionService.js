'use strict';
define(['frontend', 'services/utilities/RestFulResponseFactory', 'services/LoginService'], function(frontend) {

  frontend.service('PostInteractionService', function($q, RestFulResponse, LoggedUserFactory) {

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

  });
});
