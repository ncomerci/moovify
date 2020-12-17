'use strict';
define(['frontend', 'uikit', 'services/LoginService', 'services/utilities/PageTitleService', 'services/entities/UserService'], function(frontend, UIkit) {

  frontend.controller('IndexCtrl', function($scope, LoggedUserFactory, LoginService, $location, UserService,
                                            PageTitle) {

    $scope.loggedUser = LoggedUserFactory.getLoggedUser();
    $scope.title = PageTitle.getTitle();

    $scope.search = {query: ''};

    $scope.waitLogin = true;
    LoggedUserFactory.startLoggedUserCheck();
    //TODO tobi
    LoginService.refreshToken().then(function (resp) {
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
      UserService.resendConfirmEmail($scope.loggedUser).then(function () {
        $scope.resendSuccess = true;
      }).catch(function (err) {
        $scope.resendError = true;
        console.log(err);
      });
    }

    $scope.gotIt = function () {
      UIkit.modal(document.getElementById('confirm-email-modal')).hide();
      $location.path('/user');
    }

    $scope.hasRole = UserService.userHasRole;

    $scope.modalClose = function (path) {
      UIkit.modal(document.getElementById('no-user-modal')).hide();
      $location.path(path);
    }

  });

});
