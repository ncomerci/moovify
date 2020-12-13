'use strict';
define(['frontend', 'services/RestFulResponseFactory', 'services/LinkParserService'], function(frontend) {

  frontend.service('UserFetchService', function(RestFulResponse, LinkParserService, $q) {

    this.searchUsers = function(query, role, enabled, orderBy, pageSize, pageNumber) {
      return fetchUsersInternal('/users', query, role, enabled, orderBy, pageSize, pageNumber);
    }

    this.fetchUsers = function (path, enabled, orderBy, pageSize, pageNumber) {
      return fetchUsersInternal(path, null, null, enabled, orderBy, pageSize, pageNumber);
    }

    function fetchUsersInternal(path, query, role, enabled, orderBy, pageSize, pageNumber) {

      // Obligatory params
      var queryParams = {
        query: query,
        orderBy: orderBy,
        pageSize: pageSize ? pageSize : 5,
        pageNumber: pageNumber ? pageNumber : 0
      };

      // Optional Params
      if(role)
        queryParams.role = role;

      if(enabled !== null)
        queryParams.enabled = enabled;

      return $q(function(resolve, reject) {
        RestFulResponse.noAuth().all(path).getList(queryParams).then(function(userResponse) {

          var paginationParams = {pageSize: queryParams.pageSize, lastPage: 0};
          var linkHeader = userResponse.headers('Link');
          var users = userResponse.data;

          // Si no hay Link -> no habia contenido -> no me interesa paginar nada
          if(linkHeader){
            paginationParams = LinkParserService.parse(linkHeader);
          }

          resolve({collection: users, paginationParams: paginationParams, queryParams: queryParams});

        }).catch(function(response) { reject({status: response.status, message: 'UserFetchService: FetchUsers'}) });
      });
    }
  });
});
