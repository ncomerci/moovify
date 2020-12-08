'use strict';
define(['frontend', 'services/LoginService'], function(frontend) {

	frontend.controller('IndexCtrl', function($scope, LoggedUserFactory) {
		$scope.welcomeText = 'Welcome to your frontend page';
		$scope.loggedUser = LoggedUserFactory.getLoggedUser();

	});

});
