'use strict';
define(['frontend'], function(frontend) {

	frontend.directive('sample', function() {
		return {
			restrict: 'E',
			template: '<span>Sample</span>'
		};
	});
});
