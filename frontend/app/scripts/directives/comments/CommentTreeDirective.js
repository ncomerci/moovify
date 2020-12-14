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

        $scope.loadingMore = false;
        $scope.loadMore = function() {

          $scope.loadingMore = true;
          $scope.comments.getNext(0).then(function(comments) {

            $scope.comments.hasNext = comments.hasNext;
            if(comments.hasNext) {
              $scope.comments.getNext = comments.getNext;
            }

            Array.prototype.push.apply($scope.comments, comments);
            $scope.loadingMore = false;
          })
        }
      }
    }
  });
});
