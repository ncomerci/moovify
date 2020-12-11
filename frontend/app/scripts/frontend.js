'use strict';
define(['routes',
	'services/dependencyResolverFor',
	'i18n/i18nLoader!',
	'uikit',
	'uikiticons',
	'angular',
	'angular-route',
	'angular-translate',
	'restangular',
  'iconify'
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

					// El link del url viene con localhost:8080 :C
					// RestangularProvider.setRestangularFields({
          //   selfLink: 'url'
          // });
          RestangularProvider.setResponseExtractor(function(response) {
            var newResponse = response;
            if (angular.isArray(response)) {
              angular.forEach(newResponse, function(value, key) {
                newResponse[key].originalElement = angular.copy(value);
              });
            } else if(newResponse) {
              newResponse.originalElement = angular.copy(response);
            }

            return newResponse;
          });

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
								resolve: route.controller ? dependencyResolverFor(['controllers/' + route.controller]) : () => {},
								controller: route.controller,
								gaPageTitle: route.gaPageTitle,
                reloadOnSearch: false
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
