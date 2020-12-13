'use strict';
define(['frontend', 'directives/CommentTreeDirective', 'services/CommentInteractionsService'], function(frontend) {

  frontend.directive('commentDisplayDirective', function (CommentInteractionService){

    return {
      restrict: 'E',
      scope: {
        comment: '='
      },
      templateUrl:'resources/views/directives/commentDisplayDirective.html',
      link: function(scope) {
        console.log(scope.comment);
      },
      controller: function($scope) {

        console.log('constructor');


        $scope.upVote = function() {
          console.log('UpVote');
          CommentInteractionService.sendVote($scope.comment, 1).then(function (comment) {
            $scope.comment = comment
          }).catch(console.log);
        }

        $scope.downVote = function() {
          console.log('DownVote');
          CommentInteractionService.sendVote($scope.comment, -1).then(function (comment) {
            $scope.comment = comment
          }).catch(console.log);
        }
      }
    }
  });
});
