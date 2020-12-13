'use strict';
define(['frontend', 'directives/CommentTreeDirective'], function(frontend) {

  frontend.directive('commentDisplayDirective', function (){

    return {
      restrict: 'E',
      scope: {
        comment: '='
      },
      templateUrl:'resources/views/directives/commentDisplayDirective.html',
      link: function (scope){
        console.log(scope.comment);
      }
    }
  });
});
