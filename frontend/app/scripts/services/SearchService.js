'use strict';
define(['frontend'], function(frontend) {
  
    frontend.factory('SearchService', function(Restangular) {
        return function(contentType, searchOptions) {
            return Restangular.all('search').all(contentType).getList(searchOptions);
        };
    });
});