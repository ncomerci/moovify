'use strict';
define(['frontend', 'services/PageTitleService'], function(frontend) {

	frontend.controller('HomeCtrl', function($scope, PageTitle) {
		$scope.homePageText = 'This is your homepage';
    PageTitle.setTitle('asdasd'); // TODO: cambiar key
	});
});
