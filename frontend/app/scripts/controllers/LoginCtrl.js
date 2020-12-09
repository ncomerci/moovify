'use strict';
define(['frontend', 'services/LoginService', 'services/PageTitleService'], function(frontend) {

    'use strict';
    frontend.controller('LoginCtrl', function($scope, LoggedUserFactory, $window, PageTitle, $location) {
      PageTitle.setTitle('USER_LOGIN_TITLE')
      const loggedUser = LoggedUserFactory.getLoggedUser();

      LoggedUserFactory.isLogged().then(resp => {
        if(resp) {
          $window.location.href = '/';
        }
      });

      $scope.login = function (user) {
        LoggedUserFactory.login(user, $scope.checkValue).then(function () {
          $window.history.back();
        });
      }
    });
});
