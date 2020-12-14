define(['frontend', 'services/fetch/PostFetchService', 'services/fetch/CommentFetchService', 'directives/comments/CommentTreeDirective', 'services/CommentInteractionService'], function(frontend) {

  'use strict';
  frontend.controller('PostViewController', function ($scope, PostFetchService, $location, CommentFetchService, $q, CommentInteractionService, $routeParams) {

    $scope.post = null;
    $scope.comments = null;

    var postId = $routeParams.id;
    var commentDepth = 0;
    var commentsOrder = 'newest';
    var commentsPageSize = 5;
    var commentsPageNumber = 0;


    PostFetchService.fetchPost(postId).then(function(post) {
      $scope.post = post;
    }).catch(console.log);

    CommentFetchService.getPostCommentsWithUserVote(postId, commentDepth, commentsOrder, commentsPageSize, commentsPageNumber).then(function(comments) {
      $scope.comments = comments;
    }).catch(console.log);

    $scope.newComment = {};
    $scope.newComment.fn = function(newCommentBody){

      return $q(function (resolve, reject) {
        CommentInteractionService.sendPostReply($scope.post, newCommentBody).then(function(newComment) {
          $scope.comments.unshift(newComment);
          resolve(newComment);
        }).catch(reject);
      });

    }
  });
});
