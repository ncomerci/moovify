'use strict';
define(['frontend'], function(frontend) {

  frontend.directive('paginationHandlerDirective', function() {

    return {
      restrict: 'E',
      scope: {
        paginationParams: '=',
        searchFn: '&',
        resetPaginationFn: '='
      },

      templateUrl: 'views/directives/paginationHandlerDirective.html',

      link: function(scope) {

        scope.searchFn = scope.searchFn();

        scope.resetPaginationFn = () => {
          console.log(scope);
          scope.paginationParams.currentPage = 0;
        }

        scope.$watchCollection('paginationParams', (newParams, oldParams, scope) => {

          if(!newParams || !oldParams){
            return;
          }

          let newPageSize = newParams.pageSize !== oldParams.pageSize;
          let newPageNumber = newParams.currentPage !== oldParams.currentPage;

          if(newPageSize || newPageNumber){

            if(newPageSize){
              scope.resetPaginationFn();
            }

            console.log("search");

            scope.searchFn();
          }
        });

      }
    };
  });

});
