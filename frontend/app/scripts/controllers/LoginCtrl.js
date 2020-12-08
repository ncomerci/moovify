define(['frontend', 'services/LoginService'], function(frontend) {

    'use strict';
    frontend.controller('LoginCtrl', function($scope, LoggedUserFactory, $window) {
      $scope.login = function (user) {
        LoggedUserFactory.login(user).then(function () {
          $window.history.back();
        });
      }
    });

});
