'use strict';
define(['frontend', 'uikit', 'services/LoginService', 'services/utilities/PageTitleService',
  'services/utilities/RestFulResponseFactory'], function(frontend, UIkit) {

  frontend.controller('IndexCtrl', function($scope, LoggedUserFactory, $location, PageTitle, $window, RestFulResponse) {

    $scope.loggedUser = LoggedUserFactory.getLoggedUser();
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
      $location.search({});
      $location.path('search');
      $location.search('query', $scope.search.query);
      $scope.search.query = '';
    }

    $scope.logout = LoggedUserFactory.logout

    $scope.resendEmail = function () {
      RestFulResponse.withAuth($scope.loggedUser).then(function (r) {
        r.all('/user/email_confirmation').post().then(function () {
          $scope.resendSuccess = true;
        }).catch(function (err) {
          $scope.resendError = true;
          console.log(err);
        });
      });
    }

    $scope.gotIt = function () {
      UIkit.modal(document.getElementById('confirm-email-modal')).hide();
      $location.path('/user');
    }

  });

});
