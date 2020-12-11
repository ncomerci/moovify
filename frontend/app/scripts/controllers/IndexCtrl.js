'use strict';
define(['frontend', 'services/LoginService', 'services/PageTitleService'], function(frontend) {

  function finishInit($scope) {
    $scope.logout = function () {
      const loggedUser = LoggedUserFactory.getLoggedUser();
      let aux = {
        logged: false
      };
      Object.assign(loggedUser, aux);
      Restangular.setDefaultHeaders({});
      $window.localStorage.removeItem("authorization");
    }
  }

	frontend.controller('IndexCtrl', function($scope, LoggedUserFactory, $route, PageTitle, $window) {

		$scope.loggedUser = LoggedUserFactory.getLoggedUser();
		PageTitle.setTitle('asdasd'); // TODO: cambiar key
    $scope.title = PageTitle.getTitle();

    var token = $window.localStorage.getItem("authorization");

    if(token) {
      LoggedUserFactory.saveToken(token).then(function () {
          finishInit($scope);
        }
      );
    }
    else {
      finishInit($scope);
    }

	});

});
