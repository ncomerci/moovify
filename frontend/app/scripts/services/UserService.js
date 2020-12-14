'use strict';
define(['frontend', 'services/utilities/RestFulResponseFactory'], function (frontend) {

  frontend.service('UserService', function (RestFulResponse) {

    this.userHasRole = function (user, role) {
      if(!user){
        return false;
      }

       return user.roles.includes(role);
    }
  });
});
