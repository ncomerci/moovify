'use strict';
define(['frontend', 'services/entities/PostService', 'services/entities/UserService', 'directives/PrettyDateDirective',
  'services/LoginService'], function(frontend) {

  frontend.directive('postListEntryDirective', function(LoggedUserFactory,UserService, PostService, $locale) {
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
      controller: function ($scope) {

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
          PostService.recoverPost($scope.post).then(function (post) {
            $scope.removePostFn(post);
          }).catch();
        }
      }
    }
  });

});
