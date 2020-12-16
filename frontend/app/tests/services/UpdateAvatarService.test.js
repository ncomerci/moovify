define(['angular', 'angularMocks', 'frontend', 'services/UpdateAvatarService', 'restangular'], function(angular) {

  describe('UserService', function() {

    var $scope;
    var $q;
    var $httpBackend;
    var Restangular;
    var $provide;
    var UpdateAvatar;

    var user = {expDate: new Date(Date.now() + 100), avatar: ''};

    beforeEach(angular.mock.module('frontend'));

    beforeEach(module(function (_$provide_) {
      $provide = _$provide_;
    }));

    beforeEach(inject(function (_$rootScope_, _$q_, _$httpBackend_, _Restangular_, _UpdateAvatar_) {
      $scope = _$rootScope_;
      $q = _$q_;
      $httpBackend = _$httpBackend_;
      Restangular = _Restangular_;
      UpdateAvatar = _UpdateAvatar_;
    }));

    beforeEach(function() {
      var ReqFullResponse = Restangular.withConfig(function (RestangularConfigurer) {
        RestangularConfigurer.setFullResponse(true);
      });

      $provide.value('RestFulResponse', {withAuth: function() {return $q.resolve(ReqFullResponse)}});

      $provide.value('LoggedUserFactory', {
        getLoggedUser: function() { return user; }
      });
    });

    it('set file success test', function () {

      $httpBackend.expectPUT('http://localhost/api/user/avatar').respond(204);

      UpdateAvatar.uploadAvatar();

      $httpBackend.flush();
    });

  });


});
