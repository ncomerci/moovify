'use strict';
define(['frontend', 'services/utilities/LocalStorageService'], function(frontend) {

  frontend.directive('commentReplyDirective', function () {

    function getCommentReplyStorageKey(id){
      return "comment-reply-parent-id-" + id;
    }
    function getPostReplyStorageKey(id){
      return "post-reply-parent-id-" + id;
    }

    return {
      restrict: 'E',
      scope: {
        postId: '@?',
        sendCommentFn: '&',
        parentId: '@?'
      },
      templateUrl:'resources/views/directives/comments/commentReplyDirective.html',
      link: function (scope){

        scope.sendCommentFn = scope.sendCommentFn();
        scope.maxLen = 400;
      },
      controller: function ($scope, LocalStorageService){

        $scope.sendingComment = false;

        if($scope.postId) {
          $scope.storageKey = getPostReplyStorageKey($scope.postId);
        }
        else {
          $scope.storageKey = getCommentReplyStorageKey($scope.parentId);
        }

        $scope.body = {content: LocalStorageService.get($scope.storageKey)};

        $scope.sendComment = function () {

          $scope.sendingComment = true;
          $scope.sendCommentFn($scope.body.content).then(function () {
            $scope.sendingComment = false;
            LocalStorageService.delete($scope.storageKey);
            $scope.body.content = '';
          }).catch(console.log);
        }

        $scope.$on('$destroy', function() {
          if($scope.body.content && $scope.body.content.length > 0){
            LocalStorageService.save($scope.storageKey, $scope.body.content);
          }
        });
      }
    }
  });
});
