'use strict';
define(['frontend'], function(frontend) {

  frontend.factory('LoggedUserFactory', function(Restangular) {
    let loggedUser = {
      logged: false
    };
    return {
      getLoggedUser: function () {
        return loggedUser;
      },
      login: function (user) {
        return new Promise((resolve, reject) => {
          Restangular.setFullResponse(true).all("user").post(user).then(function(resp) {
            Restangular.setDefaultHeaders({authorization: resp.headers("authorization")});
            Restangular.one("user").get().then(function (user) {
              loggedUser = Object.assign(loggedUser, user);
              loggedUser.logged = true;
              resolve(loggedUser);
            });
          }).catch(reject);
        })
      }
    }
  });
});
