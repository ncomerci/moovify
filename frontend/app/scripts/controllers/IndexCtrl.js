'use strict';
define(['frontend'], function(frontend) {

	frontend.controller('IndexCtrl', function($scope, Restangular) {
		$scope.welcomeText = 'Welcome to your frontend page';
	});
});
