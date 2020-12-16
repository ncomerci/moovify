define(['angular', 'angularMocks', 'frontend', 'services/utilities/RestFulResponseFactory'], function(angular) {

  describe('RestFulResponse', function() {

    var RestFulResponse;
    var $scope;
    var $q;
    var $httpBackend;

    beforeEach(angular.mock.module('frontend'));

    beforeEach(inject(function (_RestFulResponse_, _$rootScope_, _$q_, _$httpBackend_) {
      RestFulResponse = _RestFulResponse_;
      $scope = _$rootScope_;
      $q = _$q_;
      $httpBackend = _$httpBackend_;
    }));

    it('set token test', function () {

      var jwt = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VlZXIyIiwiZW5hYmxlZCI" +
        "6InRydWUiLCJyb2xlcyI6Ik5PVF9WQUxJREFURUQiLCJpYXQiOjE2MDgwNjg5MTMsI" +
        "mV4cCI6MTYwODA2OTgxM30.0DOtmWHL75X_KglWS-pSgAKtuxTjABDsPUH7ScIhEU9K0" +
        "ZsfQJLsAEiqFFL4qrsdjcVDM6wo1SjO6yZ0yW-Sig";

      var expirationDate = new Date(1608069813 * 1000);

      expect(RestFulResponse.setToken(jwt)).toEqual(expirationDate);
    });

    it('with auth need to refresh test', function () {

      var loggedUser = { expDate: new Date(Date.now() - 100), id: 3 };

      var jwt = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VlZXIyIiwiZW5hYmxlZCI" +
        "6InRydWUiLCJyb2xlcyI6Ik5PVF9WQUxJREFURUQiLCJpYXQiOjE2MDgwNjg5MTMsI" +
        "mV4cCI6MTYwODA2OTgxM30.0DOtmWHL75X_KglWS-pSgAKtuxTjABDsPUH7ScIhEU9K0" +
        "ZsfQJLsAEiqFFL4qrsdjcVDM6wo1SjO6yZ0yW-Sig";

      var authorizationHeader = 'Bearer ' + jwt;

      var expirationDate = new Date(1608069813 * 1000)

      $httpBackend.expectPOST('http://localhost/api/user/refresh_token').respond(200, '', {Authorization: authorizationHeader});

      RestFulResponse.withAuth(loggedUser).then(function (restfull) {
        expect(loggedUser.expDate).toEqual(expirationDate);
        expect(restfull.defaultHeaders.authorization).toEqual(authorizationHeader);
      });

      $httpBackend.flush();

      $scope.$digest();
    });

    it('with auth no need to refresh test', function () {

      var expDate = new Date(Date.now() + 100);

      var loggedUser = { expDate: expDate, id: 3 };

      RestFulResponse.withAuth(loggedUser).then(function (restfull) {
        expect(loggedUser.expDate).toEqual(expDate);
        expect(restfull.defaultHeaders.authorization).toBeUndefined();
      });

      $scope.$digest();
    });
  });

});
