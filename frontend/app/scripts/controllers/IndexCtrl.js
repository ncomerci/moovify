'use strict';
define(['frontend', 'services/LoginService', 'services/PageTitleService'], function(frontend) {

	frontend.controller('IndexCtrl', function($scope, LoggedUserFactory, $route, PageTitle) {
		$scope.welcomeText = 'Welcome to your frontend page';
		$scope.loggedUser = LoggedUserFactory.getLoggedUser();
		PageTitle.setTitle('asdasd'); // TODO: cambiar key
    $scope.$on('$routeChangeSuccess', function () {
      $scope.title = PageTitle.getTitle();

    });
	});

});
