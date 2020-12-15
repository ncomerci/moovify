'use strict';
define(['frontend', 'uikit', 'services/LoginService', 'services/utilities/PageTitleService',
  'services/utilities/RestFulResponseFactory'], function(frontend, UIkit) {

  frontend.controller('IndexCtrl', function($scope, LoggedUserFactory, $location, PageTitle, $window, RestFulResponse) {

    $scope.loggedUser = LoggedUserFactory.getLoggedUser();
    PageTitle.setTitle('asdasd'); // TODO: cambiar key
    $scope.title = PageTitle.getTitle();

    $scope.search = {query: ''};

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

    $scope.execSearch = function () {
      document.activeElement.blur();
      UIkit.drop(document.getElementById('navbar-search-drop')).hide(0);
      $location.path('search');
      $location.search('query', $scope.search.query);
      $scope.search.query = '';
    }

    $scope.logout = LoggedUserFactory.logout

  });

});
