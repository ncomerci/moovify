'use strict';
define(['frontend'], function(frontend) {

  frontend.factory('RestFulResponse', function (Restangular) {
    return Restangular.withConfig(function (RestangularConfigurer) {
      RestangularConfigurer.setFullResponse(true);
    });
  });
});
