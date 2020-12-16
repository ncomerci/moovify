define(['angular', 'angularMocks', 'frontend', 'services/CommentInteractionService', 'restangular'], function(angular) {

  describe('CommentInteractionService', function() {

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

    it('send vote success test', inject(function (CommentInteractionService) {

      var value = 1;

      var comment = {
        id: 1,
        totalVotes: 0,
        userVote: -1
      }

      $httpBackend.expectPUT(/.*\/api\/comments\/1\/votes/, {value: value}).respond(200);

      CommentInteractionService.sendVote(comment, value).then(function (comment) {
        expect(comment.totalVotes).toEqual(2);
        expect(comment.userVote).toEqual(1);
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('send comment reply success test', inject(function (CommentInteractionService) {

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

      CommentInteractionService.sendCommentReply(comment, body).then(function (data) {
        expect(data.data).toEqual(1);
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('send post reply success test', inject(function (CommentInteractionService) {

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

      CommentInteractionService.sendPostReply(post, body).then(function (data) {
        expect(data.data).toEqual(1);
      });

      $httpBackend.flush();

      $scope.$digest();
    }));


  });


});
