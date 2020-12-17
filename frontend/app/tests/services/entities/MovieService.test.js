define(['angular', 'angularMocks', 'frontend', 'services/entities/MovieService', 'restangular', 'polyfillURLSearchParams'], function(angular) {

  describe('MovieService', function() {

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

      $provide.value('RestFulResponse', {noAuth: function() {return ReqFullResponse}});

      $provide.value('LoggedUserFactory', {
        getLoggedUser: function() { return null; }
      });

      $provide.value('LinkParserService', {parse: function() {return 10}});
    });

    it('search movies success test', inject(function (MovieService) {

      var query = "queryParam";
      var category = "categoryParam";
      var decade = "decadeParam";
      var orderBy = "orderByParam";
      var pageSize = 10;
      var pageNumber = 15;

      var movies = [{id: 1}, {id: 2}, {id: 3}, {id: 4}];

      $httpBackend.expectGET(/.*\/api\/movies\?.*/).respond(function(method, url) {

        var searchParams = new URLSearchParams(url.substring(url.indexOf('?'), url.length));

        expect(searchParams.get('query')).toEqual(query);
        expect(searchParams.get('movieCategory')).toEqual(category);
        expect(searchParams.get('decade')).toEqual(decade);
        expect(searchParams.get('orderBy')).toEqual(orderBy);
        expect(searchParams.get('pageSize')).toEqual(pageSize.toString());
        expect(searchParams.get('pageNumber')).toEqual(pageNumber.toString());

        return [200, [{id: 1}, {id: 2}, {id: 3}, {id: 4}]];
      });

      MovieService.searchMovies(query, category, decade, orderBy, pageSize, pageNumber).then(function (response) {
        expect(response.collection.map(function(u) {return u.originalElement })).toEqual(movies);
        expect(response.paginationParams).toEqual({pageSize: pageSize, currentPage: pageNumber, lastPage: 0});
        expect(response.queryParams).toEqual(
          {query: query, movieCategory: category, decade: decade, orderBy: orderBy, pageSize: pageSize, pageNumber: pageNumber })
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('fetch movies success test', inject(function (MovieService) {

      var orderBy = "orderByParam";
      var pageSize = 10;
      var pageNumber = 15;

      var movies = [{id: 1}, {id: 2}, {id: 3}, {id: 4}];

      $httpBackend.expectGET(/.*\/api\/movies\?.*/).respond(function(method, url) {

        var searchParams = new URLSearchParams(url.substring(url.indexOf('?'), url.length));

        expect(searchParams.get('orderBy')).toEqual(orderBy);
        expect(searchParams.get('pageSize')).toEqual(pageSize.toString());
        expect(searchParams.get('pageNumber')).toEqual(pageNumber.toString());

        return [200, [{id: 1}, {id: 2}, {id: 3}, {id: 4}]];
      });

      MovieService.fetchMovies('/movies', orderBy, pageSize, pageNumber).then(function (response) {
        expect(response.collection.map(function(u) {return u.originalElement })).toEqual(movies);
        expect(response.paginationParams).toEqual({pageSize: pageSize, currentPage: pageNumber, lastPage: 0});
        expect(response.queryParams).toEqual(
          { orderBy: orderBy, pageSize: pageSize, pageNumber: pageNumber })
      });

      $httpBackend.flush();

      $scope.$digest();
    }));
  });
});
