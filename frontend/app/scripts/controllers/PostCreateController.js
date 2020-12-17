define(['frontend', 'uikit', 'easymde', 'purify', 'services/entities/PostCategoryService',
  'services/entities/MovieService', 'directives/PostCreateModalDirective', 'services/utilities/PageTitleService',
  'services/entities/PostService'
], function(frontend, UIkit, EasyMDE, DOMPurify) {

  'use strict';
  frontend.controller('PostCreateController', function($scope, PostCategoryService, MovieService, PostService, PageTitle, $location) {
    PageTitle.setTitle('POST_CREATE_TITLE');

    $scope.post = {};
    $scope.moviesTitles = [];
    $scope.moviesList = {};
    $scope.moviesBadges = {};
    $scope.openModalBtnPressed = false;
    $scope.createPostBtnPressed = false;
    $scope.easyMde = configureEasyMDE();

    $scope.categoryMap = {
      "watchlist": "{{'WATCHLIST' | translate }}",
      "critique":"{{'CRITIQUE' | translate }}",
      "debate":"{{'DEBATE' | translate }}",
      "news":"{{'NEWS' | translate }}"
    }

    PostCategoryService.getPostCategories().then(function(optionArray) {
      $scope.postCategories = optionArray;
    }).catch(function() { $location.path('/404') });

    $scope.titleConstraints = {
      pattern: /^[a-zA-Z ]*$/,
      minLen: 6,
      maxLen: 200
    }
    $scope.tagsConstraints = {
      pattern: /^[a-zA-Z ]*$/,
      maxLen: 50
    }
    $scope.bodyConstraints = {
      minLen: 6,
      maxLen: 10000
    }

    function configureEasyMDE(){
      return new EasyMDE({
        element: document.getElementById("create-post-data"),
        spellChecker: false,
        autosave: {
          enabled: true,
          uniqueId: "source",
          delay: 1000,
          text: "Saved: ",
        },
        forceSync: true,
        minHeight: "300px", // This is the default minHeight
        parsingConfig: {
          allowAtxHeaderWithoutSpace: true,
          strikethrough: true,
          underscoresBreakWords: true
        },

        // Upload Image Support Configurations

        inputStyle: "textarea", // Could be contenteditable
        theme: "easymde", // Default

        toolbar: ["bold", "italic", "heading", "|",
          "quote", "unordered-list", "ordered-list", "|",
          "horizontal-rule", "strikethrough",
          "link", "image", "|",
          "preview", "side-by-side", "fullscreen", "|",
          "clean-block", "guide", "|",
          {
            name: "upload",
            action: function () { $scope.validateForm(); },
            className: "fa fa-upload",
            title: "Upload",
          },
        ],

        renderingConfig: {
          sanitizerFunction: function(dirtyHTML) {
            DOMPurify.sanitize(dirtyHTML);
          }
        },

        onToggleFullScreen: easyMdeFullscreenHandle,
      });
    }

    function easyMdeFullscreenHandle(fullscreen){
      document.getElementById('navbar').style.visibility = (fullscreen) ? 'hidden' : 'visible';
    }
    $scope.getCategory = function(option) {
      return $scope.categoryMap[option];
    }

    $scope.validateForm = function() {
      $scope.post.body = document.getElementById('create-post-data').value;
      $scope.openModalBtnPressed = true;

      if(
        !$scope.fieldIsNotValid($scope.openModalBtnPressed,'title') &&
        !$scope.fieldIsNotValid($scope.openModalBtnPressed,'category') &&
        !$scope.bodyValidation($scope.openModalBtnPressed,$scope.post.body)
      ){
        var modalElem = document.getElementById('movies-modal');
        UIkit.modal(modalElem).show();
      }
    }

    function handleCreatePost(postResponse){
      $scope.easyMde.clearAutosavedValue();
      var modalElem = document.getElementById('movies-modal');
      UIkit.modal(modalElem).hide();
      var link = postResponse.headers('Location');
      return '/post' + link.substring(link.indexOf("posts/") + 5);
    }

    $scope.createPost = function () {

      $scope.createPostBtnPressed = true;
      if (
        (Object.keys($scope.post.movies).length > 0 && Object.keys($scope.post.movies).length <= 20) &&
        (Object.keys($scope.post.tags).length <= 5)
      ) {

        var movies = $scope.post.movies;
        $scope.post.movies = [];

        Object.values(movies).forEach(function  (movie) {
          $scope.post.movies.push(movie);
        })

        var tags = $scope.post.tags;
        $scope.post.tags = [];
        Object.keys(tags).forEach(function (tag) {
          $scope.post.tags.push(tag);
        })
        PostService.createPost($scope.post).then(function (postResponse) {
          handleCreatePost(postResponse);
          $location.path(handleCreatePost(postResponse))
        }).catch(console.log);
      }
    }
    $scope.bodyRequired = function (button, body) {
      return button && body === undefined;
    }

    $scope.bodyMinLen = function (button, body) {
      return button && body.length < $scope.bodyConstraints.minLen;
    }

    $scope.bodyMaxLen = function (button, body) {
      return button && body.length > $scope.bodyConstraints.maxLen;
    }

    $scope.bodyValidation = function (button, body) {
      return body === undefined || body.length < $scope.bodyConstraints.minLen || body.length > $scope.bodyConstraints.maxLen;
    }

    $scope.fieldRequired = function (button, field) {
      return button && $scope.createPostForm[field].$error.required !== undefined;
    }

    $scope.fieldIsNotValid = function (button, field) {
      return $scope.fieldRequired(button, field) || $scope.createPostForm[field].$error.pattern ||
        $scope.createPostForm[field].$error.minlength || $scope.createPostForm[field].$error.maxlength;
    }
  });
});
