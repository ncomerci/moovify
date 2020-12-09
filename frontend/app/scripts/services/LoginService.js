'use strict';
define(['frontend'], function(frontend) {

  frontend.factory('LoggedUserFactory', function(Restangular, $window) {
    let loggedUser = {
      logged: false
    };
    const LoggedUserFactory =  {
      getLoggedUser: function () {
        return loggedUser;
      },
      saveToken: function (token) {
        return new Promise((resolve, reject) => {
          Restangular.setDefaultHeaders({authorization: token});
          Restangular.one("user").get().then(function (user) {
            loggedUser = Object.assign(loggedUser, user);
            loggedUser.logged = true;
            resolve(loggedUser);
          }).catch(err => {
            $window.localStorage.removeItem("authorization");
            Restangular.setDefaultHeaders({});
          });
        });
      },
      login: function (user, remember) {
        return new Promise((resolve, reject) => {
          Restangular.setFullResponse(true).all("user").post(user).then(function(resp) {
            LoggedUserFactory.saveToken(resp.headers("authorization")).then(r => resolve(r));
            if(remember) {
              $window.localStorage.setItem("authorization", resp.headers("authorization"));
            }
          }).catch(reject);
        })
      }
    }

    return LoggedUserFactory;
  });
});
