'use strict';
define(['frontend', 'services/DisplayService', 'services/UserService', 'directives/PrettyDateDirective',
  'services/utilities/RestFulResponseFactory','services/LoginService'], function(frontend) {

  frontend.directive('postListEntryDirective', function(DisplayService, LoggedUserFactory, RestFulResponse, UserService, $q) {
    return {
      restrict: 'E',
      scope: {
        post: '=',
        adminControls:'<',
        removePostFn:'&'
      },
      templateUrl: 'resources/views/directives/listEntries/postListEntryDirective.html',
      link: function (scope) {
        if(scope.removePostFn)
          scope.removePostFn = scope.removePostFn();
      },
      controller: function ($scope, $q) {
        $scope.getAgeMessage = function (creationDate) {
          return DisplayService.getAgeMessageCode(creationDate);
        }

        $scope.loggedUser = LoggedUserFactory.getLoggedUser();

        $scope.isAdmin = function (user){
          return UserService.userHasRole(user, 'ADMIN');
        }

        $scope.recoverPost = function () {
          return $q(function (resolve, reject) {
            RestFulResponse.withAuthIfPossible($scope.loggedUser).then(function (Restangular) {
              Restangular.one('posts', $scope.post.id).all('enabled').doPUT().then(function () {
                $scope.removePostFn($scope.post);
                resolve($scope.user);
              }).catch(reject);
            }).catch(reject);
          });
        }
      }
    }
  });

});
