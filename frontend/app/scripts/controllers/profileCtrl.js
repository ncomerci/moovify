define(['frontend', 'uikit', 'directives/TabDisplayDirective', 'services/UpdateAvatarService'], function(frontend, UIkit) {

    'use strict';
    frontend.controller('profileCtrl', function($scope, UpdateAvatar) {

      $scope.tabs = [
        {value:'posts', message:"{{'PROFILE_POST_TAB_DISPLAY' | translate }}"},
        {value:'comments', message:"{{'PROFILE_COMMENTS_TAB_DISPLAY' | translate }}"},
        {value:'bookmarks', message:"{{'PROFILE_BOOK_TAB_DISPLAY' | translate }}"}
      ];

      $scope.selectedTab = {
        value: 'posts' // default selected tab
      }

      $scope.avatar = UpdateAvatar.getAvatar();
      var inputFile = angular.element(document.getElementById('avatar'))[0];

      inputFile.addEventListener('change', function () {
        UpdateAvatar.setFile(inputFile.files[0]);
        $scope.$apply();
        if(!$scope.avatar.error && $scope.avatar.file !== undefined) {
          UIkit.modal(document.getElementById('avatar-update-modal')).show();
        }
      });

      $scope.uploadAvatar = function () {
        UpdateAvatar.uploadAvatar();
        UIkit.modal(document.getElementById('avatar-update-modal')).hide();
      };


    });

});
