'use strict';
define(['frontend'], function(frontend) {

	frontend.service('History', function() {
	  // se podria usar un array para guardar el historial de navegación en la pag
    return{
      lastRoute: ''
    }
	});
});
