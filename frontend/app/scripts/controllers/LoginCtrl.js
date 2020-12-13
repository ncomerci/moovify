define(['frontend', 'services/LoginService', 'services/PageTitleService'], function(frontend) {

    'use strict';
    frontend.controller('LoginCtrl', function($scope, LoggedUserFactory, $window, PageTitle) {
      PageTitle.setTitle('USER_LOGIN_TITLE')

      $scope.loginBtnPressed = false;
      $scope.loginError = false;

      LoggedUserFactory.isLogged().then(function(resp) {
        if(resp) {
          $window.location.href = '/';
        }
      });

      $scope.login = function (user) {
        $scope.loginBtnPressed = true;
        LoggedUserFactory.login(user).then(function () {
          $window.history.back();
        }).catch(function() {
          $scope.loginError = true;
          $scope.$apply();
        });
      };
    });
});
