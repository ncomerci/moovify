'use strict';
define(['frontend', 'services/utilities/RestFulResponseFactory', 'services/LoginService', 'services/utilities/LinkParserService'], function(frontend) {

  frontend.service('UserFetchService', function(RestFulResponse, LinkParserService, $q, LoggedUserFactory) {

    this.searchUsers = function (query, role, enabled, orderBy, pageSize, pageNumber) {
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
      if (role)
        queryParams.role = role;

      if (enabled !== null)
        queryParams.enabled = enabled;

      return $q(function (resolve, reject) {

        RestFulResponse.withAuthIfPossible(LoggedUserFactory.getLoggedUser()).then(function (Restangular) {

          Restangular.all(path).getList(queryParams).then(function (userResponse) {

            var paginationParams = {lastPage: 0, pageSize: queryParams.pageSize, currentPage: queryParams.pageNumber};
            var linkHeader = userResponse.headers('Link');
            var users = userResponse.data;
            // Si no hay Link -> no habia contenido -> no me interesa paginar nada
            if (linkHeader) {
              paginationParams.lastPage = LinkParserService.parse(linkHeader);
            }
            resolve({collection: users, paginationParams: paginationParams, queryParams: queryParams});

          }).catch(function (response) {
            reject({status: response.status, message: 'UserFetchService: FetchUsers'})
          });
        }).catch(reject);
      });
    }
  });
});
