'use strict';
define(['frontend'], function (frontend) {

  frontend.service('DisplayService', function () {
    return {
      getAgeMessageCode: function (creationDate) {

        var creationDateTime = new Date(creationDate).getTime();
        var currentDateTime = new Date().getTime();

        if (currentDateTime - creationDateTime < 1000 * 60 * 60) {
          return "{{'LAST_HOUR' | translate }}";
        } else if (currentDateTime - creationDateTime < 1000 * 60 * 60 * 24) {
          return "{{'LAST_DAY' | translate }}";
        } else if (currentDateTime - creationDateTime < 1000 * 60 * 60 * 24 * 7) {
          return "{{'LAST_WEEK' | translate }}";
        } else if (currentDateTime - creationDateTime < 1000 * 60 * 60 * 24 * 31) {
          return "{{'LAST_MONTH' | translate }}";
        } else if (currentDateTime - creationDateTime < 1000 * 60 * 60 * 24 * 365) {
          return "{{'LAST_YEAR' | translate }}";
        }
      },
      getYear: function (releaseDate){
        return releaseDate.substring(0, releaseDate.indexOf("-"));
      },
      getBodyFormatted: function (body){
        return body.length <= 40 ? '\"' + body + '\"': '\"' + body.substring(0, 40) + '\" ' + '[...]';
      }
    }
  });
});
