define(['angular', 'angularMocks', 'frontend', 'services/entities/UserService', 'restangular',
  'polyfillURLSearchParams', 'polyfillIncludesArray'], function(angular) {

  describe('UserService', function() {

    var $scope;
    var $q;
    var $httpBackend;
    var Restangular;
    var $location;
    var $provide;

    beforeEach(angular.mock.module('frontend'));

    beforeEach(module(function (_$provide_) {
      $provide = _$provide_;
    }));

    beforeEach(inject(function (_$rootScope_, _$q_, _$httpBackend_, _$location_, _Restangular_) {
      $scope = _$rootScope_;
      $q = _$q_;
      $httpBackend = _$httpBackend_;
      Restangular = _Restangular_;
      $location = _$location_;
    }));

    beforeEach(function() {
      var ReqFullResponse = Restangular.withConfig(function (RestangularConfigurer) {
        RestangularConfigurer.setFullResponse(true);
      });

      $provide.value('RestFulResponse', {
        withAuthIfPossible: function() {return $q.resolve(ReqFullResponse)},
        withAuth: function() {return $q.resolve(ReqFullResponse)},
        noAuth: function() { return ReqFullResponse }
      });

      $provide.value('LoggedUserFactory', {
        getLoggedUser: function() { return {avatar: '', logout: function() {return $q.resolve()}}},
        logout: function() {return $q.resolve()}
      });

      $provide.value('LinkParserService', {parse: function() {return 10}});
    });

    it('search users success test', inject(function (UserService) {

      var query = "queryParam";
      var role = "roleParam";
      var enabled = true;
      var orderBy = "orderByParam";
      var pageSize = 10;
      var pageNumber = 15;

      var users = [{id: 1}, {id: 2}, {id: 3}, {id: 4}];

      $httpBackend.expectGET(/.*\/api\/users\?.*/).respond(function(method, url) {

        var searchParams = new URLSearchParams(url.substring(url.indexOf('?'), url.length));

        expect(searchParams.get('query')).toEqual(query);
        expect(searchParams.get('role')).toEqual(role);
        expect(searchParams.get('enabled')).toEqual(enabled.toString());
        expect(searchParams.get('orderBy')).toEqual(orderBy);
        expect(searchParams.get('pageSize')).toEqual(pageSize.toString());
        expect(searchParams.get('pageNumber')).toEqual(pageNumber.toString());

        return [200, [{id: 1}, {id: 2}, {id: 3}, {id: 4}]];
      });

      UserService.searchUsers(query, role, enabled, orderBy, pageSize, pageNumber).then(function (response) {
        expect(response.collection.map(function(u) {return u.originalElement })).toEqual(users);
        expect(response.paginationParams).toEqual({pageSize: pageSize, currentPage: pageNumber, lastPage: 0});
        expect(response.queryParams).toEqual(
          {query: query, role: role, enabled: enabled, orderBy: orderBy, pageSize: pageSize, pageNumber: pageNumber })
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('fetch users success test', inject(function (UserService) {

      var enabled = true;
      var orderBy = "orderByParam";
      var pageSize = 10;
      var pageNumber = 15;

      var users = [{id: 1}, {id: 2}, {id: 3}, {id: 4}];

      $httpBackend.expectGET(/.*\/api\/users\?.*/).respond(function(method, url) {

        var searchParams = new URLSearchParams(url.substring(url.indexOf('?'), url.length));

        expect(searchParams.get('enabled')).toEqual(enabled.toString());
        expect(searchParams.get('orderBy')).toEqual(orderBy);
        expect(searchParams.get('pageSize')).toEqual(pageSize.toString());
        expect(searchParams.get('pageNumber')).toEqual(pageNumber.toString());

        return [200, [{id: 1}, {id: 2}, {id: 3}, {id: 4}]];
      });

      UserService.fetchUsers('/users', enabled, orderBy, pageSize, pageNumber).then(function (response) {
        expect(response.collection.map(function(u) {return u.originalElement })).toEqual(users);
        expect(response.paginationParams).toEqual({pageSize: pageSize, currentPage: pageNumber, lastPage: 0});
        expect(response.queryParams).toEqual(
          {enabled: enabled, orderBy: orderBy, pageSize: pageSize, pageNumber: pageNumber })
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('has role success test', inject(function (UserService) {

      var user = {roles: ['ADMIN', 'USER']};

      expect(UserService.userHasRole(user, 'ADMIN')).toEqual(true);
    }));

    it('has role failure test', inject(function (UserService) {

      var user = {roles: ['ADMIN', 'USER']};

      expect(UserService.userHasRole(user, 'INVALID')).toEqual(false);
    }));

    it('has role invalid user test', inject(function (UserService) {

      expect(UserService.userHasRole(null, 'ADMIN')).toEqual(false);
    }));

    it('get user test', inject(function (UserService) {

      var user = {id: 5};

      $httpBackend.expectGET(/.*\/api\/users\/5.*/).respond(200, user);

      UserService.getUser(user.id).then(function(returnedUser) {
        expect(returnedUser.originalElement).toEqual(user);
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('is logged user following test', inject(function (UserService) {

      var user = {id: 5};

      $httpBackend.expectGET(/.*\/api\/user\/following\/5.*/).respond(200, {response: true});

      UserService.doLoggedUserFollow(user, user.id).then(function(isFollowing) {
        expect(isFollowing).toEqual(true);
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    // TODO: Make it better
    it('upload avatar test', inject(function (UserService) {

      $httpBackend.expectPUT(/.*\/api\/user\/avatar/).respond(204);

      UserService.avatar.upload();

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('resend confirm email test', inject(function (UserService) {

      var user = {id: 5};

      $httpBackend.expectPOST(/.*\/api\/user\/email_confirmation/).respond(204);

      UserService.resendConfirmEmail(user);

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('follow user test', inject(function (UserService) {

      var user = {id: 5, followerCount: 0};

      var loggedUser = {};

      $httpBackend.expectPUT(/.*\/api\/user\/following\/5/).respond(204);

      UserService.followUser(loggedUser, user).then(function() {
        expect(user.followerCount).toEqual(1);
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('unfollow user test', inject(function (UserService) {

      var user = {id: 5, followerCount: 0};

      var loggedUser = {};

      $httpBackend.expectDELETE(/.*\/api\/user\/following\/5/).respond(204);

      UserService.unfollowUser(loggedUser, user).then(function() {
        expect(user.followerCount).toEqual(-1);
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('promote user test', inject(function (UserService) {

      var user = {id: 5, roles: ['USER']};

      var loggedUser = {};

      $httpBackend.expectPUT(/.*\/api\/users\/5\/privilege/).respond(204);

      UserService.promoteUser(loggedUser, user).then(function() {
        expect(user.roles).toContain('ADMIN');
        expect(user.roles).toContain('USER');
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('delete user test', inject(function (UserService) {

      var user = {id: 5, roles: ['USER']};

      var loggedUser = {};

      $httpBackend.expectDELETE(/.*\/api\/users\/5\/enabled/).respond(204);

      UserService.deleteUser(loggedUser, user);

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('update user test', inject(function (UserService) {

      var info = {id: 5};

      var loggedUser = {};

      $httpBackend.expectPUT(/.*\/api\/user/, info).respond(204);

      UserService.updateInfo(loggedUser, info);

      $httpBackend.flush();

      $scope.$digest();
    }));

    // TODO: Refactor
    it('update password test', inject(function (UserService) {

      var password = "hola";

      var loggedUser = {};

      $httpBackend.expectPUT(/.*\/api\/user/, password).respond(204);

      UserService.updatePassword(loggedUser, password).then(function () {
        expect($location.path()).toEqual('/login');
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('send email confirmation token test', inject(function (UserService) {

      var token = "hola";

      var loggedUser = {roles: ["INVALID", "NOT_VALIDATED", "ANOTHER"]};

      $httpBackend.expectPUT(/.*\/api\/user\/email_confirmation/, token).respond(204);

      UserService.sendConfirmToken(loggedUser, token).then(function () {
        expect(loggedUser.roles).toEqual(["INVALID", "USER", "ANOTHER"]);
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('recover user test', inject(function (UserService) {

      var user = {id: 4};

      $httpBackend.expectPUT(/.*\/api\/users\/4\/enabled/).respond(204);

      UserService.recoverUser(user).then(function (returnedUser) {
        expect(returnedUser).toEqual(user);
      });

      $httpBackend.flush();
    }));

    it('create user test', inject(function (UserService) {

      var user = {id: 4};

      $httpBackend.expectPOST(/.*\/api\/users/, user).respond(201);

      UserService.signUp(user);

      $httpBackend.flush();
    }));

    it('reset password test', inject(function (UserService) {

      var pass = {id: 4};

      $httpBackend.expectPUT(/.*\/api\/user\/password_reset/, pass).respond(204);

      UserService.resetPassword(pass);

      $httpBackend.flush();
    }));

    it('send password reset token test', inject(function (UserService) {

      var email = "hola";

      $httpBackend.expectPOST(/.*\/api\/user\/password_reset/, email).respond(204);

      UserService.sendToken(email);

      $httpBackend.flush();
    }));

  });
});
