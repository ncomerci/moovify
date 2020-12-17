define(['angular', 'angularMocks', 'frontend', 'services/entities/PostInteractionService', 'restangular'], function(angular) {

  describe('PostInteractionService', function() {

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

      $provide.value('RestFulResponse', {withAuth: function() {return $q.resolve(ReqFullResponse)}});

      $provide.value('LoggedUserFactory', {
        getLoggedUser: function() { return null; }
      });
    });

    it('send vote success test', inject(function (PostInteractionService) {

      var value = 1;

      var post = {
        all: function (path) {
          return Restangular.all('/posts/1').all(path);
        },
        totalLikes: 0,
        userVote: -1
      }

      $httpBackend.expectPUT(/.*\/api\/posts\/1\/votes/, {value: value}).respond(200);

      PostInteractionService.sendVote(post, value).then(function (post) {
        expect(post.totalLikes).toEqual(2);
        expect(post.userVote).toEqual(1);
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('toggle bookmark false test', inject(function (PostInteractionService) {

      $httpBackend.expectDELETE(/.*\/api\/user\/bookmarked\/1/).respond(204);

      var post = {
        hasUserBookmarked: true,
        id: 1
      }

      PostInteractionService.toggleBookmark(post).then(function (post) {
        expect(post.hasUserBookmarked).toEqual(false);
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('toggle bookmark true test', inject(function (PostInteractionService) {

      $httpBackend.expectPUT(/.*\/api\/user\/bookmarked\/1/).respond(204);

      var post = {
        hasUserBookmarked: false,
        id: 1
      }

      PostInteractionService.toggleBookmark(post).then(function (post) {
        expect(post.hasUserBookmarked).toEqual(true);
      });

      $httpBackend.flush();

      $scope.$digest();
    }));
  });


});
