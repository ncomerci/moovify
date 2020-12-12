'use strict';
define(['frontend', 'services/RestFulResponseFactory'], function (frontend) {

  frontend.service('PostCategoriesService', function($q, RestFulResponse) {

    var categories = null;

    return {
      getPostCategories: function() {

        return $q(function(resolve, reject) {
          if (!categories) {
            RestFulResponse.all('/posts').all('categories').getList().then(function(response) {
              categories = response.data.map(function(entry) { return entry.originalElement; });
              resolve(categories);
            }).catch(reject);
          } else {
            resolve(categories)
          }
        });
      }
    }
  });
});
