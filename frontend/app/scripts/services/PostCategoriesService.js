'use strict';
define(['frontend', 'services/RestFulResponseFactory'], function (frontend) {

  frontend.service('PostCategoriesService', function ($q, RestFulResponse) {

    let categories = null;

    return {
      getPostCategories: () => {

        return $q((resolve, reject) => {
          if (!categories) {
            RestFulResponse.all('/posts').all('categories').getList().then((response) => {
              categories = response.data.map(entry => entry.originalElement);
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
