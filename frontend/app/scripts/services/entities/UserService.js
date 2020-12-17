'use strict';
define(['frontend', 'services/utilities/RestFulResponseFactory', 'services/LoginService', 'services/utilities/LinkParserService'], function(frontend) {

  frontend.service('UserService', function(RestFulResponse, LinkParserService, $q, LoggedUserFactory) {

    this.signUp = function (user) {
      return RestFulResponse.noAuth().all('users').post(user);
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

    var avatarTmp = {
      file: undefined,
      error: false
    }
    this.avatar = {
      setFile: function (file) {
        if (file !== undefined && file.size > 1000000) {
          avatarTmp.file = undefined;
          avatarTmp.error = true;
        } else {
          avatarTmp.file = file;
          avatarTmp.error = false;
        }
      },
      get: function () {
        return avatarTmp;
      },
      upload: function () {
        return $q(function (resolve, reject) {
          RestFulResponse.withAuth(LoggedUserFactory.getLoggedUser()).then(function (r) {
            var fd = new FormData();
            fd.append('avatar', avatarTmp.file);
            r.one('/user/avatar').customPUT(fd, undefined, undefined, {'Content-Type': undefined}).then(function () {
              avatarTmp.file = undefined;
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

    this.resetPassword = function (passWithToken) {
      return RestFulResponse.noAuth().one('/user/password_reset').customPUT(passWithToken, undefined, undefined, {'Content-Type': 'application/json'});
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

    this.sendToken = function(email) {
       return RestFulResponse.noAuth().all('/user/password_reset').post(email);
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

    this.searchUsers = function (query, role, enabled, orderBy, pageSize, pageNumber) {
      return fetchUsersInternal('/users', query, role, enabled, orderBy, pageSize, pageNumber);
    }

    this.fetchUsers = function (path, enabled, orderBy, pageSize, pageNumber) {
      return fetchUsersInternal(path, null, null, enabled, orderBy, pageSize, pageNumber);
    }

    this.recoverUser = function (user) {
      return $q(function (resolve, reject) {
        RestFulResponse.withAuthIfPossible(LoggedUserFactory.getLoggedUser()).then(function (Restangular) {
          Restangular.one('users', user.id).all('enabled').doPUT().then(function () {
            resolve(user);
          }).catch(reject);
        }).catch(reject);
      });
    }

    function fetchUsersInternal(path, query, role, enabled, orderBy, pageSize, pageNumber) {

      // Obligatory params
      var queryParams = {
        orderBy: orderBy,
        pageSize: pageSize ? pageSize : 5,
        pageNumber: pageNumber ? pageNumber : 0
      };

      if(query)
        queryParams.query = query;

      // Optional Params
      if (role)
        queryParams.role = role;

      if (enabled !== null)
        queryParams.enabled = enabled;

      return $q(function (resolve, reject) {

        RestFulResponse.withAuthIfPossible(LoggedUserFactory.getLoggedUser()).then(function (r) {

          r.all(path).getList(queryParams).then(function (userResponse) {

            var paginationParams = {lastPage: 0, pageSize: queryParams.pageSize, currentPage: queryParams.pageNumber};
            var linkHeader = userResponse.headers('Link');
            var users = userResponse.data;
            // Si no hay Link -> no habia contenido -> no me interesa paginar nada
            if (linkHeader) {
              paginationParams.lastPage = LinkParserService.parse(linkHeader);
            }
            resolve({collection: users, paginationParams: paginationParams, queryParams: queryParams});

          }).catch(function (response) {
            reject({status: response.status, message: 'UserService: FetchUsers'})
          });
        }).catch(reject);
      });
    }
  });
});
