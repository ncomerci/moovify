'use strict';
define(['frontend'], function(frontend) {

  frontend.directive('commentLikeHandlerDirective', function() {

    return {
      restrict: 'E',
      scope: {
        userVote: '=',
        sendVoteFn: '&'
      },
      templateUrl:'resources/views/directives/commentLikeHandlerDirective.html',
      link: function (scope){
        scope.sendVoteFn = scope.sendVoteFn();
        scope.sendingVote = false;
      },
      controller: function ($scope){

        $scope.sendVote = function (value){

          $scope.sendingVote = true;
          $scope.sendVoteFn(value).then(function () {
            $scope.sendingVote = false;
          }).catch(console.log);

        }

      }
    }
  });
});
