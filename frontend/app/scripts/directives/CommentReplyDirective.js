'use strict';
define(['frontend', 'services/LocalStorageService'], function(frontend) {

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
      templateUrl:'resources/views/directives/commentReplyDirective.html',
      link: function (scope){

        scope.sendCommentFn = scope.sendCommentFn();
        scope.maxLen = 400;
      },
      controller: function ($scope, LocalStorageService){

        $scope.newComment = !$scope.comment;
        $scope.sendingComment = false;

        $scope.body = {content: ''};

        if($scope.newComment) {
          $scope.body.content = LocalStorageService.get(getReplyStorageKey($scope.parentId));
        }
        else {
          $scope.body.content = comment.body;
        }

        $scope.sendComment = function () {

          console.log('Sending');

          $scope.sendingComment = true;
          $scope.sendCommentFn($scope.body.content).then(function () {
            $scope.sendingComment = false;
            LocalStorageService.delete(getReplyStorageKey($scope.parentId));
            $scope.body.content = '';
          }).catch(function () {
            $scope.sendingComment = false;
          });
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
