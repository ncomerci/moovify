// define(['angular', 'angularMocks', 'frontend', 'services/LoginService'], function(angular) {
//
//   describe('LoginService', function() {
//
//     var LoggedUserFactory;
//     var $scope;
//     var $q;
//     var $httpBackend;
//
//     beforeEach(angular.mock.module('frontend'));
//
//     beforeEach(inject(function (_LoggedUserFactory_, _$rootScope_, _$q_, _$httpBackend_) {
//       LoggedUserFactory = _LoggedUserFactory_;
//       $scope = _$rootScope_;
//       $q = _$q_;
//       $httpBackend = _$httpBackend_;
//     }));
//
//     it('loggin test', function () {
//
//       var user = { useranme: "hola", password: "chau" };
//
//       var jwt = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VlZXIyIiwiZW5hYmxlZCI" +
//         "6InRydWUiLCJyb2xlcyI6Ik5PVF9WQUxJREFURUQiLCJpYXQiOjE2MDgwNjg5MTMsI" +
//         "mV4cCI6MTYwODA2OTgxM30.0DOtmWHL75X_KglWS-pSgAKtuxTjABDsPUH7ScIhEU9K0" +
//         "ZsfQJLsAEiqFFL4qrsdjcVDM6wo1SjO6yZ0yW-Sig";
//
//       $httpBackend.expectPOST('http://localhost/api/user').respond(200, {authorization: 'Bearer ' + jwt});
//
//       LoggedUserFactory.login(user);
//
//       $httpBackend.flush();
//     });
//
//
//   });
//
// });
