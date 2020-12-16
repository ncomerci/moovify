define(['angular', 'angularMocks', 'frontend', 'services/utilities/LinkParserService', 'polyfillURLSearchParams',
  'polyfillFindArray'], function(angular) {

  describe('LinkParserService', function() {

    var LinkParserService;

    beforeEach(angular.mock.module('frontend'));

    beforeEach(inject(function (_LinkParserService_) {
      LinkParserService = _LinkParserService_;
    }));

    it('parse get last page success test', function() {

      var last = 25;

      var link = '<http://localhost:8080/api/comments/4/children?pageSize=10&orderBy=newest&pageNumber=0>; rel="first", ' +
        '<http://localhost:8080/api/comments/4/children?pageSize=10&orderBy=newest&pageNumber=' + last + '>; rel="last"';

      expect(LinkParserService.parse(link)).toEqual(last);
    });

    it('get link maps success test', function() {

      var link = '<http://localhost:8080/api/comments/4/children?pageSize=10&orderBy=newest&pageNumber=0>; rel="first", ' +
        '<http://localhost:8080/api/comments/4/children?pageSize=10&orderBy=newest&pageNumber=25>; rel="last"';

      var map = LinkParserService.getLinksMaps(link);

      expect(map.first.pageNumber).toEqual('0');
      expect(map.first.pageSize).toEqual('10');
      expect(map.first.orderBy).toEqual('newest');
      expect(map.last.pageNumber).toEqual('25');
      expect(map.last.pageSize).toEqual('10');
    });

  });


});
