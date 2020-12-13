'use strict';
define(['frontend'], function(frontend) {

  frontend.factory('RestFulResponse', function (Restangular, $q) {

    var ReqFullResponse = Restangular.withConfig(function (RestangularConfigurer) {
      RestangularConfigurer.setFullResponse(true);
    });

    // Source: https://stackoverflow.com/questions/38552003/how-to-decode-jwt-token-in-javascript-without-using-a-library
    var parseToken = function (token) {
      var base64Url = token.split('.')[1];
      var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      var jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));

      return JSON.parse(jsonPayload);
    };

    var RestFulResponse = {

      setToken: function (token) {
        ReqFullResponse.setDefaultHeaders({authorization: token});
        return new Date(parseToken(token).exp * 1000);
      },

      noAuth: function () {
        return ReqFullResponse;
      },

      withAuth: function (loggedUser) {
        return $q(function (resolve, reject) {
          if(new Date() > loggedUser.expDate) {
            ReqFullResponse.all('/user/refresh_token').post().then(function (resp) {
              loggedUser.expDate = RestFulResponse.setToken(resp.headers("authorization"));
              resolve(ReqFullResponse);
            }).catch(function (err) {
              reject(err); // TODO login redirect
            });
          }
          else {
            resolve(ReqFullResponse);
          }
        });
      },

      withAuthIfPossible: function(loggedUser) {

        if(loggedUser.logged){
          return this.withAuth(loggedUser);
        }
        return $q.resolve(this.noAuth());
      },

      clearHeaders: function () {
        ReqFullResponse.setDefaultHeaders({});
      }
    };

    return RestFulResponse;
  });
});
