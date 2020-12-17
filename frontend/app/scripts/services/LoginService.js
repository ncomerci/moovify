'use strict';
define(['frontend', 'services/utilities/RestFulResponseFactory'], function(frontend) {

  frontend.factory('LoggedUserFactory', function(RestFulResponse, $window, $q, $location) {
    var loggedUser = {
      logged: false,
      expDate: undefined,
      roles: []
    };
    var mutex = {
      value: false
    }

    var logoutInternal = function (query) {
      return $q(function (resolve, reject) {
        RestFulResponse.noAuth().one("/user/refresh_token").remove(query).then(function () {
          var aux = {
            logged: false,
            expDate: undefined
          };
          Object.assign(loggedUser, aux);
          RestFulResponse.clearHeaders();
          $location.path('/');
          resolve();
        }).catch(reject)
      });
    }

    var LoggedUserFactory = {
      getLoggedUser: function () {
        return loggedUser;
      },
      saveToken: function (token) {
        return $q(function (resolve, reject) {
          mutex.value = true;
          loggedUser.expDate = RestFulResponse.setToken(token);
          RestFulResponse.withAuth(loggedUser).then(function (r) {
            r.one("user").get().then(function (user) {
              Object.assign(loggedUser, user.data ? user.data : user);
              loggedUser.logged = true;
              mutex.value = false;
              resolve(loggedUser);
            }).catch(function (err) {
              mutex.value = false;
              reject(err);
            });
          }).catch(function (err) {
            mutex.value = false;
            reject(err);
          });

        })
      },

      refreshToken: function () {
        return RestFulResponse.noAuth().all('/user/refresh_token').post();
      },

      login: function (user) {
        return $q(function (resolve, reject) {
          mutex.value = true;
          RestFulResponse.noAuth().all("user").post(user).then(function (resp) {
            LoggedUserFactory.saveToken(resp.headers("authorization")).then(function (r) {
              RestFulResponse.setLogoutHandler(LoggedUserFactory.logout);
              resolve(r);
            }).catch(function (err) {
              mutex.value = false;
              reject(err);
            });
          }).catch(function (err) {
            mutex.value = false;
            reject(err);
          });
        });
      },

      logout: function () {
        return logoutInternal();
      },

      logoutEverywhere: function () {
        return logoutInternal({allSessions: true});
      },

    //  esto es solo debería usarse en index controller
      startLoggedUserCheck: function () {
        mutex.value = true;
      },

      finishLoggedUserCheck: function () {
        mutex.value = false;
      }
    };

    return LoggedUserFactory;
  });
});
