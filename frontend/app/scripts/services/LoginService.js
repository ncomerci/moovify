'use strict';
define(['frontend'], function(frontend) {

  frontend.factory('LoggedUserFactory', function(Restangular, $window) {
    let loggedUser = {
      logged: false,
    };
    let mutex = {
      value: false
    }

    const LoggedUserFactory =  {
      getLoggedUser: function () {
        return loggedUser;
      },
      saveToken: function (token) {
        return new Promise((resolve, reject) => {
          Restangular.setDefaultHeaders({authorization: token});
          mutex.value = true;
          Restangular.one("user").get().then(function (user) {
            Object.assign(loggedUser, user.data ? user.data : user);
            loggedUser.logged = true;
            mutex.value = false;
            resolve(loggedUser);
          }).catch(err => {
            mutex.value = false;
            $window.localStorage.removeItem("authorization");
            Restangular.setDefaultHeaders({});
            reject(err);
          });
        });
      },
      login: function (user, remember) {
        return new Promise((resolve, reject) => {
          mutex.value = true;
          Restangular.setFullResponse(true).all("user").post(user).then(function(resp) {
            LoggedUserFactory.saveToken(resp.headers("authorization")).then(r => resolve(r));
            if(remember) {
              $window.localStorage.setItem("authorization", resp.headers("authorization"));
            }
          }).catch(err => {
            mutex.value = false;
            reject(err);
          });
        })
      },
      isLogged: function () {
        return new Promise((resolve, reject) => {

          const f = function() {
            if (!mutex.value) {
              return resolve(loggedUser.logged);
            }
            setTimeout(f, 50);
          };

          f();

        })
      }
    }

    return LoggedUserFactory;
  });
});
