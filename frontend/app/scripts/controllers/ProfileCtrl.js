define(['frontend', 'uikit', 'directives/TabDisplayDirective', 'directives/fetch/FetchPostsDirective',
    'directives/fetch/FetchUsersDirective', 'directives/fetch/FetchCommentsDirective', 'services/LoginService',
    'services/entities/UserService', 'services/utilities/PageTitleService']
  , function(frontend, UIkit) {

    'use strict';
    frontend.controller('ProfileCtrl', function($scope, $locale, $translate, $location, $routeParams,
                                                LoggedUserFactory, UserService, PageTitle, $q) {

      var routeID = parseInt($routeParams.id);
      $scope.user = {};
      $scope.loadUserFinished = false;
      $scope.isFollowed = false;

      if(routeID) {
        if(routeID !== $scope.loggedUser.id) {

          var getUserData = UserService.getUser(routeID).then(function (u) {
            if(u.enabled === false) {
              $location.path('/404');
            }
            Object.assign($scope.user, u);
            $scope.isAdmin = UserService.userHasRole($scope.user, 'ADMIN');
            $scope.tabs = [
              {value:'posts', message:"{{'USER_POST_TAB_DISPLAY' | translate}}"},
              {value:'comments', message:"{{'USER_COMMENTS_TAB_DISPLAY' | translate}}"},
              {value:'bookmarks', message:"{{'USER_BOOK_TAB_DISPLAY' | translate}}"},
              {value:'following', message:"{{'USER_FOLLOWED_USERS' | translate}}"},
            ];
            PageTitle.setTitle('PROFILE_TITLE', {user:$scope.user.username});
          }).catch(function(response) {
            if(response.status === 404) {
              $location.path('/404');
            }
            else {
              $location.path('/500');
            }
          });

          var followedUser = UserService.doLoggedUserFollow($scope.loggedUser, routeID).then(function (bool) {
            $scope.isFollowed = bool;
          }).catch(); // no need for 500

          $q.all(getUserData, followedUser).then(function () {
            $scope.loadUserFinished = true;
          })
        } else {
          Object.assign($scope.user, $scope.loggedUser);
          PageTitle.setTitle('PROFILE_TITLE', {user:$scope.user.username});
          $scope.isAdmin = UserService.userHasRole($scope.loggedUser, 'ADMIN');
          $scope.loadUserFinished = true;
        }
      } else {

        if(!$scope.loggedUser.logged){
          $location.path('/404');
        }

        Object.assign($scope.user, $scope.loggedUser);
        PageTitle.setTitle('PROFILE_TITLE', {user:$scope.user.username});
        $scope.isAdmin = UserService.userHasRole($scope.loggedUser, 'ADMIN');
        $scope.loadUserFinished = true;
      }


      $scope.profileSameAsLogged = function () {
        return $routeParams.id === undefined || routeID === $scope.loggedUser.id;
      }

      if(UserService.userHasRole($scope.loggedUser, 'NOT_VALIDATED')) {
        UIkit.modal(document.getElementById('confirm-email-profile-modal')).show();
      }

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

      // Change on back and forward
      $scope.$on('$locationChangeSuccess', function() {
        if($routeParams.showing !== $scope.showing.value)
          $scope.showing.value = $routeParams.showing ? $routeParams.showing : $scope.tabs[0].value;
      });

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

      $scope.avatar = UserService.avatar.get();
      var inputFile = angular.element(document.getElementById('avatar'))[0];

      inputFile.addEventListener('change', function () {
        UserService.avatar.setFile(inputFile.files[0]);
        $scope.$apply();
        if(!$scope.avatar.error && $scope.avatar.file !== undefined) {
          UIkit.modal(document.getElementById('avatar-update-modal')).show();
        }
      });

      $scope.uploadAvatar = function () {
        UserService.avatar.upload().then(function () {
          $scope.user.avatar = $scope.loggedUser.avatar;
        });
        UIkit.modal(document.getElementById('avatar-update-modal')).hide();
      };

      $scope.updateUserInfo = function () {
        $scope.btnPressed = true;

        if(
          !$scope.fieldIsNotValid('editForm', 'name') &&
          !$scope.fieldIsNotValid('editForm', 'username') &&
          !$scope.descriptionIsNotValid()
        ) {
          $scope.loading = true;
          var aux = {};
          Object.keys($scope.editInfo).forEach(function (key) {
            if($scope.editInfo[key] !== $scope.loggedUser[key]) {
              aux[key] = $scope.editInfo[key];
            }
          });
          if(Object.keys(aux).length > 0) {
            UserService.updateInfo($scope.loggedUser, aux).then(function () {
              Object.assign($scope.loggedUser, aux);
              Object.assign($scope.user, aux);
              $scope.loading = false;
              $scope.btnPressed = false;
              UIkit.modal(document.getElementById('edit-info-modal')).hide();
            }).catch(function (err) {
              $scope.loading = false;
              if(err.data) {
                err.data.forEach(function (e) {
                  $translate(fieldErrors[e['attribute']].i18nKey).then(function(field) {
                    $translate('FORM_DUPLICATED_FIELD_ERROR', {field: field}).then(function(msg) {
                      fieldErrors[e['attribute']].message = msg;
                    }).catch(); // no need for 500
                  }).catch();
                });
              }
            })
          }
          else {
            $scope.loading = false;
            UIkit.modal(document.getElementById('edit-info-modal')).hide();
          }
        }
      }

      $scope.fieldRequired = function (form, field) {
        return $scope.btnPressed && $scope[form][field].$error.required !== undefined;
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

        $scope.btnPressed = false;
        $scope.loading = false;
      }

      $scope.updatePassword = function () {
        $scope.btnPressed = true;

        if(
          !$scope.fieldIsNotValid('changePassForm', 'password') &&
          !$scope.fieldIsNotValid('changePassForm', 'repeatPassword') &&
          !$scope.passwordsNotEquals()
        ) {
          $scope.loading = true;
          delete $scope.editPass.repeatPassword;
          UserService.updatePassword($scope.loggedUser, $scope.editPass).then(function () {
            $scope.loading = false;
            $scope.btnPressed = false;
            UIkit.modal(document.getElementById('change-password-modal')).hide();
            $translate('UPDATE_PASS_SUCCESS_ALT').then(function (msg) {
              UIkit.notification({message: '<span uk-icon=\'icon: check\'></span> '+msg, status:'success'})
            });
          }).catch(function (err) {
            $scope.loading = false;
          });
        }
      }

      $scope.resetInfoModal();

      $scope.confirmEmail = {};
      $scope.tokenError = false;

      $scope.sendConfirmation = function () {
        $scope.btnPressed = true;
        if(
          !$scope.fieldRequired('confirmMailForm', 'token')
        ) {
          UserService.sendConfirmToken($scope.loggedUser, $scope.confirmEmail).then(function () {
            UIkit.modal(document.getElementById('confirm-email-profile-modal')).hide();
          }).catch(function (err) {
            $scope.tokenError = true;
          });
        }
      }

      $scope.resendSuccess = false;
      $scope.resendError = false;

      $scope.resendEmail = function () {
        UserService.resendConfirmEmail($scope.loggedUser).then(function () {
          $scope.resendSuccess = true;
        }).catch(function (err) {
          $scope.resendError = true;
        });
      }

      $scope.followUser = function () {
        UserService.followUser($scope.loggedUser, $scope.user).then(function () {
          $scope.isFollowed = true;
        }).catch();
      }

      $scope.unfollowUser = function () {
        UserService.unfollowUser($scope.loggedUser, $scope.user).then(function () {
          $scope.isFollowed = false;
        }).catch();
      }

      $scope.promoteUser = function () {
        UserService.promoteUser($scope.loggedUser, $scope.user).then(function () {
          $scope.isAdmin = true;
          UIkit.modal(document.getElementById('promote-modal')).hide();
        }).catch();
      }

      $scope.deleteUser = function () {
        UserService.deleteUser($scope.loggedUser, $scope.user).then(function () {
          UIkit.modal(document.getElementById('delete-modal')).hide();
          $location.path('/');
        }).catch();
      }

      $scope.logoutEverywhere = function () {
        LoggedUserFactory.logoutEverywhere().then(function () {
          $translate('ACTION_WARNING').then(function (msg) {
            UIkit.notification({message: msg, status: 'warning'});
          });
        });
      };
    });
});
