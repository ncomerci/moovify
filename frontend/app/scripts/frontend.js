'use strict';
define(['routes',
	'services/dependencyResolverFor',
	'i18n/i18nLoader!',
	'uikit',
	'uikiticons',
	'angular',
	'angular-route',
	'angular-translate',
	'restangular'
	],
	function(config, dependencyResolverFor, i18n, UIkit, icons) {

		// Wire UIkit icons to UIkit handler.
		icons(UIkit);

		var frontend = angular.module('frontend', [
			'ngRoute',
			'pascalprecht.translate',
			'restangular'
		]);
		frontend
			.config(
				['$routeProvider',
				'$controllerProvider',
				'$compileProvider',
				'$filterProvider',
				'$provide',
				'$translateProvider',
				'$locationProvider',
				'RestangularProvider',
				function($routeProvider, $controllerProvider, $compileProvider, $filterProvider, $provide, $translateProvider, $locationProvider, RestangularProvider) {

					RestangularProvider.setBaseUrl('http://localhost/api');
          // RestangularProvider.addResponseInterceptor(function(data, operation, what, url, response, deferred) {
          //   return response;
          // });
					$locationProvider.html5Mode(true);


					frontend.controller = $controllerProvider.register;
					frontend.directive = $compileProvider.directive;
					frontend.filter = $filterProvider.register;
					frontend.factory = $provide.factory;
					frontend.service = $provide.service;

					if (config.routes !== undefined) {
						angular.forEach(config.routes, function(route, path) {
							$routeProvider.when(path, {
								templateUrl: route.templateUrl, 
								resolve: dependencyResolverFor(['controllers/' + route.controller]), 
								controller: route.controller,
								gaPageTitle: route.gaPageTitle
							});
						});
					}
					if (config.defaultRoutePath !== undefined) {
						$routeProvider.otherwise({redirectTo: config.defaultRoutePath});
					}

					$translateProvider.translations('preferredLanguage', i18n);
					$translateProvider.preferredLanguage('preferredLanguage');
				}]);

		return frontend;
	}
);
