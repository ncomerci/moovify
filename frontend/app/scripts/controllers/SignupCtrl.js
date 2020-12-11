define(['frontend', 'services/LoginService', 'services/PageTitleService', 'services/RestFulResponseFactory'], function(frontend) {

    'use strict';
    frontend.controller('SignupCtrl', function($scope, LoggedUserFactory, $window, PageTitle, RestFulResponse, $location, $q) {
      PageTitle.setTitle('asd') //TODO: cambiar la key

      LoggedUserFactory.isLogged().then(resp => {
        if(resp) {
          $window.location.href = '/';
        }
      });

      $scope.signUpBtnPressed = false;
      $scope.signUpError = false;

      $scope.user = {};

      $scope.nameConstrains = {
        pattern: /^[a-zA-Z ]*$/,
        minLen: 2,
        maxLen: 50
      }

      $scope.usernameConstrains = {
        pattern: /^[a-zA-Z0-9#_]+$/,
        minLen: 6,
        maxLen: 50
      }

      $scope.passwordConstrains = {
        pattern: /^[^\s]+$/,
        minLen: 12,
        maxLen: 30
      }

      // TODO: subir avatar
      $scope.signup = function (user) {

        $scope.signUpBtnPressed = true;

        if(
          !$scope.fieldIsNotValid('name') &&
          !$scope.emailIsNotValid() &&
          !$scope.fieldIsNotValid('username') &&
          !$scope.fieldIsNotValid('password') &&
          !$scope.fieldIsNotValid('repeatPassword') &&
          !$scope.passwordsNotEquals()
        )
        {
          RestFulResponse.all('users').post(user).then(resp => {
            if(resp.status === 201) {
              const aux_user = {
                username: user.username,
                password: user.password
              }
              LoggedUserFactory.login(aux_user, false).then(() => {
                $location.path(`/`) // TODO: poner la ruta del perfil
              })

            }
            else {
              console.log(resp);
            //  TODO: mostrar errores (por ej si el usuario o el mail existen)
            }
          })
        }
      }

      $scope.fieldRequired = function (field) {
        return $scope.signUpBtnPressed && $scope.signupForm[field].$error.required !== undefined;
      }

      // fieldRequired || cons1 || cons2 || ... || consN
      $scope.fieldIsNotValid = function (field) {
        return $scope.fieldRequired(field) || $scope.signupForm[field].$error.pattern || $scope.signupForm[field].$error.minlength || $scope.signupForm[field].$error.maxlength;
      }

      $scope.emailIsNotValid = function () {
        return $scope.fieldRequired("email") || $scope.signupForm.email.$error.email;
      }

      $scope.passwordsNotEquals = function () {
        return $scope.user.password !== $scope.user.repeatPassword;
      }

    });

});
