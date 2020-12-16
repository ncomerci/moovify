define(['angular', 'angularMocks', 'frontend', 'services/LoginService', 'restangular', 'polyfillObjectAssign'], function(angular) {

  describe('LoginService', function() {

    var $scope;
    var $q;
    var $httpBackend;
    var $location;

    var Restangular;
    var ReqFullResponse;

    var $provide;

    var expDate = new Date(30);

    beforeEach(angular.mock.module('frontend'));

    beforeEach(module(function(_$provide_) {
      $provide = _$provide_;
    }))

    beforeEach(inject(function (_$rootScope_, _$q_, _$httpBackend_, _$location_, _Restangular_) {
      $scope = _$rootScope_;
      $q = _$q_;
      $httpBackend = _$httpBackend_;
      $location = _$location_;
      Restangular = _Restangular_;
    }));

    beforeEach(function() {
      ReqFullResponse = Restangular.withConfig(function (RestangularConfigurer) {
        RestangularConfigurer.setFullResponse(true);
      });

      var RestFulResponseMock = {
        withAuth: function() { return $q.resolve(ReqFullResponse) },
        setToken: function() { return expDate },
        noAuth: function() { return ReqFullResponse },
        clearHeaders: function() { ReqFullResponse.setDefaultHeaders({}) }
      };

      $provide.value('RestFulResponse', RestFulResponseMock);
    });

    it('save token test', inject(function(LoggedUserFactory) {

      var token = "token";

      var user = { username: "hola", password: "chau" };

      $httpBackend.expectGET(/.*\/api\/user/).respond(200, user);

      LoggedUserFactory.saveToken(token).then(function(loggedUser) {
        expect(loggedUser.username).toEqual(user.username);
        expect(loggedUser.password).toEqual(user.password);
        expect(loggedUser.logged).toEqual(true);
        expect(loggedUser.expDate).toEqual(expDate);

      })

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('loggin test', inject(function (LoggedUserFactory) {

      var user = { useranme: "hola", password: "chau" };

      var token = "token";

      $httpBackend.expectPOST(/.*\/api\/user/, user).respond(200, '', {authorization: token});

      $httpBackend.expectGET(/.*\/api\/user/).respond(200, user);

      LoggedUserFactory.login(user).then(function(loggedUser) {
        expect(loggedUser.username).toEqual(user.username);
        expect(loggedUser.password).toEqual(user.password);
        expect(loggedUser.logged).toEqual(true);
        expect(loggedUser.expDate).toEqual(expDate);
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('logout test', inject(function(LoggedUserFactory) {

      $httpBackend.expectDELETE(/.*\/api\/user\/refresh_token/).respond(200);

      LoggedUserFactory.logout();

      $httpBackend.flush();

      $scope.$digest();

      expect($location.path()).toEqual("/");
      expect(ReqFullResponse.defaultHeaders).toEqual({});

    }));


  });

});
