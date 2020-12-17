define(['frontend', 'services/utilities/PageTitleService'], function(frontend) {

  'use strict';
  frontend.controller('errorCtrl', function($scope, PageTitle, $location) {

    console.log($location.path());

    if($location.path().match(/.*500$/)) {
      PageTitle.setTitle('PAGE_500');
    }
    else if($location.path().match(/.*404$/)) {
      PageTitle.setTitle('PAGE_404');
    }

    $scope.goHome = function () {
      $location.path('/');
    }
  });
});
