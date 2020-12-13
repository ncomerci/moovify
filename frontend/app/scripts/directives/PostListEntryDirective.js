'use strict';
define(['frontend'], function(frontend) {

  frontend.directive('postListEntryDirective', function() {
    return {
      restrict: 'E',
      scope: {
        post: '='
      },
      templateUrl: 'resources/views/directives/postListEntryDirective.html',
      link: function (scope) {
        // console.log(scope.post);
      },
      controller: function ($scope) {
        $scope.getAgeMessageCode = function () {

          var creationDateTime = new Date($scope.post.creationDate).getTime();
          var currentDateTime = new Date().getTime();

          if(currentDateTime - creationDateTime < 1000 * 60 * 60){
            return 'LAST_HOUR';
          }
          else if (currentDateTime - creationDateTime < 1000 * 60 * 60 * 24) {
            return 'LAST_DAY'
          }
          else if (currentDateTime - creationDateTime < 1000 * 60 * 60 * 24 * 7) {
            return 'LAST_WEEK'
          }
          else if (currentDateTime - creationDateTime < 1000 * 60 * 60 * 24 * 31) {
            return 'LAST_MONTH'
          }
          else if (currentDateTime - creationDateTime < 1000 * 60 * 60 * 24 * 365) {
            return 'LAST_YEAR'
          }
        }
      }
    };
  });

});
