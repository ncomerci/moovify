define(['angular', 'angularMocks', 'frontend', 'services/UserService', 'polyfillIncludesArray'], function(angular) {

  describe('UserService', function() {

    var UserService;

    beforeEach(angular.mock.module('frontend'));

    beforeEach(inject(function (_UserService_) {
      UserService = _UserService_;
    }));

    it('has role success test', function() {

      var user = {roles: ['ADMIN', 'USER']};

      expect(UserService.userHasRole(user, 'ADMIN')).toEqual(true);
    });

    it('has role failure test', function() {

      var user = {roles: ['ADMIN', 'USER']};

      expect(UserService.userHasRole(user, 'INVALID')).toEqual(false);
    });

    it('has role invalid user test', function() {

      expect(UserService.userHasRole(null, 'ADMIN')).toEqual(false);
    });

  });


});
