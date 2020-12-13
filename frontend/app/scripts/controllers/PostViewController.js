define(['frontend', 'services/PostFetchService', 'services/CommentFetchService', 'directives/CommentTreeDirective'], function(frontend) {

  'use strict';
  frontend.controller('PostViewController', function ($scope, PostFetchService, $location, CommentFetchService) {

    $scope.post = null;
    $scope.comments = null;

    var postId = 6;
    var userId = 10;

    PostFetchService.fetchFullPost(postId, userId, 3).then(function(post) {
      $scope.post = post;
    }).catch(console.log);

    CommentFetchService.getPostCommentsWithUserVote(postId, userId, 3, 'oldest', 5, 0).then(function(comments) {
      $scope.comments = comments;
      console.log($scope.comments);
    }).catch(console.log);
  });
});
