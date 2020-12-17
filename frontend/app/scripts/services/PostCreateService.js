'use strict';
define(['frontend', 'services/utilities/RestFulResponseFactory'], function(frontend) {

  frontend.factory('PostCreateService', function(RestFulResponse, LoggedUserFactory, $q){
  return {
    createPost: function (post){
      console.log(post);
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
  }
  });
});
