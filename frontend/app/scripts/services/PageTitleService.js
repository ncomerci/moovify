'use strict';
define(['frontend'], function(frontend) {

	frontend.service('PageTitle', function($translate) {
	  let title = {
	    value: 'Moovify'
    };
	  return {
	    getTitle: function () {
	      return title;
      },
      setTitle: function (i18nKey) {
	      $translate(i18nKey).then(t => title.value = t).catch(() => title.value = 'Moovify');
      }
    }
	});
});
