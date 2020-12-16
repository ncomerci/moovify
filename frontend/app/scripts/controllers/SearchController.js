'use strict';
define(['frontend', 'uikit', 'directives/search/SearchMoviesDirective', 'directives/search/SearchUsersDirective',
  'directives/search/SearchPostsDirective', 'directives/TabDisplayDirective', 'services/utilities/PageTitleService'], function(frontend) {

  var defaultType = 'posts';

    frontend.controller('SearchController', function($scope, $routeParams, $location, PageTitle) {
      PageTitle.setTitle('SEARCH_TITLE')
      $scope.query = {
        value: $routeParams.query ? $routeParams.query : ''
      };

      $scope.searchOptions = {
        contentType: $routeParams.showing
      };

      $scope.tabs = [
        {value:'posts', message:"{{'POST_TAB_DISPLAY' | translate }}"},
        {value:'movies', message:"{{'MOVIE_TAB_DISPLAY' | translate }}"},
        {value:'users', message:"{{'USER_TAB_DISPLAY' | translate }}"}
      ];

      $scope.setPostSearchUrl = null;
      $scope.setUserSearchUrl = null;
      $scope.setMovieSearchUrl = null;

      if(!$scope.searchOptions.contentType) {
        $scope.searchOptions.contentType = defaultType;
        $location.search('showing', defaultType);
      }

      $scope.$watch('searchOptions.contentType', function(newParam, oldParam, scope) {

        if(newParam !== oldParam) {
          $location.search({ showing: scope.searchOptions.contentType });

          if(newParam === 'posts' && scope.setPostSearchUrl !== null){
            scope.setPostSearchUrl();
          }
          else if(newParam === 'users' && scope.setUserSearchUrl !== null){
            scope.setUserSearchUrl();
          }
          else if(newParam === 'movies' && scope.setMovieSearchUrl !== null){
            scope.setMovieSearchUrl();
          }

        }

      }, true);

    });

});
