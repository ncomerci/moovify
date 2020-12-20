define(['frontend','uikit', 'services/entities/CommentService',
  'directives/comments/CommentTreeDirective', 'services/LoginService', 'services/entities/UserService',
  'services/utilities/PageTitleService',  'directives/PrettyDateDirective', 'services/utilities/TimeService'], function(frontend, UIkit) {

  'use strict';
  frontend.controller('CommentViewCtrl', function ($scope, $location, LoggedUserFactory, UserService,
                                                         PageTitle, $q, CommentService, TimeService, $routeParams) {

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


    CommentService.fetchOneComment(commentId).then(function(comment) {
      $scope.mainComment = comment;
    }).catch(function() { $location.path('/500') });

    CommentService.getCommentCommentsWithUserVoteById(commentId, commentDepth, commentsOrder, commentsPageSize, commentsPageNumber).then(function(comments) {
      $scope.comments = comments;
    }).catch(); // Can continue without comments

    $scope.newComment = {};
    $scope.newComment.fn = function(newCommentBody){

      return $q(function (resolve, reject) {
        CommentService.sendCommentReply($scope.mainComment, newCommentBody).then(function(newComment) {
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
        CommentService.sendVote($scope.mainComment, value).then(function(comment) {
          Object.assign($scope.mainComment, comment);
          resolve($scope.mainComment.userVote);
        }).catch(reject);
      });
    }

    $scope.callback.edit = function(newBody) {
      $scope.lastEditTime = TimeService.localDateNow();
      $scope.edited = true;
      $scope.mainComment.body = newBody;
      return $scope.mainComment.put();
    }

    $scope.getDateFormatted = function (creationDate){
      return TimeService.getDateFormatted(creationDate);
    };

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
