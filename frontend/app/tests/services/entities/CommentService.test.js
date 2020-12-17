define(['angular', 'angularMocks', 'frontend', 'services/entities/CommentService', 'restangular',
  'polyfillURLSearchParams'], function(angular) {

  describe('CommentService', function() {

    var $scope;
    var $q;
    var $httpBackend;
    var Restangular;
    var $provide;

    beforeEach(angular.mock.module('frontend'));

    beforeEach(module(function (_$provide_) {
      $provide = _$provide_;
    }));

    beforeEach(inject(function (_$rootScope_, _$q_, _$httpBackend_, _Restangular_) {
      $scope = _$rootScope_;
      $q = _$q_;
      $httpBackend = _$httpBackend_;
      Restangular = _Restangular_;
    }));

    beforeEach(function() {
      var ReqFullResponse = Restangular.withConfig(function (RestangularConfigurer) {
        RestangularConfigurer.setFullResponse(true);
      });

      $provide.value('RestFulResponse', {
        withAuthIfPossible: function() {return $q.resolve(ReqFullResponse)},
        withAuth: function() {return $q.resolve(ReqFullResponse)}
      });

      $provide.value('LoggedUserFactory', {
        getLoggedUser: function() { return null; }
      });

      $provide.value('LinkParserService', {parse: function() {return 10}});
    });

    it('search comments success test', inject(function (CommentService) {

      var query = "queryParam";
      var enabled = true;
      var orderBy = "orderByParam";
      var pageSize = 10;
      var pageNumber = 15;

      var comments = [{id: 1}, {id: 2}, {id: 3}, {id: 4}];

      $httpBackend.expectGET(/.*\/api\/comments\?.*/).respond(function(method, url) {

        var searchParams = new URLSearchParams(url.substring(url.indexOf('?'), url.length));

        expect(searchParams.get('query')).toEqual(query);
        expect(searchParams.get('enabled')).toEqual(enabled.toString());
        expect(searchParams.get('orderBy')).toEqual(orderBy);
        expect(searchParams.get('pageSize')).toEqual(pageSize.toString());
        expect(searchParams.get('pageNumber')).toEqual(pageNumber.toString());

        return [200, [{id: 1}, {id: 2}, {id: 3}, {id: 4}]];
      });

      CommentService.searchComments(query, enabled, orderBy, pageSize, pageNumber).then(function (response) {
        expect(response.collection.map(function(u) {return u.originalElement })).toEqual(comments);
        expect(response.paginationParams).toEqual({pageSize: pageSize, currentPage: pageNumber, lastPage: 0});
        expect(response.queryParams).toEqual(
          {query: query, enabled: enabled, orderBy: orderBy, pageSize: pageSize, pageNumber: pageNumber })
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('fetch comments success test', inject(function (CommentService) {

      var enabled = true;
      var orderBy = "orderByParam";
      var pageSize = 10;
      var pageNumber = 15;

      var comments = [{id: 1}, {id: 2}, {id: 3}, {id: 4}];

      $httpBackend.expectGET(/.*\/api\/comments\?.*/).respond(function(method, url) {

        var searchParams = new URLSearchParams(url.substring(url.indexOf('?'), url.length));

        expect(searchParams.get('enabled')).toEqual(enabled.toString());
        expect(searchParams.get('orderBy')).toEqual(orderBy);
        expect(searchParams.get('pageSize')).toEqual(pageSize.toString());
        expect(searchParams.get('pageNumber')).toEqual(pageNumber.toString());

        return [200, [{id: 1}, {id: 2}, {id: 3}, {id: 4}]];
      });

      CommentService.fetchComments('/comments', enabled, orderBy, pageSize, pageNumber).then(function (response) {
        expect(response.collection.map(function(u) {return u.originalElement })).toEqual(comments);
        expect(response.paginationParams).toEqual({pageSize: pageSize, currentPage: pageNumber, lastPage: 0});
        expect(response.queryParams).toEqual(
          {enabled: enabled, orderBy: orderBy, pageSize: pageSize, pageNumber: pageNumber })
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('fetch one comment test', inject(function(CommentService) {

      var comment = {id: 1};

      $httpBackend.expectGET(/.*\/api\/comments\/1/).respond(200, comment);

      CommentService.fetchOneComment(comment.id).then(function(returnedComment) {
        expect(returnedComment.originalElement).toEqual(comment);
      })

      $httpBackend.flush();

      $scope.$digest();

    }));

    it('send vote success test', inject(function (CommentService) {

      var value = 1;

      var comment = {
        id: 1,
        totalVotes: 0,
        userVote: -1
      }

      $httpBackend.expectPUT(/.*\/api\/comments\/1\/votes/, {value: value}).respond(200);

      CommentService.sendVote(comment, value).then(function (comment) {
        expect(comment.totalVotes).toEqual(2);
        expect(comment.userVote).toEqual(1);
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('send comment reply success test', inject(function (CommentService) {

      var body = "estoEsUnBody";

      var comment = {
        all: function (path) {
          return Restangular.all('/comments/1').all(path);
        }
      }

      var data = {data: 1};

      $httpBackend.expectPOST(/.*\/api\/comments\/1\/children/, {body: body})
        .respond(200, {headers: function() { return "unaURL"}});

      $httpBackend.expectGET('unaURL').respond(200, data);

      CommentService.sendCommentReply(comment, body).then(function (data) {
        expect(data.data).toEqual(1);
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('send post reply success test', inject(function (CommentService) {

      var body = "estoEsUnBody";

      var post = {
        all: function (path) {
          return Restangular.all('/posts/1').all(path);
        }
      }

      var data = {data: 1};

      $httpBackend.expectPOST(/.*\/api\/posts\/1\/comments/, {body: body})
        .respond(200, {headers: function() { return "unaURL"}});

      $httpBackend.expectGET('unaURL').respond(200, data);

      CommentService.sendPostReply(post, body).then(function (data) {
        expect(data.data).toEqual(1);
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('recover comment test', inject(function (CommentService) {

      var comment = { id: 3 };

      $httpBackend.expectPUT(/.*\/api\/comments\/3\/enabled/).respond(204);

      CommentService.recoverComment(comment).then(function (returnedComment) {
        expect(returnedComment).toEqual(comment);
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

  });
});
