'use strict';
define(['frontend', 'directives/comments/CommentDisplayDirective'], function(frontend) {

  frontend.directive('commentTreeDirective', function (){

    return {
      restrict: 'E',
      scope: {
        comments: '='
      },
      templateUrl:'resources/views/directives/comments/commentTreeDirective.html',
      link: function (scope){
      }
    }
  });
});
