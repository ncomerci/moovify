'use strict';
define(['frontend'], function(frontend) {

	frontend.directive('navbar', function() {
		return {
			restrict: 'E',
			templateUrl: 'views/navbar.html'
		};
	});
});
