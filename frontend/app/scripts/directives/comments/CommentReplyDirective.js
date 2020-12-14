'use strict';
define(['frontend', 'services/utilities/LocalStorageService'], function(frontend) {

  frontend.directive('commentReplyDirective', function () {

    function getReplyStorageKey(id){
      return "comment-reply-parent-id-" + id;
    }

    return {
      restrict: 'E',
      scope: {
        comment: '=?',
        sendCommentFn: '&',
        parentId: '@'
      },
      templateUrl:'resources/views/directives/comments/commentReplyDirective.html',
      link: function (scope){

        scope.sendCommentFn = scope.sendCommentFn();
        scope.maxLen = 400;
      },
      controller: function ($scope, LocalStorageService){

        $scope.newComment = !$scope.comment;
        $scope.sendingComment = false;

        $scope.body = {content: LocalStorageService.get(getReplyStorageKey($scope.parentId))};

        $scope.sendComment = function () {

          $scope.sendingComment = true;
          $scope.sendCommentFn($scope.body.content).then(function () {
            $scope.sendingComment = false;
            LocalStorageService.delete(getReplyStorageKey($scope.parentId));
            $scope.body.content = '';
          }).catch(console.log);
        }

        $scope.$on('$destroy', function() {
          if($scope.body.content.length > 0){
            LocalStorageService.save(getReplyStorageKey($scope.parentId), $scope.body.content);
          }
        });
      }
    }
  });
});
