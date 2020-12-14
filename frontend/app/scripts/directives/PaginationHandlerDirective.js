'use strict';
define(['frontend'], function(frontend) {

  frontend.directive('paginationHandlerDirective', function() {

    return {
      restrict: 'E',
      scope: {
        paginationParams: '=',
        searchFn: '&',
        resetPaginationFn: '=',
        mutex: '='
      },

      templateUrl: 'resources/views/directives/paginationHandlerDirective.html',

      link: function(scope) {

        scope.pageSizeOptions = [2, 5, 10, 25, 35];

        scope.mutex = false;

        var width = 5;

        scope.firstShownPage = scope.paginationParams.currentPage - width;
        if(scope.firstShownPage < 0)
          scope.firstShownPage = 0;

        scope.lastShownPage = scope.paginationParams.currentPage + width;
        if(scope.lastShownPage > scope.paginationParams.lastPage)
          scope.lastShownPage = scope.paginationParams.lastPage;

        scope.searchFn = scope.searchFn();

        scope.resetPaginationFn = function() {
          console.log("resetPagination");
          scope.paginationParams.currentPage = 0;
        }

        scope.$watchCollection('paginationParams', function(newParams, oldParams, scope) {

          console.log(newParams, oldParams);
          if(!newParams || !oldParams){
            return;
          }

          var newPageSize = newParams.pageSize !== oldParams.pageSize;
          var newPageNumber = newParams.currentPage !== oldParams.currentPage;

          if(newPageSize || newPageNumber){

            scope.mutex = true;

            if(newPageSize){
              scope.resetPaginationFn();
            }

            console.log("paginationSearch");
            scope.searchFn();
          }
        });

      }
    };
  });

});
