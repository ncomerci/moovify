'use strict';
define(['frontend', 'services/LoginService', 'services/utilities/PageTitleService', 'services/utilities/RestFulResponseFactory'], function(frontend) {

  frontend.controller('IndexCtrl', function($scope, LoggedUserFactory, $route, PageTitle, $window, RestFulResponse) {

    $scope.loggedUser = LoggedUserFactory.getLoggedUser();
    PageTitle.setTitle('asdasd'); // TODO: cambiar key
    $scope.title = PageTitle.getTitle();

    $scope.waitLogin = true;
    LoggedUserFactory.startLoggedUserCheck();
    RestFulResponse.noAuth().all('/user/refresh_token').post().then(function (resp) {
      LoggedUserFactory.saveToken(resp.headers("authorization")).then(function (user) {
        $scope.loggedUser = user;
        $scope.waitLogin = false;
      }).catch(console.log);
    }).catch(function () {
      LoggedUserFactory.finishLoggedUserCheck();
      $scope.waitLogin = false;
    });

    $scope.logout = LoggedUserFactory.logout

  });

});
