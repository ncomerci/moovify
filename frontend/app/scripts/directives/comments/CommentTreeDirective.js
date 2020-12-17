'use strict';
define(['frontend', 'directives/comments/CommentDisplayDirective'], function(frontend) {

  frontend.directive('commentTreeDirective', function ($locale){

    return {
      restrict: 'E',
      scope: {
        comments: '=',
        depth: '='
      },
      templateUrl:'resources/views/directives/comments/commentTreeDirective.html',
      link: function(scope) {
      },
      controller: function($scope, $locale) {

        if($locale.id === 'es') {
          $scope.repliesForm = {
            1:'Mostrar respuesta',
            other:'Mostrar {} respuestas'
          }
        }
        else {
          $scope.repliesForm = {
            1:'Show reply',
            other:'Show {} replies'
          }
        }

        $scope.newReplyCount = 0;
        $scope.newComments = null;

        function loadMore() {

          $scope.comments.getNext(0).then(function(comments) {
            $scope.newReplyCount = comments.length;
            $scope.newComments = comments;
          }).catch(); // No need for error
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
