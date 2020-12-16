define(['angular', 'angularMocks', 'frontend', 'services/DisplayService', 'restangular'], function(angular) {

  describe('DisplayService', function() {

    beforeEach(angular.mock.module('frontend'));

    it('get year test', inject(function(DisplayService){

      var year = "12345";
      var garbage = "igjdofgjfd";

      expect(DisplayService.getYear(year + '-' + garbage)).toEqual(year);
    }));

    it('get body formatted <= 40 test', inject(function(DisplayService){

      var body = "holas";

      expect(DisplayService.getBodyFormatted(body)).toEqual('"' + body + '"');
    }));

    it('get body formatted > 40 test', inject(function(DisplayService){

      var tenChars = "0123456789";
      body = tenChars + tenChars + tenChars + tenChars + tenChars;

      expect(DisplayService.getBodyFormatted(body))
        .toEqual('"' + tenChars + tenChars + tenChars + tenChars + '"' + ' [...]');
    }));
  });

});
