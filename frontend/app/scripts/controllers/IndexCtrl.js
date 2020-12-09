'use strict';
define(['frontend', 'services/LoginService', 'services/PageTitleService'], function(frontend) {

	frontend.controller('IndexCtrl', function($scope, LoggedUserFactory, $route, PageTitle, $window) {
		$scope.welcomeText = 'Welcome to your frontend page';
		$scope.loggedUser = LoggedUserFactory.getLoggedUser();
		PageTitle.setTitle('asdasd'); // TODO: cambiar key
    $scope.title = PageTitle.getTitle();

    const token = $window.localStorage.getItem("authorization");
    if(token) {
      LoggedUserFactory.saveToken(token);
    }

	});

});
