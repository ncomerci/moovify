'use strict';
define(['frontend','easymde', 'marked', 'purify'], function(frontend,EasyMDE,marked, DOMPurify) {

  frontend.directive('editablePostBodyDirective', function () {

    return {
      restrict: 'E',
      scope: {
        body: '=',
        sendUpdateFn: '&',
        isEditable: '='
      },
      templateUrl:'resources/views/directives/editablePostBodyDirective.html',
      link: function (scope){
        scope.sendUpdateFn = scope.sendUpdateFn();
        scope.bodyConstraints = {
          maxLen: 100000,
          minLen: 0
        };


      },
      controller: function ($scope){

        marked.setOptions({
          gfm: true,
          breaks: true,
          sanitizer: DOMPurify.sanitize,
          //  silent: true,
        });

        $scope.sendingEdit = false;
        $scope.editing = {value: false};
        $scope.htmlBody = marked($scope.body);
        $scope.mde = null;
        $scope.hasError = {value: false};

        $scope.startEdit = function () {

          $scope.editing.value = true;

          if(!$scope.mde){
            $scope.mde = $scope.mde = new EasyMDE({
              element: document.getElementById('edit-post-body'),
              spellChecker: false,
              // autosave: {
              //   enabled: true,
              //   uniqueId: "source",
              //   delay: 1000,
              //   text: "Saved: ",
              // },
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
                "preview", "side-by-side", "|",
                "clean-block", "guide", "|"
              ],

              renderingConfig: {
                sanitizerFunction: function(dirtyHTML) {
                  DOMPurify.sanitize(dirtyHTML);
                }
              }
            });
          }
        }

        $scope.sendEdit = function () {

          var prevBody = $scope.body;

          $scope.body = document.getElementById('edit-post-body').value;

          if($scope.bodyRequired() || $scope.bodyMinLen() || $scope.bodyMaxLen()){

            console.log('ERRORS');
            $scope.hasError.value = true;
            $scope.hasError.bodyRequired = $scope.bodyRequired();
            $scope.hasError.bodyMinLen = $scope.bodyMinLen();
            $scope.hasError.bodyMaxLen = $scope.bodyMaxLen();
            $scope.body = prevBody;
            return;
          }

          $scope.hasError = false;
          $scope.sendingEdit = true;

          $scope.sendUpdateFn($scope.body).then(function () {
            $scope.sendingEdit = false;
            $scope.editing.value = false;
            $scope.htmlBody = marked($scope.body);
          }).catch(console.log);
        }

        $scope.bodyRequired = function () {
          return $scope.body === undefined;
        }

        $scope.bodyMinLen = function () {
          return $scope.body.length <= 0;
        }

        $scope.bodyMaxLen = function () {
          return $scope.body.length > $scope.bodyConstraints.maxLen;
        }

      }
    }
  });
});
