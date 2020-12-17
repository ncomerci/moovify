'use strict';
define(['frontend', 'services/utilities/RestFulResponseFactory', 'services/LoginService',
  'services/utilities/LinkParserService'], function(frontend) {

  frontend.service('CommentService', function (RestFulResponse, $q, LoggedUserFactory, LinkParserService) {

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

    this.getCommentCommentsWithUserVoteById = function (commentId, depth, orderBy, pageSize, pageNumber){

      if(depth)
        depth = 0

      var queryParams = {orderBy: orderBy, pageSize: pageSize, pageNumber: pageNumber};

      return $q(function(resolve, reject) {

        RestFulResponse.withAuthIfPossible(LoggedUserFactory.getLoggedUser()).then(function (Restangular){
          Restangular.one('comments', commentId).all('children').getList(queryParams).then(function (response) {
            handleCommentResponse(response, depth, orderBy, pageSize, pageNumber, resolve, reject);
          }).catch(reject);
        }).catch(reject);
      });
    };

    this.fetchOneComment = function (commentId) {

      return $q(function(resolve, reject) {

        RestFulResponse.withAuthIfPossible(LoggedUserFactory.getLoggedUser()).then(function (Restangular){
          Restangular.one('comments', commentId).get().then(function (response) {
           resolve(response.data);
          }).catch(reject);
        }).catch(reject);
      });
    }

    this.searchComments = function (query, enabled, orderBy, pageSize, pageNumber) {
      return fetchComments('/comments', query, enabled, orderBy, pageSize, pageNumber);
    }

    this.fetchComments = function(path, enabled, orderBy, pageSize, pageNumber) {
      return fetchComments(path, null, enabled, orderBy, pageSize, pageNumber);
    }

    function fetchComments (path, query, enabled, orderBy, pageSize, pageNumber) {
      // Obligatory params
      var queryParams = {
        orderBy: orderBy,
        pageSize: pageSize ? pageSize : 5,
        pageNumber: pageNumber ? pageNumber : 0
      };

      if(query)
        queryParams.query = query;

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

          }).catch(function(response) { reject({status: response.status, message: 'CommentService: FetchComment'})
          });
        }).catch(reject);
      });
    }

    this.sendVote =  function(comment, value) {

      return $q(function(response, reject){
        RestFulResponse.withAuth(LoggedUserFactory.getLoggedUser()).then(function(Restangular) {
          Restangular.one('comments', comment.id).all('votes').customPUT({value: value}).then(function() {
            comment.totalVotes -= comment.userVote;
            comment.userVote = value;
            comment.totalVotes += comment.userVote;
            response(comment);
          }).catch(reject);
        }).catch(reject);
      })
    }

    this.sendCommentReply = function(comment, newCommentBody) {

      return $q(function(resolve, reject){
        RestFulResponse.withAuth(LoggedUserFactory.getLoggedUser()).then(function(Restangular) {
          comment.all('children').post({body: newCommentBody}).then(function(response) {
            Restangular.oneUrl('comments', response.headers('Location')).get().then(function(response) {
              resolve(response.data);
            }).catch(reject);
          }).catch(reject);
        }).catch(reject);
      });
    }

    this.sendPostReply = function(post, newCommentBody) {

      return $q(function(resolve, reject){
        RestFulResponse.withAuth(LoggedUserFactory.getLoggedUser()).then(function(Restangular) {
          post.all('comments').post({body: newCommentBody}).then(function(response) {
            Restangular.oneUrl('comments', response.headers('Location')).get().then(function(response) {
              resolve(response.data);
            }).catch(reject);
          }).catch(reject);
        }).catch(reject);
      });
    }

    this.getCommentCommentsWithUserVote = function (commentId, depth, orderBy, pageSize, pageNumber){

      if(depth)
        depth = 0

      if(!orderBy)
        orderBy = 'hottest';

      if(!pageSize)
        pageSize = 5;

      if(!pageNumber)
        pageNumber = 0;

      return getCommentCommentsWithUserVoteInternal(commentId, depth, orderBy, pageSize, pageNumber)
    }

    this.recoverComment = function (comment) {
      return $q(function (resolve, reject) {
        RestFulResponse.withAuthIfPossible(LoggedUserFactory.getLoggedUser()).then(function (Restangular) {
          Restangular.one('comments', comment.id).all('enabled').doPUT().then(function () {
            resolve(comment);
          }).catch(reject);
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
