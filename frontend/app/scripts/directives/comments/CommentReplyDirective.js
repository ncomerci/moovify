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
        parentId: '@?',
        startReply: '=?'
      },
      templateUrl:'resources/views/directives/comments/commentReplyDirective.html',
      link: function (scope){

        scope.sendCommentFn = scope.sendCommentFn();
        scope.maxLen = 400;
      },
      controller: function ($scope, LocalStorageService){

        $scope.sendingComment = false;
        $scope.writtingReply= {value: false};

        if($scope.postId) {
          $scope.storageKey = getPostReplyStorageKey($scope.postId);
        }
        else {
          $scope.storageKey = getCommentReplyStorageKey($scope.parentId);
        }

        if($scope.startReply){
          $scope.startReply.fn = function () {
            $scope.writtingReply.value = !$scope.writtingReply.value;
          }
        }
        else {
          $scope.writtingReply.value = true;
        }

        $scope.body = {content: LocalStorageService.get($scope.storageKey)};

        $scope.sendComment = function () {

          $scope.sendingComment = true;
          $scope.sendCommentFn($scope.body.content).then(function () {

            if($scope.startReply){
              $scope.writtingReply.value = false;
            }

            $scope.sendingComment = false;
            LocalStorageService.delete($scope.storageKey);
            $scope.body.content = '';

          }).catch(function () { $scope.sendingComment = false; });
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
