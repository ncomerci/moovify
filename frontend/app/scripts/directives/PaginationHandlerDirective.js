'use strict';
define(['frontend'], function(frontend) {

  frontend.directive('paginationHandlerDirective', function() {

    return {
      restrict: 'E',
      scope: {
        paginationParams: '=',
        searchFn: '&',
        resetPaginationFn: '=',
        width: '<?',
        mutex: '='
      },

      templateUrl: 'resources/views/directives/paginationHandlerDirective.html',

      link: function(scope) {

        scope.pageSizeOptions = [2, 5, 10, 25];

        if(!scope.width){
          scope.width = 5;
        }

        scope.mutex = false;

        scope.updatePageOptions();

        scope.searchFn = scope.searchFn();

        scope.resetPaginationFn = function() {
          scope.paginationParams.currentPage = 0;
        }

        scope.$watchCollection('paginationParams', function(newParams, oldParams, scope) {

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

            if(newPageNumber){
              scope.updatePageOptions();
            }

            scope.searchFn();
          }
        });

      },
      controller: function($scope) {

        $scope.updatePageOptions = function () {

          $scope.pageNumberOptions = [];

          $scope.firstShownPage = $scope.paginationParams.currentPage - Math.floor($scope.width/2);
          if($scope.firstShownPage < 0)
            $scope.firstShownPage = 0;

          $scope.lastShownPage = $scope.paginationParams.currentPage + Math.floor($scope.width/2);
          if($scope.lastShownPage > $scope.paginationParams.lastPage)
            $scope.lastShownPage = $scope.paginationParams.lastPage;

          for (var i = $scope.firstShownPage; i <= $scope.lastShownPage; i++) {
            $scope.pageNumberOptions.push(i);
          }
        }
      }
    };
  });

});
