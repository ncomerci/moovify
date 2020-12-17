define(['frontend', 'services/fetch/PostFetchService', 'services/fetch/CommentFetchService',
  'directives/comments/CommentTreeDirective', 'services/CommentInteractionService', 'services/LoginService', 'services/UserService',
  'services/PostInteractionService', 'directives/EditablePostBodyDirective', 'services/utilities/PageTitleService', 'services/TimeService'], function(frontend) {

  'use strict';
  frontend.controller('PostViewController', function ($scope, PostFetchService, $location, $locale, PostInteractionService,
                           LoggedUserFactory, UserService, TimeService, CommentFetchService, PageTitle, $q, CommentInteractionService, $routeParams) {

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

    PostFetchService.fetchPost(postId).then(function(post) {
      $scope.post = post;
      if(!post.enabled){
        $location.path('404');
      }
      PageTitle.setTitle('POST_VIEW_TITLE', {post:$scope.post.title});
    }).catch(console.log);

    CommentFetchService.getPostCommentsWithUserVote(postId, commentDepth, commentsOrder, commentsPageSize, commentsPageNumber).then(function(comments) {
      $scope.comments = comments;
    }).catch(console.log);

    $scope.newComment = {};
    $scope.newComment.fn = function(newCommentBody){

      return $q(function (resolve, reject) {
        CommentInteractionService.sendPostReply($scope.post, newCommentBody).then(function(newComment) {
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
      PostInteractionService.toggleBookmark($scope.post).then(function(post) {
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
        PostInteractionService.sendVote($scope.post, value).then(function(post) {
          Object.assign($scope.post, post);
          resolve(post.userVote);
        }).catch(console.log);
      });
    }

    $scope.callback.edit = function(newBody) {

      $scope.post.body = newBody;
      return $scope.post.put();
    }

    $scope.deletePost = function() {

      $scope.mutex.deleting = false;

      $scope.post.all('enabled').remove().then(function() {
        $scope.post.enabled = false;
      }).catch(console.log);

    }

    $scope.wordsPerMinute = function () {
      return Math.floor($scope.post.wordCount / wordsPerMin);
    }

  });
});
