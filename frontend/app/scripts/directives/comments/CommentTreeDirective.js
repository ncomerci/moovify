'use strict';
define(['frontend', 'directives/comments/CommentDisplayDirective'], function(frontend) {

  frontend.directive('commentTreeDirective', function (){

    return {
      restrict: 'E',
      scope: {
        comments: '='
      },
      templateUrl:'resources/views/directives/comments/commentTreeDirective.html',
      link: function(scope) {
      },
      controller: function($scope) {

        $scope.newReplyCount = 0;
        $scope.newComments = null;

        function loadMore() {

          $scope.comments.getNext(0).then(function(comments) {
            $scope.newReplyCount = comments.length;
            $scope.newComments = comments;
          }).catch(console.log);
        }

        if($scope.comments.hasNext){
          loadMore();
        }

        $scope.showMore = function () {

          $scope.comments.hasNext = $scope.newComments.hasNext;
          if($scope.newComments.hasNext) {
            $scope.comments.getNext = $scope.newComments.getNext;
            loadMore()
          }
          $scope.newReplyCount = 0;
          Array.prototype.push.apply($scope.comments, $scope.newComments);
        }
      }
    }
  });
});
