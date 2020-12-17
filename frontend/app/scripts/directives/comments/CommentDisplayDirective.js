'use strict';
define(['frontend', 'uikit', 'directives/comments/CommentTreeDirective', 'services/CommentInteractionService',
  'services/fetch/CommentFetchService', 'directives/comments/CommentReplyDirective', 'directives/comments/EditableCommentBodyDirective',
  'directives/comments/CommentLikeHandlerDirective', 'services/UserService', 'services/LoginService', 'directives/PrettyDateDirective'], function(frontend, UIkit) {

  frontend.directive('commentDisplayDirective', function (){

    return {
      restrict: 'E',
      scope: {
        comment: '=',
        depth: '='
      },
      templateUrl:'resources/views/directives/comments/commentDisplayDirective.html',
      link: function(scope) {
        scope.writtingReply = {value: false};
        scope.showChildren = {value: false};

        scope.maxDepth = 5;

      },
      controller: function($scope, CommentInteractionService, CommentFetchService, $q, UserService, LoggedUserFactory) {

        $scope.isUser = false;
        $scope.isAdmin = false;
        $scope.deletingComment = false;
        $scope.sendingDelete = {value: false};
        $scope.currentChildren = 0;

        var loggedUser = LoggedUserFactory.getLoggedUser();

        if(loggedUser.logged) {
          $scope.isAdmin = UserService.userHasRole(loggedUser, 'ADMIN');
          $scope.isUser = UserService.userHasRole(loggedUser, 'USER');
        }

        if(!$scope.comment.childrenFetched && $scope.depth < 5) {
          CommentFetchService.getCommentCommentsWithUserVote($scope.comment).then(function (comment) {
            $scope.comment.children = comment;
            $scope.currentChildren = $scope.comment.children.length;
          })
        }

        $scope.hasChildren = function () {
          return Array.isArray($scope.comment.children) && $scope.comment.children.length > 0;
        }

        $scope.callbackFunctions = {
          startEdit: {},
          startReply: {}
        };

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

            CommentInteractionService.sendCommentReply($scope.comment, newCommentBody).then(function(newComment) {
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

        $scope.openGoToViewModal = function () {
          $scope.tryingReply = true;
          UIkit.modal(document.getElementById('go-to-comment-view-modal-' + $scope.comment.id)).show();
        }

        $scope.openDeleteModal = function () {
          $scope.deletingComment = true;
          UIkit.modal(document.getElementById('delete-comment-modal-' + $scope.comment.id)).show();
        }

        $scope.confirmDelete = function (){
          $scope.sendingDelete.value = true;
          $q.resolve($scope.comment.all('enabled').remove()).then(function() {
            $scope.sendingDelete.value = false;
            $scope.comment.enabled = false;
            UIkit.modal(document.getElementById('delete-comment-modal-' + $scope.comment.id)).hide();
          }).catch(console.log);
        }
      }
    }
  });
});
