define(['frontend', 'services/utilities/PageTitleService'], function(frontend) {

  'use strict';
  frontend.controller('errorCtrl', function($scope, PageTitle, $location) {
    PageTitle.setTitle('PAGE_404');
    $scope.goHome = function () {
      $location.path('/');
    }
  });
});
