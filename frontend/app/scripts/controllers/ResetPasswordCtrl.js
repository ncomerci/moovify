define(['frontend', 'services/utilities/RestFulResponseFactory', 'services/utilities/PageTitleService'], function(frontend) {

    'use strict';
    frontend.controller('ResetPasswordCtrl', function($scope, RestFulResponse, PageTitle, $timeout, $location) {
      PageTitle.setTitle('RESET_PASSWORD_TITLE');

      $scope.btnPressed = false;
      $scope.mailSent = false;
      $scope.mailError = false;

      $scope.sendEmail = function () {
        $scope.btnPressed = true;

        if(!$scope.resetPassForm.email.$error.required && !$scope.resetPassForm.email.$error.email) {
          var aux = {email: $scope.email};
          RestFulResponse.noAuth().all('/user/password_reset').post(aux).then(function () {
            $scope.mailSent = true;
            $timeout(function () {
              $location.path('/updatePassword');
            }, 2000);
          }).catch(function (err) {
            $scope.mailError = true;
            console.log(err);
          });
        }
      }

      $scope.reset = function () {
        $scope.mailSent = false;
        $scope.mailError = false;
      }
    });

});
