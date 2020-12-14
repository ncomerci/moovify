define(['frontend', 'services/LoginService', 'services/utilities/PageTitleService'], function(frontend) {

    'use strict';
    frontend.controller('LoginCtrl', function($scope, LoggedUserFactory, $location, PageTitle) {
      PageTitle.setTitle('USER_LOGIN_TITLE')

      $scope.loginBtnPressed = false;
      $scope.loginError = false;

      if(LoggedUserFactory.getLoggedUser().logged) {
        $location.path('/');
      }

      $scope.login = function (user) {
        $scope.loginBtnPressed = true;
        LoggedUserFactory.login(user).then(function () {
          $location.path('/');
        }).catch(function() {
          $scope.loginError = true;
          $scope.$apply();
        });
      };
    });
});
