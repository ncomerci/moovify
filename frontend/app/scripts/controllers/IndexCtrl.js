'use strict';
define(['frontend'], function(frontend) {

	frontend.controller('IndexCtrl', function($scope, Restangular) {
		Restangular.one('posts', 1).get().then(n => console.log(n.title));
		$scope.welcomeText = 'Welcome to your frontend page';
	});
});
