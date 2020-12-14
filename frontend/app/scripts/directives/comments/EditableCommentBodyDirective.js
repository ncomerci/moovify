'use strict';
define(['frontend'], function(frontend) {

  frontend.directive('editableCommentBodyDirective', function () {

    return {
      restrict: 'E',
      scope: {
        body: '=',
        sendUpdateFn: '&',
        isOwner: '='
      },
      templateUrl:'resources/views/directives/comments/editableCommentBodyDirective.html',
      link: function (scope){
        scope.sendUpdateFn = scope.sendUpdateFn();
        scope.maxLen = 400;
      },
      controller: function ($scope){

        $scope.sendingEdit = false;
        $scope.editing = {value: false};

        $scope.editableBody = {value: ''};

        $scope.sendEdit = function () {

          $scope.sendingEdit = true;

          $scope.body = $scope.editableBody.value;

          $scope.sendUpdateFn($scope.body).then(function () {
            $scope.sendingEdit = false;
            $scope.editing = false;
          }).catch(console.log);
        }

      }
    }
  });
});
