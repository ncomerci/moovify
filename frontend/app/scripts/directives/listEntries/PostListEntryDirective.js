'use strict';
define(['frontend', 'services/UserService', 'directives/PrettyDateDirective',
  'services/utilities/RestFulResponseFactory','services/LoginService'], function(frontend) {

  frontend.directive('postListEntryDirective', function(LoggedUserFactory, RestFulResponse, UserService, $locale, $q) {
    return {
      restrict: 'E',
      scope: {
        post: '=',
        adminControls:'<',
        removePostFn:'&'
      },
      templateUrl: 'resources/views/directives/listEntries/postListEntryDirective.html',
      link: function (scope) {
        if(scope.removePostFn)
          scope.removePostFn = scope.removePostFn();
      },
      controller: function ($scope, $q) {

        $scope.categoryMap = {
          "watchlist": "{{'WATCHLIST' | translate }}",
          "critique":"{{'CRITIQUE' | translate }}",
          "debate":"{{'DEBATE' | translate }}",
          "news":"{{'NEWS' | translate }}"
        }

        if($locale.id === 'es'){
          $scope.moviesDiscussedForm = {
            1: 'Película discutida:',
            other:'Películas discutidas:'
          }
        }
        else{
          $scope.moviesDiscussedForm = {
            1: 'Movie discussed:',
            other:'Movies discussed:'
          }
        }

        $scope.loggedUser = LoggedUserFactory.getLoggedUser();

        $scope.isAdmin = function (user){
          return UserService.userHasRole(user, 'ADMIN');
        }

        $scope.getCategory = function (){
          return $scope.categoryMap[$scope.post.postCategory.name];
        }

        $scope.recoverPost = function () {
          return $q(function (resolve, reject) {
            RestFulResponse.withAuthIfPossible($scope.loggedUser).then(function (Restangular) {
              Restangular.one('posts', $scope.post.id).all('enabled').doPUT().then(function () {
                $scope.removePostFn($scope.post);
                resolve($scope.user);
              }).catch(reject);
            }).catch(reject);
          });
        }
      }
    }
  });

});
