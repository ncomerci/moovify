'use strict';
define(['frontend', 'services/LoginService', 'services/PageTitleService'], function(frontend) {

	frontend.controller('IndexCtrl', async function($scope, LoggedUserFactory, $route, PageTitle, $window, Restangular) {
		$scope.welcomeText = 'Welcome to your frontend page';
		$scope.loggedUser = LoggedUserFactory.getLoggedUser();
		PageTitle.setTitle('asdasd'); // TODO: cambiar key
    $scope.title = PageTitle.getTitle();

    const token = $window.localStorage.getItem("authorization");
    if(token) {
      await LoggedUserFactory.saveToken(token);
    }

    $scope.logout = function () {
      const loggedUser = LoggedUserFactory.getLoggedUser();
      let aux = {
        logged: false
      };
      Object.assign(loggedUser, aux);
      Restangular.setDefaultHeaders({});
      $window.localStorage.removeItem("authorization");
    }

	});

});
