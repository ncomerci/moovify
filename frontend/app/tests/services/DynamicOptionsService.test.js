define(['angular', 'angularMocks', 'frontend', 'services/DynamicOptionsService', 'restangular'], function(angular) {

  describe('DynamicOptionsService', function() {

    var $scope;
    var $q;
    var $httpBackend;
    var Restangular;
    var $provide;

    beforeEach(angular.mock.module('frontend'));

    beforeEach(module(function(_$provide_) {
      $provide = _$provide_;
    }))

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

      $provide.value('RestFulResponse', {noAuth: function() { return ReqFullResponse }});
    });

    it('get options test', inject(function(DynamicOptionsService){

      var options = [];

      for (var i = 0; i < 5; i++) {
        var option = {};
        option[i] = i;
        option.plain = function () {
        };
        options.push(option);
      }

      $httpBackend.expectGET(/.*\/api\/hola\/options/).respond(200, options)

      DynamicOptionsService.getOptions('/hola').then(function (returnedOptions) {
        expect(returnedOptions.map(function(c) { return c.originalElement })).toEqual(options);
      });

      $httpBackend.flush();

      $scope.$digest();
    }));
  });

});
