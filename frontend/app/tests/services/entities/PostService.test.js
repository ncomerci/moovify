define(['angular', 'angularMocks', 'frontend', 'services/entities/PostService', 'restangular',
  'polyfillURLSearchParams'], function(angular) {

  describe('PostService', function() {

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

    it('search posts success test', inject(function (PostService) {

      var query = "queryParam";
      var category = "categoryParam";
      var age = "ageParam";
      var enabled = true;
      var orderBy = "orderByParam";
      var pageSize = 10;
      var pageNumber = 15;

      var posts = [{id: 1}, {id: 2}, {id: 3}, {id: 4}];

      $httpBackend.expectGET(/.*\/api\/posts\?.*/).respond(function(method, url) {

        var searchParams = new URLSearchParams(url.substring(url.indexOf('?'), url.length));

        expect(searchParams.get('query')).toEqual(query);
        expect(searchParams.get('postCategory')).toEqual(category);
        expect(searchParams.get('postAge')).toEqual(age);
        expect(searchParams.get('enabled')).toEqual(enabled.toString());
        expect(searchParams.get('orderBy')).toEqual(orderBy);
        expect(searchParams.get('pageSize')).toEqual(pageSize.toString());
        expect(searchParams.get('pageNumber')).toEqual(pageNumber.toString());

        return [200, [{id: 1}, {id: 2}, {id: 3}, {id: 4}]];
      });

      PostService.searchPosts(query, category, age, enabled, orderBy, pageSize, pageNumber).then(function (response) {
        expect(response.collection.map(function(u) {return u.originalElement })).toEqual(posts);
        expect(response.paginationParams).toEqual({pageSize: pageSize, currentPage: pageNumber, lastPage: 0});
        expect(response.queryParams).toEqual(
          {query: query, postCategory: category, postAge: age, enabled: enabled, orderBy: orderBy, pageSize: pageSize, pageNumber: pageNumber })
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('fetch posts success test', inject(function (PostService) {

      var enabled = true;
      var orderBy = "orderByParam";
      var pageSize = 10;
      var pageNumber = 15;

      var posts = [{id: 1}, {id: 2}, {id: 3}, {id: 4}];

      $httpBackend.expectGET(/.*\/api\/posts\?.*/).respond(function(method, url) {

        var searchParams = new URLSearchParams(url.substring(url.indexOf('?'), url.length));

        expect(searchParams.get('enabled')).toEqual(enabled.toString());
        expect(searchParams.get('orderBy')).toEqual(orderBy);
        expect(searchParams.get('pageSize')).toEqual(pageSize.toString());
        expect(searchParams.get('pageNumber')).toEqual(pageNumber.toString());

        return [200, [{id: 1}, {id: 2}, {id: 3}, {id: 4}]];
      });

      PostService.fetchPosts('/posts', enabled, orderBy, pageSize, pageNumber).then(function (response) {
        expect(response.collection.map(function(u) {return u.originalElement })).toEqual(posts);
        expect(response.paginationParams).toEqual({pageSize: pageSize, currentPage: pageNumber, lastPage: 0});
        expect(response.queryParams).toEqual(
          {enabled: enabled, orderBy: orderBy, pageSize: pageSize, pageNumber: pageNumber })
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('post create success test', inject(function (PostService) {

      var post = {id: 1};

      $httpBackend.expectPOST(/.*\/api\/posts/, post).respond(201);

      PostService.createPost(post);

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('send vote success test', inject(function (PostService) {

      var value = 1;

      var post = {
        all: function (path) {
          return Restangular.all('/posts/1').all(path);
        },
        totalLikes: 0,
        userVote: -1
      }

      $httpBackend.expectPUT(/.*\/api\/posts\/1\/votes/, {value: value}).respond(200);

      PostService.sendVote(post, value).then(function (post) {
        expect(post.totalLikes).toEqual(2);
        expect(post.userVote).toEqual(1);
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('toggle bookmark false test', inject(function (PostService) {

      $httpBackend.expectDELETE(/.*\/api\/user\/bookmarked\/1/).respond(204);

      var post = {
        hasUserBookmarked: true,
        id: 1
      }

      PostService.toggleBookmark(post).then(function (post) {
        expect(post.hasUserBookmarked).toEqual(false);
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('toggle bookmark true test', inject(function (PostService) {

      $httpBackend.expectPUT(/.*\/api\/user\/bookmarked\/1/).respond(204);

      var post = {
        hasUserBookmarked: false,
        id: 1
      }

      PostService.toggleBookmark(post).then(function (post) {
        expect(post.hasUserBookmarked).toEqual(true);
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('recover post test', inject(function (PostService) {

      $httpBackend.expectPUT(/.*\/api\/posts\/1\/enabled/).respond(204);

      var post = {id: 1};

      PostService.recoverPost(post).then(function (returnedPost) {
        expect(returnedPost).toEqual(post);
      });

      $httpBackend.flush();
    }));




  });
});
