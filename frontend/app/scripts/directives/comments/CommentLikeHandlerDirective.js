'use strict';
define(['frontend', 'services/LoginService', 'services/UserService'], function(frontend) {

  frontend.directive('commentLikeHandlerDirective', function() {

    return {
      restrict: 'E',
      scope: {
        userVote: '=?',
        totalVotes: '=',
        sendVoteFn: '&'
      },
      templateUrl:'resources/views/directives/comments/commentLikeHandlerDirective.html',
      link: function (scope){
        scope.sendVoteFn = scope.sendVoteFn();
        scope.sendingVote = false;
      },
      controller: function ($scope, UserService, LoggedUserFactory){

        $scope.isUser = false;
        var loggedUser = LoggedUserFactory.getLoggedUser();

        if(loggedUser.logged) {
          $scope.isUser = UserService.userHasRole(loggedUser, 'USER');
        }
        else {
          $scope.userVote = 0;
        }

        console.log('Comment like directive', $scope);

        $scope.sendVote = function (value){

          console.log('Comment like directive', $scope);

          if(!$scope.isUser){
            return;
          }

          $scope.sendingVote = true;
          $scope.sendVoteFn(value).then(function () {
            $scope.sendingVote = false;
          }).catch(console.log);
        }
      }
    }
  });
});
