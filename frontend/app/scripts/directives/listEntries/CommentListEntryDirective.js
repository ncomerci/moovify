'use strict';
define(['frontend', 'directives/PrettyDateDirective', 'services/DisplayService', 'services/utilities/RestFulResponseFactory','services/LoginService'], function (frontend) {

  frontend.directive('commentListEntryDirective', function (DisplayService, LoggedUserFactory, RestFulResponse, $q){
    return {
      restrict: 'E',
      scope: {
        comment: '=',
        adminControls:'<',
        removeCommentFn:'&'
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
          return $q(function (resolve, reject) {
            RestFulResponse.withAuthIfPossible($scope.loggedUser).then(function (Restangular) {
              Restangular.one('comments', $scope.comment.id).all('enabled').doPUT().then(function () {
                $scope.removeCommentFn($scope.comment);
                resolve($scope.user);
              }).catch(reject);
            }).catch(reject);
          });
        }
      }
    }
  });
});
