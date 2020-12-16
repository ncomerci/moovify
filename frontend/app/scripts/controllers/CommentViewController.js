define(['frontend','uikit', 'services/fetch/CommentFetchService',
  'directives/comments/CommentTreeDirective', 'services/CommentInteractionService', 'services/LoginService', 'services/UserService',
  'services/utilities/PageTitleService'], function(frontend, UIkit) {

  'use strict';
  frontend.controller('CommentViewController', function ($scope, $location, LoggedUserFactory, UserService,
                             CommentFetchService, PageTitle, $q, CommentInteractionService, $routeParams) {

    PageTitle.setTitle('COMMENT_VIEW_TITLE');

    $scope.mainComment = null;
    $scope.comments = null;
    $scope.deletingComment = false;
    $scope.sendingDelete = {value: false};
    $scope.currentChildren = 0;

    $scope.isUser = false;
    $scope.isAdmin = false;

    $scope.mutex = {
      deleting: false
    };
    var loggedUser = LoggedUserFactory.getLoggedUser();
    $scope.isLogged = loggedUser.logged;

    if($scope.isLogged) {
      $scope.isAdmin = UserService.userHasRole(loggedUser, 'ADMIN');
      $scope.isUser = UserService.userHasRole(loggedUser, 'USER');
    }

    var commentId = $routeParams.id;
    var commentDepth = 0;
    var commentsOrder = 'newest';
    var commentsPageSize = 5;
    var commentsPageNumber = 0;


    CommentFetchService.fetchOneComment(commentId).then(function(comment) {
      $scope.mainComment = comment;
      console.log($scope.mainComment);
    }).catch(console.log);

    CommentFetchService.getCommentCommentsWithUserVoteById(commentId, commentDepth, commentsOrder, commentsPageSize, commentsPageNumber).then(function(comments) {
      $scope.comments = comments;
    }).catch(console.log);

    $scope.newComment = {};
    $scope.newComment.fn = function(newCommentBody){

      return $q(function (resolve, reject) {
        CommentInteractionService.sendCommentReply($scope.mainComment, newCommentBody).then(function(newComment) {
          $scope.comments.unshift(newComment);
          resolve(newComment);
        }).catch(reject);
      });

    }
    $scope.callbackFunctions = {
      startEdit: {}
    };

    $scope.callback = {
      startReply: {}
    };

    $scope.callback.vote = function (value) {

      if($scope.mainComment.userVote === value) {
        value = 0;
      }

      return $q(function(resolve, reject) {
        CommentInteractionService.sendVote($scope.mainComment, value).then(function(comment) {
          Object.assign($scope.mainComment, comment);
          resolve($scope.mainComment.userVote);
        }).catch(console.log);
      });
    }

    $scope.callback.edit = function(newBody) {

      $scope.mainComment.body = newBody;
      return $scope.mainComment.put();
    }

    $scope.openDeleteModal = function () {
      $scope.deletingComment = true;
      UIkit.modal(document.getElementById('delete-comment-modal-' + $scope.mainComment.id)).show();
    }

    $scope.confirmDelete = function (){
      $scope.sendingDelete.value = true;
      $q.resolve($scope.mainComment.all('enabled').remove()).then(function() {
        $scope.sendingDelete.value = false;
        $scope.mainComment.enabled = false;
        UIkit.modal(document.getElementById('delete-comment-modal-' + $scope.mainComment.id)).hide();
      }).catch(console.log);
    }

  });
});
