define(['frontend', 'uikit','services/entities/PostService', 'services/entities/CommentService',
  'directives/comments/CommentTreeDirective', 'services/LoginService', 'services/entities/UserService',
  'directives/EditablePostBodyDirective', 'services/utilities/PageTitleService', 'services/utilities/TimeService'], function(frontend, UIkit) {

  'use strict';
  frontend.controller('PostViewController', function ($scope, $location, $locale, $q, $routeParams, PostService,
                           LoggedUserFactory, UserService, TimeService, CommentService, PageTitle) {

    PageTitle.setTitle('POST_VIEW_TITLE');

    $scope.post = null;
    $scope.comments = null;

    $scope.isUser = false;
    $scope.isAdmin = false;
    $scope.mutex = {
      bookmark: false,
      deleting: false
    };
    var loggedUser = LoggedUserFactory.getLoggedUser();
    $scope.isLogged = loggedUser.logged;
    if(loggedUser.logged) {
      $scope.isAdmin = UserService.userHasRole(loggedUser, 'ADMIN');
      $scope.isUser = UserService.userHasRole(loggedUser, 'USER');
    }

    var postId = $routeParams.id;
    var commentDepth = 0;
    var commentsOrder = 'newest';
    var commentsPageSize = 5;
    var commentsPageNumber = 0;
    var wordsPerMin = 150;

    if($locale.id === 'es') {
      $scope.minsForm = {
        0:'Toma menos de un minuto para leerse',
        1:'Toma un minuto para leerse',
        other:'Toma {} minutos para leerse'
      }
    }
    else {
      $scope.minsForm = {
        0:'Takes less than a minute for reading',
        1:'Takes a minute for reading',
        other:'Takes {} minutes for reading'
      }
    }

    PostService.fetchPost(postId).then(function(post) {
      $scope.post = post;
      if(!post.enabled){
        $location.path('404');
      }
      PageTitle.setTitle('POST_VIEW_TITLE', {post:$scope.post.title});
    }).catch(console.log);

    CommentService.getPostCommentsWithUserVote(postId, commentDepth, commentsOrder, commentsPageSize, commentsPageNumber).then(function(comments) {
      $scope.comments = comments;
    }).catch(console.log);

    $scope.newComment = {};
    $scope.newComment.fn = function(newCommentBody){

      return $q(function (resolve, reject) {
        CommentService.sendPostReply($scope.post, newCommentBody).then(function(newComment) {
          $scope.comments.unshift(newComment);
          resolve(newComment);
        }).catch(reject);
      });

    }

    $scope.getDateFormatted = function (creationDate){
      return TimeService.getDateFormatted(creationDate);
    };

    $scope.getMovieYear = function (date) {
      return new Date(date).getFullYear();
    }

    $scope.toggleBookmark = function () {
      $scope.mutex.bookmark = true;
      PostService.toggleBookmark($scope.post).then(function(post) {
        $scope.post.hasUserBookmarked = post.hasUserBookmarked;
        $scope.mutex.bookmark = false;
      })
    }

    $scope.callback = {
      startReply: {},
      startEdit: {}
    };
    $scope.callback.vote = function (value) {

      if($scope.post.userVote === value) {
        value = 0;
      }

      return $q(function(resolve, reject) {
        PostService.sendVote($scope.post, value).then(function(post) {
          Object.assign($scope.post, post);
          resolve(post.userVote);
        }).catch(console.log);
      });
    }

    $scope.callback.edit = function(newBody) {
      $scope.post.body = newBody;
      return $scope.post.put();
    }

    $scope.openDeleteModal = function () {
      $scope.deletingPost = true;
      UIkit.modal(document.getElementById('delete-post-modal')).show();
    }


    $scope.confirmDelete = function() {

      $scope.mutex.deleting = true;

      $scope.post.all('enabled').remove().then(function() {
        $scope.post.enabled = false;
        UIkit.modal(document.getElementById('delete-post-modal')).hide();
        $location.path('/');
      }).catch(console.log);

    }

    $scope.wordsPerMinute = function (wordCount) {
      return Math.floor(wordCount / wordsPerMin);
    }

  });
});
