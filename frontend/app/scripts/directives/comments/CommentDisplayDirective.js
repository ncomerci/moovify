'use strict';
define(['frontend', 'directives/comments/CommentTreeDirective', 'services/CommentInteractionService',
  'services/fetch/CommentFetchService', 'directives/comments/CommentReplyDirective', 'directives/comments/EditableCommentBodyDirective',
  'directives/comments/CommentLikeHandlerDirective', 'services/UserService', 'services/LoginService'], function(frontend) {

  frontend.directive('commentDisplayDirective', function (){

    return {
      restrict: 'E',
      scope: {
        comment: '='
      },
      templateUrl:'resources/views/directives/comments/commentDisplayDirective.html',
      link: function(scope) {
        scope.writtingReply = {value: false};
        scope.showChildren = {value: false};
      },
      controller: function($scope, CommentInteractionService, CommentFetchService, $q, UserService, LoggedUserFactory) {

        $scope.isUser = false;
        $scope.isAdmin = false;
        var loggedUser = LoggedUserFactory.getLoggedUser();

        if(loggedUser.logged) {
          $scope.isAdmin = UserService.userHasRole(loggedUser, 'ADMIN');
          $scope.isUser = UserService.userHasRole(loggedUser, 'USER');
        }

        if(!$scope.comment.childrenFetched){
          CommentFetchService.getCommentCommentsWithUserVote($scope.comment.id).then(function (comment) {
            $scope.comment.children = comment;
          })
        }

        $scope.hasChildren = function () {
          return Array.isArray($scope.comment.children) && $scope.comment.children.length > 0;
        }

        $scope.callbackFunctions = {};

        $scope.callbackFunctions.vote = function(value) {

          if($scope.comment.userVote === value) {
            value = 0;
          }

          return $q(function(resolve, reject) {
            CommentInteractionService.sendVote($scope.comment, value).then(function(comment) {
              Object.assign($scope.comment, comment);
              resolve(comment.userVote);
            }).catch(console.log);
          });
        }

        $scope.callbackFunctions.reply = function(newCommentBody) {
          return $q(function (resolve, reject) {

            CommentInteractionService.sendReply($scope.comment, newCommentBody).then(function(newComment) {
              // debugger;
              if(Array.isArray($scope.comment.children)){
                $scope.comment.children.unshift(newComment);
              }
              else {
                $scope.comment.children = [newComment];
              }

              $scope.showChildren.value = true;
              resolve(newComment);

            }).catch(reject);
          });
        }

        $scope.callbackFunctions.edit = function(newBody) {

          $scope.comment.body = newBody;
          return $scope.comment.put();
        }

        $scope.deleteComment = function () {

          $q.resolve($scope.comment.all('enabled').remove()).then(function(response) {
            $scope.comment.enabled = false;
          }).catch(console.log);
        }
      }
    }
  });
});
