'use strict';
define(['frontend', 'directives/comments/CommentTreeDirective', 'services/CommentInteractionService',
  'services/fetch/CommentFetchService', 'directives/comments/CommentReplyDirective', 'directives/comments/EditableCommentBodyDirective',
  'directives/comments/CommentLikeHandlerDirective'], function(frontend) {

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
      controller: function($scope, CommentInteractionService, CommentFetchService, $q) {

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

        $scope.callbackFunctions.reply = function(content) {
          return $q(function (resolve, reject) {
            CommentInteractionService.sendReply($scope.comment.originalElement.post.id, $scope.comment.id, content).then(function(newComment) {
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
          console.log('Pre and recieved', $scope.comment.body, newBody);
          $scope.comment.body = newBody;
          return $scope.comment.put();
        }
      }
    }
  });
});
