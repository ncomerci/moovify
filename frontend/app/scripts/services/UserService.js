'use strict';
define(['frontend'], function (frontend) {

  frontend.service('UserService', function () {

    this.userHasRole = function (user, role) {
      if(!user){
        return false;
      }

       return user.roles.includes(role);
    }
  });
});
