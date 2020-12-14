define(['frontend', 'services/PostFetchService', 'services/CommentFetchService', 'directives/CommentTreeDirective'], function(frontend) {

  'use strict';
  frontend.controller('PostViewController', function ($scope, PostFetchService, $location, CommentFetchService) {

    $scope.post = null;
    $scope.comments = null;

    var postId = 6;
    var depth = 1;

    PostFetchService.fetchFullPost(postId).then(function(post) {
      $scope.post = post;
    }).catch(console.log);

    CommentFetchService.getPostCommentsWithUserVote(postId, depth, 'newest', 5, 0).then(function(comments) {
      $scope.comments = comments;
    }).catch(console.log);
  });
});
