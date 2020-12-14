define(['frontend', 'marked', 'purify','services/fetch/PostFetchService', 'services/fetch/CommentFetchService',
  'directives/comments/CommentTreeDirective', 'services/CommentInteractionService'], function(frontend, marked, DOMPurify) {

  'use strict';
  frontend.controller('PostViewController', function ($scope, PostFetchService, $location, CommentFetchService, $q, CommentInteractionService, $routeParams) {

    $scope.post = null;
    $scope.comments = null;

    var postId = $routeParams.id;
    var commentDepth = 0;
    var commentsOrder = 'newest';
    var commentsPageSize = 5;
    var commentsPageNumber = 0;

    marked.setOptions({
      gfm: true,
      breaks: true,
      sanitizer: DOMPurify.sanitize,
      //  silent: true,
    });

    PostFetchService.fetchPost(postId).then(function(post) {
      $scope.post = post;
      $scope.post.body = marked($scope.post.body);
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
