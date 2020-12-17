'use strict';
define(['frontend', 'services/utilities/RestFulResponseFactory', 'services/LoginService'], function (frontend) {

  frontend.service('UserService', function ($q, RestFulResponse, LoggedUserFactory, $location) {

    var avatarData = {
      file: undefined,
      error: false
    }

    this.userHasRole = function (user, role) {
      if(!user){
        return false;
      }

       return user.roles.includes(role);
    }

    this.getUser = function (id) {
      return $q(function (resolve, reject) {
        RestFulResponse.noAuth().one('/users/' + id).get().then(function (r) {
          resolve(r.data);
        }).catch(reject);
      });
    }

    this.doLoggedUserFollow = function (loggedUser, userId) {
      return $q(function (resolve, reject) {
        RestFulResponse.withAuth(loggedUser).then(function (r) {
          r.one('user/following/'+userId).get().then(function (resp) {
            resolve(resp.data.response);
          }).catch(reject);
        }).catch(reject);
      });
    }

    this.avatar = {
      setFile: function (file) {
        if (file !== undefined && file.size > 1000000) {
          avatarData.file = undefined;
          avatarData.error = true;
        } else {
          avatarData.file = file;
          avatarData.error = false;
        }
      },
      get: function () {
        return avatarData;
      },
      upload: function () {
        return $q(function (resolve, reject) {
          RestFulResponse.withAuth(LoggedUserFactory.getLoggedUser()).then(function (r) {
            var fd = new FormData();
            fd.append('avatar', avatarData.file);
            r.one('/user/avatar').customPUT(fd, undefined, undefined, {'Content-Type': undefined}).then(function () {
              avatar.file = undefined;
              LoggedUserFactory.getLoggedUser().avatar += '?' + new Date().getTime();
              resolve();
            }).catch(reject);
          }).catch(reject);
        });
      }
    };

    this.resendConfirmEmail = function (loggedUser) {
      return $q(function (resolve, reject) {
        RestFulResponse.withAuth(loggedUser).then(function (r) {
          r.all('/user/email_confirmation').post().then(function () {
            resolve();
          }).catch(reject);
        }).catch(reject);
      });
    }

    this.followUser = function (loggedUser, user) {
      return $q(function (resolve, reject) {
        RestFulResponse.withAuth(loggedUser).then(function (r) {
          r.one('/user/following/'+user.id).put().then(function (){
            user.followerCount += 1;
            resolve();
          }).catch(reject);
        }).catch(reject);
      });
    }

    this.unfollowUser = function (loggedUser, user) {
      return $q(function (resolve, reject) {
        RestFulResponse.withAuth(loggedUser).then(function (r) {
          r.one('/user/following/'+user.id).remove().then(function (){
            user.followerCount -= 1;
            resolve();
          }).catch(reject);
        }).catch(reject);
      });
    }

    this.promoteUser = function (loggedUser, user) {
      return $q(function (resolve, reject) {
        RestFulResponse.withAuth(loggedUser).then(function (r) {
          r.one('/users/'+user.id+'/privilege').put().then(function () {
            user.roles.push('ADMIN');
            resolve();
          }).catch(reject);
        }).catch(reject);
      });
    }

    this.deleteUser = function (loggedUser, user) {
      return $q(function (resolve, reject) {
        RestFulResponse.withAuth(loggedUser).then(function (r) {
          r.one('/users/'+user.id+'/enabled').remove().then(function () {
            resolve();
          }).catch(reject);
        }).catch(reject);
      });
    }

    this.updateInfo = function (loggedUser, info) {
      return $q(function (resolve, reject) {
        RestFulResponse.withAuth(loggedUser).then(function (r) {
          r.one('/user').customPUT(info, undefined, undefined, {'Content-Type': 'application/json'})
           .then(resolve).catch(reject);
        })
      });
    }

    this.updatePassword = function (loggedUser, password) {
      return $q(function (resolve, reject) {
        RestFulResponse.withAuth(loggedUser).then(function (r) {
          r.one('/user').customPUT(password, undefined, undefined, {'Content-Type': 'application/json'})
           .then(function () {
             LoggedUserFactory.logout().then(function () {
               $location.path('/login');
               resolve();
             });
           }).catch(reject);
        }).catch(reject);
      });
    }

    this.sendConfirmToken = function (loggedUser, token) {
      return $q(function (resolve, reject) {
        RestFulResponse.withAuth(loggedUser).then(function (r) {
          r.all('/user/email_confirmation').customPUT(token, undefined, undefined, {'Content-Type': 'application/json'})
           .then(function () {
             var idx = loggedUser.roles.indexOf('NOT_VALIDATED');
             loggedUser.roles[idx] = 'USER';
             resolve();
           }).catch(reject);
        }).catch(reject);
      });
    }

  });
});
