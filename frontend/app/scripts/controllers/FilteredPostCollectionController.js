'use strict';
define(['frontend', 'services/LoginService', 'services/PageTitleService', 'services/PostFetchService',
  'directives/PaginationHandlerDirective', 'directives/PostsFiltersHandlerDirective'], function(frontend) {

  frontend.controller('FilteredPostCollectionController', function ($scope, PostFetchService) {
    $scope.posts = [];
    $scope.paginationParams = {
      pageSize: 5,
      currentPage: 0
    };
    $scope.query = $scope.$parent.query;
    $scope.filterParams = {
      postCategory: '',
      postAge: '',
      orderBy: 'newest'
    }; //Inicializar en base a la ruta, sino default;

    $scope.execSearch = () => PostFetchService.searchPosts(
      $scope.query.value, $scope.filterParams.postCategory, $scope.filterParams.postAge, $scope.filterParams.orderBy,
      $scope.paginationParams.pageSize, $scope.paginationParams.currentPage).then(
      resp => {
        $scope.posts = resp.posts;
        $scope.paginationParams = Object.assign($scope.paginationParams, resp.paginationParams);
        $scope.$apply();
        // console.log(resp);
      }).catch(console.log);

    $scope.execSearch();

    $scope.$watchCollection('paginationParams', (newParams, oldParams) => {


      if(newParams.pageSize !== oldParams.pageSize || newParams.currentPage !== oldParams.currentPage){
      console.log("Pagination", newParams, oldParams);
        $scope.execSearch();
      }

    });

    $scope.$watch('query.value', (newQueryVal, oldQueryVal) => {
      if(newQueryVal !== oldQueryVal){
        console.log(newQueryVal, oldQueryVal);
        $scope.execSearch();
      }
    });

    $scope.$watchCollection('filterParams', (newParams, oldParams) => {


      if(newParams.postCategory !== oldParams.postCategory || newParams.postAge !== oldParams.postAge || newParams.orderBy !== oldParams.orderBy){
      console.log("filter", newParams, oldParams);
        $scope.execSearch();
      }

    });
  });
});
