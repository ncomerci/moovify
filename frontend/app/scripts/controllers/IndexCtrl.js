'use strict';
define(['frontend', 'services/LoginService', 'services/PageTitleService', 'services/RestFulResponseFactory'], function(frontend) {

  frontend.controller('IndexCtrl', function($scope, LoggedUserFactory, $route, PageTitle, $window, RestFulResponse) {

    $scope.loggedUser = LoggedUserFactory.getLoggedUser();
    PageTitle.setTitle('asdasd'); // TODO: cambiar key
    $scope.title = PageTitle.getTitle();

    LoggedUserFactory.startLoggedUserCheck();
    RestFulResponse.noAuth().all('/user/refresh_token').post().then(function (resp) {
      LoggedUserFactory.saveToken(resp.headers("authorization")).then(function (user) {
        $scope.loggedUser = user;
      });
    }).catch(function () {
      LoggedUserFactory.finishLoggedUserCheck();
    });

    $scope.logout = LoggedUserFactory.logout

  });

});
