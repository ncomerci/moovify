'use strict';
define(['frontend'], function(frontend) {

	frontend.directive('sample', function() {
		return {
			restrict: 'E',
			templateUrl: 'index.html'
		};
	});
});
