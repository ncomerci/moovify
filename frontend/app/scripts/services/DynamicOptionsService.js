'use strict';
define(['frontend', 'services/RestFulResponseFactory'], function (frontend) {

  frontend.service('DynamicOptionsService', function ($q, RestFulResponse) {

    let optionsMap = {};

    return {
      getOptions: (endPoint) => {

        return $q((resolve, reject) => {

          if(!optionsMap[endPoint]) {

            RestFulResponse.all(endPoint).all('options').getList().then((response) => {
              optionsMap[endPoint] = response.data.map(entry => entry.originalElement);
              resolve(optionsMap[endPoint]);
            }).catch(reject);
          }
          else {
            resolve(optionsMap[endPoint]);
          }
        });
      }
    }

  });

});
