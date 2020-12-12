'use strict';
define(['frontend', 'services/LoginService', 'services/PageTitleService', 'services/RestFulResponseFactory'], function(frontend) {

  frontend.controller('IndexCtrl', function($scope, LoggedUserFactory, $route, PageTitle, $window, RestFulResponse) {

    $scope.loggedUser = LoggedUserFactory.getLoggedUser();
    PageTitle.setTitle('asdasd'); // TODO: cambiar key
    $scope.title = PageTitle.getTitle();

    /*var token = $window.localStorage.getItem("authorization");

    if(token) {
      LoggedUserFactory.saveToken(token).then(/!*nothing to do*!/);
    }*/

    RestFulResponse.noAuth().all('/user/refresh_token').post().then(function (resp) {
      $scope.loggedUser.expDate = this.setToken(resp.headers("authorization"));
    }).catch(function (err) {
      console.log(err);
    });

    $scope.logout = function () {
      var loggedUser = LoggedUserFactory.getLoggedUser();
      var aux = {
        logged: false
      };
      Object.assign(loggedUser, aux);
      RestFulResponse.setDefaultHeaders({});
      $window.localStorage.removeItem("authorization");
    }

  });

});
