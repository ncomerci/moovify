'use strict';
define(['frontend', 'directives/CommentDisplayDirective'], function(frontend) {

  frontend.directive('commentTreeDirective', function (){

    return {
      restrict: 'E',
      scope: {
        comments: '='
      },
      templateUrl:'resources/views/directives/commentTreeDirective.html',
      link: function (scope){
        console.log(scope.comments);
      }
    }
  });
});
