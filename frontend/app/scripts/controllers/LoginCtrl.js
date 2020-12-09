define(['frontend', 'services/LoginService', 'services/PageTitleService'], function(frontend) {

    'use strict';
    frontend.controller('LoginCtrl', function($scope, LoggedUserFactory, $window, PageTitle) {
      PageTitle.setTitle('USER_LOGIN_TITLE')
      $scope.login = function (user) {
        LoggedUserFactory.login(user).then(function () {
          $window.history.back();
        });
      }
    });

});
