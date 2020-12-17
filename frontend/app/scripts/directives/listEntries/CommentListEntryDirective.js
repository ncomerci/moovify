'use strict';
define(['frontend', 'directives/PrettyDateDirective', 'services/DisplayService','services/LoginService','services/entities/CommentService'], function (frontend) {

  frontend.directive('commentListEntryDirective', function (DisplayService, LoggedUserFactory, CommentService, $q){
    return {
      restrict: 'E',
      scope: {
        comment: '=',
        adminControls:'<?',
        removeCommentFn:'&?'
      },
      templateUrl: 'resources/views/directives/listEntries/commentListEntryDirective.html',
      link: function (scope) {
        if(scope.removeCommentFn)
          scope.removeCommentFn = scope.removeCommentFn();
      },
      controller: function ($scope, $q) {
        $scope.getBodyFormatted = function (body){
          return DisplayService.getBodyFormatted(body);
        }

        $scope.loggedUser = LoggedUserFactory.getLoggedUser();

        $scope.recoverComment = function () {
          CommentService.recoverComment($scope.comment).then(function (comment) {
            $scope.removeCommentFn(comment);
          }).catch();
        }
      }
    }
  });
});
