define(['frontend', 'uikit', 'directives/TabDisplayDirective', 'services/UpdateAvatarService',
    'services/utilities/RestFulResponseFactory', 'directives/fetch/FetchPostsDirective',
    'directives/fetch/FetchUsersDirective', 'directives/fetch/FetchCommentsDirective', 'services/LoginService']
  , function(frontend, UIkit) {

    'use strict';
    frontend.controller('profileCtrl', function($scope, $locale, $translate, $location, $routeParams,
                                                UpdateAvatar, RestFulResponse, LoggedUserFactory) {

      $scope.tabs = [
        {value:'posts', message:"{{'PROFILE_POST_TAB_DISPLAY' | translate }}"},
        {value:'comments', message:"{{'PROFILE_COMMENTS_TAB_DISPLAY' | translate }}"},
        {value:'bookmarks', message:"{{'PROFILE_BOOK_TAB_DISPLAY' | translate }}"},
        {value:'following', message:"{{'PROFILE_FOLLOWED_USERS' | translate }}"},
      ];

      $scope.showing = {
        value: $routeParams.showing ? $routeParams.showing : $scope.tabs[0].value
      };

      $scope.setPostsUrl = null;
      $scope.setCommentsUrl = null;
      $scope.setBookmarksUrl = null;
      $scope.setFollowingUrl = null;

      $scope.$watch('showing.value', function(newParam, oldParam, scope) {

        if(newParam !== oldParam) {

          $location.search({ showing: scope.showing.value });

          if(newParam === 'posts' && scope.setPostsUrl !== null) {
            scope.setPostsUrl();
          }
          else if(newParam === 'comments' && scope.setCommentsUrl !== null) {
            scope.setCommentsUrl();
          }
          else if(newParam === 'bookmarks' && scope.setBookmarksUrl !== null) {
            scope.setBookmarksUrl();
          }
          else if(newParam === 'following' && scope.setFollowingUrl !== null) {
            scope.setFollowingUrl();
          }
        }

      }, true);

      if($locale.id === 'es') {
        $scope.followForms = {
          0: 'Seguidores',
          one: 'Seguidor',
          other: 'Seguidores'
        };
        $scope.voteForms = {
          0: 'Votos',
          one: 'Voto',
          other: 'Votos'
        };
      } else {
        $scope.followForms = {
          0: 'Followers',
          one: 'Follower',
          other: 'Followers'
        };
        $scope.voteForms = {
          0: 'Votes',
          one: 'Vote',
          other: 'Votes'
        };
      }

      $scope.nameConstrains = {
        pattern: /^[a-zA-Z ]*$/,
        minLen: 2,
        maxLen: 50
      }

      $scope.usernameConstrains = {
        pattern: /^[a-zA-Z0-9#_]+$/,
        minLen: 6,
        maxLen: 50
      }

      $scope.descriptionConstrains = {
        maxLen: 400
      }

      $scope.passwordConstrains = {
        pattern: /^[^\s]+$/,
        minLen: 12,
        maxLen: 30
      }

      var fieldErrors = {
        name: {
          i18nKey: 'USER_CREATE_NAME',
          message: ''
        },
        username: {
          i18nKey: 'USER_CREATE_USERNAME',
          message: ''
        },
        description: {
          i18nKey: 'USER_DESCRIPTION_PLACEHOLDER',
          message: ''
        },
      }

      $scope.editPass = {
        password: '',
        repeatPassword: ''
      };

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

      $scope.updateUserInfo = function () {
        $scope.editBtnPressed = true;

        if(
          !$scope.fieldIsNotValid('editForm', 'name') &&
          !$scope.fieldIsNotValid('editForm', 'username') &&
          !$scope.descriptionIsNotValid()
        ) {
          $scope.loading = true;
          RestFulResponse.withAuth($scope.loggedUser).then(function (r) {
            var aux = {};
            Object.keys($scope.editInfo).forEach(function (key) {
              if($scope.editInfo[key] !== $scope.loggedUser[key]) {
                aux[key] = $scope.editInfo[key];
              }
            });
            if(Object.keys(aux).length > 0) {
              r.one('/user').customPUT(aux, undefined, undefined, {'Content-Type': 'application/json'})
               .then(function () {
                 $scope.loading = false;
                 Object.assign($scope.loggedUser, aux);
                 $scope.editBtnPressed = false;
                 UIkit.modal(document.getElementById('edit-info-modal')).hide();
                 if(aux.username !== undefined) {
                   LoggedUserFactory.logout().then(function () {
                     $location.path('/login');
                   });
                 }
               })
               .catch(function (err) {
                 $scope.loading = false;
                 if(err.data) {
                   err.data.forEach(function (e) {
                     $translate(fieldErrors[e['attribute']].i18nKey).then(function(field) {
                       $translate('FORM_DUPLICATED_FIELD_ERROR', {field: field}).then(function(msg) {
                         fieldErrors[e['attribute']].message = msg;
                       }).catch(function(err) { console.log('inside', err) });
                     }).catch(function(err){ console.log('outside', err) });
                   });
                 } else {
                   console.log(err);
                 }
               });
            } else {
              $scope.loading = false;
              UIkit.modal(document.getElementById('edit-info-modal')).hide();
            }
          });
        }
      }

      $scope.fieldRequired = function (form, field) {
        return $scope.editBtnPressed && $scope[form][field].$error.required !== undefined;
      }

      // fieldRequired || cons1 || cons2 || ... || consN
      $scope.fieldIsNotValid = function (form, field) {
        return $scope.fieldRequired(form, field) || ($scope[form])[field].$error.pattern || ($scope[form])[field].$error.minlength || ($scope[form])[field].$error.maxlength;
      }

      $scope.descriptionIsNotValid = function () {
        return $scope.fieldRequired('editForm',"description") || $scope.editForm.description.$error.maxlength;
      }

      $scope.passwordsNotEquals = function () {
        return $scope.editPass.password !== $scope.editPass.repeatPassword && !($scope.editPass.password === undefined || $scope.editPass.repeatPassword === undefined);
      }

      $scope.checkFieldError = function (field) {
        if(fieldErrors[field] === undefined) {
          throw 'Field ' + field + 'does not exists!';
        }

        return fieldErrors[field].message !== '';
      }

      $scope.getFieldErrors = function (field) {
        return fieldErrors[field].message;
      }

      $scope.resetFieldErrors = function (field) {
        fieldErrors[field].message = '';
      }

      $scope.resetInfoModal = function () {
        $scope.editInfo = {
          name: $scope.loggedUser.name,
          username: $scope.loggedUser.username,
          description: $scope.loggedUser.description
        }

        $scope.editBtnPressed = false;
        $scope.loading = false;
      }

      $scope.updatePassword = function () {
        $scope.editBtnPressed = true;

        if(
          !$scope.fieldIsNotValid('changePassForm', 'password') &&
          !$scope.fieldIsNotValid('changePassForm', 'repeatPassword') &&
          !$scope.passwordsNotEquals()
        ) {
          $scope.loading = true;
          RestFulResponse.withAuth($scope.loggedUser).then(function (r) {
            delete $scope.editPass.repeatPassword
            r.one('/user').customPUT($scope.editPass, undefined, undefined, {'Content-Type': 'application/json'})
              .then(function () {
                $scope.loading = false;
                $scope.editBtnPressed = false;
                UIkit.modal(document.getElementById('change-password-modal')).hide();
                LoggedUserFactory.logout().then(function () {
                  $location.path('/login');
                });
              }).catch(function (err) {
              $scope.loading = false;
              console.log(err);
            })
          }).catch(function (err) {
            $scope.loading = false;
            console.log(err);
          });
        }
      }

      $scope.resetInfoModal();
    });

});
