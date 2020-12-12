'use strict';
define(['frontend', 'services/RestFulResponseFactory'], function(frontend) {

  frontend.factory('LoggedUserFactory', function(RestFulResponse, $window, $q) {
    var loggedUser = {
      logged: false,
      expDate: undefined
    };
    var mutex = {
      value: false
    }

    return {
      getLoggedUser: function () {
        return loggedUser;
      },
      /*saveToken: function (token) {
        console.log('Date', new Date(RestFulResponse.parseToken(token).exp * 1000))
        return $q(function(resolve, reject) {
          RestFulResponse.setDefaultHeaders({authorization: token});
          mutex.value = true;
          RestFulResponse.one("user").get().then(function (user) {
            Object.assign(loggedUser, user.data ? user.data : user);
            loggedUser.logged = true;
            mutex.value = false;
            resolve(loggedUser);
          }).catch(function(err) {
            mutex.value = false;
            $window.localStorage.removeItem("authorization");
            RestFulResponse.setDefaultHeaders({});
            reject(err);
          });
        });
      },*/
      login: function (user, remember) {
        return $q(function (resolve, reject) {
          mutex.value = true;
          RestFulResponse.noAuth().all("user").post(user).then(function (resp) {
            loggedUser.expDate = RestFulResponse.setToken(resp.headers("authorization"));
            loggedUser.logged = true;
            RestFulResponse.withAuth(loggedUser).then(function (r) {
              r.one("user").get().then(function (user) {
                Object.assign(loggedUser, user.data ? user.data : user);
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

          }).catch(function (err) {
            mutex.value = false;
            reject(err);
          });
        });
      },
      isLogged: function () {
        return $q(function (resolve, reject) {

          var f = function () {
            if (!mutex.value) {
              return resolve(loggedUser.logged);
            }
            setTimeout(f, 50);
          };

          f();

        });
      }
    };
  });
});
