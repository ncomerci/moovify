'use strict';
define(['frontend', 'services/utilities/RestFulResponseFactory', 'services/LoginService',
  'services/utilities/LinkParserService'], function(frontend) {

  frontend.service('CommentFetchService', function (RestFulResponse, $q, LoggedUserFactory, LinkParserService) {

    // devolver comments con sus comments hijos cargados.

    this.getPostCommentsWithUserVote = function (postId, depth, orderBy, pageSize, pageNumber) {

      var queryParams = {orderBy: orderBy, pageSize: pageSize, pageNumber: pageNumber};

      return $q(function(resolve, reject) {

        RestFulResponse.withAuthIfPossible(LoggedUserFactory.getLoggedUser())
          .then(function (Restangular){
            Restangular.one('posts', postId).all('comments').getList(queryParams).then(function (response){
              handleCommentResponse(response, depth, orderBy, pageSize, pageNumber, resolve, reject);
            }).catch(reject);
          }).catch(reject);
      });
    };

    this.getCommentCommentsWithUserVote = function (comment, depth, orderBy, pageSize, pageNumber){

      if(depth)
        depth = 0

      if(!orderBy)
        orderBy = 'hottest';

      if(!pageSize)
        pageSize = 5;

      if(!pageNumber)
        pageNumber = 0;

      return getCommentCommentsWithUserVoteInternal(comment, depth, orderBy, pageSize, pageNumber)
    };

    this.fetchComments = function(path, enabled, orderBy, pageSize, pageNumber) {

      // Obligatory params
      var queryParams = {
        orderBy: orderBy,
        pageSize: pageSize ? pageSize : 5,
        pageNumber: pageNumber ? pageNumber : 0
      };

      if(enabled !== null)
        queryParams.enabled = enabled;

      return $q(function(resolve, reject) {
        RestFulResponse.withAuthIfPossible(LoggedUserFactory.getLoggedUser()).then(function (Restangular) {

          Restangular.all(path).getList(queryParams).then(function(commentResponse) {

            var paginationParams = {lastPage: 0, pageSize: queryParams.pageSize, currentPage: queryParams.pageNumber};
            var linkHeader = commentResponse.headers('Link');
            var comments = commentResponse.data;

            // Si no hay Link -> no habia contenido -> no me interesa paginar nada
            if(linkHeader){
              paginationParams.lastPage = LinkParserService.parse(linkHeader);
            }

            resolve({collection: comments, paginationParams: paginationParams, queryParams: queryParams});

          }).catch(function(response) { reject({status: response.status, message: 'CommentFetchService: FetchComment'})
          });
        }).catch(reject);
      });
    };

    function getCommentCommentsWithUserVoteInternal(comment, depth, orderBy, pageSize, pageNumber) {

      var queryParams = {orderBy: orderBy, pageSize: pageSize, pageNumber: pageNumber};

      comment.childrenFetched = true;

      return $q(function(resolve, reject) {

        RestFulResponse.withAuthIfPossible(LoggedUserFactory.getLoggedUser()).then(function (Restangular){
          comment.all('children').getList(queryParams).then(function (response) {
            handleCommentResponse(response, depth, orderBy, pageSize, pageNumber, resolve, reject);
          }).catch(reject);
        }).catch(reject);

      });
    }

    function handleCommentResponse(response, depth, orderBy, pageSize, pageNumber, resolve, reject) {

      var comments = response.data;

      var linkHeader = response.headers('Link');

      comments.hasNext = false;

      if(linkHeader){
        var linkMap = LinkParserService.getLinksMaps(linkHeader);

        comments.hasNext = linkMap.hasOwnProperty('next');

        if(comments.hasNext){
          comments.getNext = function(depth) {

            return $q(function(resolve, reject) {
              RestFulResponse.withAuthIfPossible(LoggedUserFactory.getLoggedUser()).then(function (Restangular){
                Restangular.allUrl('comments', linkMap.next.url).getList().then(function (response) {
                  handleCommentResponse(response, depth, linkMap.orderBy,
                    parseInt(linkMap.pageSize), parseInt(linkMap.pageNumber), resolve, reject);
                }).catch(reject);
              }).catch(reject);

            });
          }
        }
      }
      var promiseArray;

      if(depth > 0) {
        promiseArray = comments.map(function (comment) {
          return loadChildComments(comment, depth, orderBy, pageSize, pageNumber);
        });
      }
      else {
        comments.forEach(function (comment) {
          comment.childrenFetched = false;
        });
        promiseArray = [$q.resolve(comments)];
      }

      Promise.all(promiseArray).then().catch(console.log);

      resolve(comments);

    }

    function loadChildComments(comment, depth, orderBy, pageSize, pageNumber) {

      return $q(function(resolve, reject) {

        getCommentCommentsWithUserVoteInternal(comment, depth - 1, orderBy, pageSize, pageNumber)
          .then(function (response) {

            comment.children = response;
            comment.loadingChildren = false;

            resolve(comment);
          }).catch(reject);

      });
    }
  });
});
