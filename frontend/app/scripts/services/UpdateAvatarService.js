'use strict';
define(['frontend', 'services/utilities/RestFulResponseFactory', 'services/LoginService'], function(frontend) {

	frontend.service('UpdateAvatar', function(RestFulResponse, LoggedUserFactory, $q) {
    var avatar = {
      file: undefined,
      error: false
    }

    return {
      setFile: function (file) {
        if (file !== undefined && file.size > 1000000) {
          avatar.file = undefined;
          avatar.error = true;
        } else {
          avatar.file = file;
          avatar.error = false;
        }
      },
      getAvatar: function () {
        return avatar;
      },
      uploadAvatar: function () {
        return $q(function (resolve, reject) {
          RestFulResponse.withAuth(LoggedUserFactory.getLoggedUser()).then(function (r) {
            var fd = new FormData();
            fd.append('avatar', avatar.file);
            r.one('/user/avatar').customPUT(fd, undefined, undefined, {'Content-Type': undefined}).then(function () {
              avatar.file = undefined;
              LoggedUserFactory.getLoggedUser().avatar += '?' + new Date().getTime();
              resolve();
            }).catch(reject);
          }).catch(reject);
        });
      }
    };

	});
});
