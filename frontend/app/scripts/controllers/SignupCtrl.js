define(['frontend', 'services/LoginService', 'services/PageTitleService'], function(frontend) {

    'use strict';
    frontend.controller('SignupCtrl', function($scope, LoggedUserFactory, $window, PageTitle, Restangular, $location) {
      PageTitle.setTitle('asd') //TODO: cambiar la key

      LoggedUserFactory.isLogged().then(resp => {
        if(resp) {
          $window.location.href = '/';
        }
      });

      $scope.signup = function (user) {
      //  TODO: lógica de la validación del formulario
          Restangular.setFullResponse(true).all('users').post(user).then(resp => {
            if(resp.status === 201) {
              const aux = resp.headers('location').split('/');
              $location.path(`/users/${aux[aux.length - 1]}`);
            }
            else {
              console.log(resp);
            }
          })
      }

    });

});
