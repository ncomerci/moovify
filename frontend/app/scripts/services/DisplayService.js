'use strict';
define(['frontend'], function (frontend) {

  frontend.service('DisplayService', function () {
    return {
      getYear: function (releaseDate){
        return releaseDate.substring(0, releaseDate.indexOf("-"));
      },
      getBodyFormatted: function (body){
        return body.length <= 40 ? '\"' + body + '\"': '\"' + body.substring(0, 40) + '\" ' + '[...]';
      }
    }
  });
});
