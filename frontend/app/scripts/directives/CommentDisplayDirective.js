'use strict';
define(['frontend', 'directives/CommentTreeDirective', 'services/CommentInteractionService', 'directives/CommentReplyDirective', 'directives/CommentLikeHandlerDirective'], function(frontend) {

  frontend.directive('commentDisplayDirective', function (){

    return {
      restrict: 'E',
      scope: {
        comment: '='
      },
      templateUrl:'resources/views/directives/commentDisplayDirective.html',
      link: function(scope) {
        scope.writtingReply = {value: false};
      },
      controller: function($scope, CommentInteractionService, $q) {

        $scope.hasChildren = function () {
          return Array.isArray($scope.comment.children) && $scope.comment.children.length > 0;
        }


        $scope.sendVote = function(value) {

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

        $scope.sendComment = {};
        $scope.sendComment.fn = function(content) {
          return $q(function (resolve, reject) {
            CommentInteractionService.sendReply($scope.comment.originalElement.post.id, $scope.comment.id, content).then(function(newComment) {
              // debugger;
              if(Array.isArray($scope.comment.children)){
                $scope.comment.children.unshift(newComment);
              }
              else {
                $scope.comment.children = [newComment];
              }

              resolve(newComment);
            }).catch(reject);
          });
        }
      }
    }
  });
});
