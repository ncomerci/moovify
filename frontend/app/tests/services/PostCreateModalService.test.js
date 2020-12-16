define(['angular', 'angularMocks', 'frontend', 'services/PostCreateModalService'], function(angular) {

  describe('PostCreateModalService', function() {

    var PostCreateModalService;

    beforeEach(angular.mock.module('frontend'));

    beforeEach(inject(function (_PostCreateModalService_) {
      PostCreateModalService = _PostCreateModalService_;
    }));

    it('add movie success test', function() {

      var movieList = {
        movie1: 1,
        movie2: 2,
        movie3: 3
      };

      var movieMap = {};

      PostCreateModalService.addMovie('movie2', movieMap, movieList);

      expect(movieList.movie2).toBeUndefined();

      expect(movieMap.movie2).toEqual(2);
    });

    it('add movie too many test', function() {

      var movieList = {
        movie1: 1,
        movie2: 2,
        movie3: 3
      };

      var movieMap = {};

      for(var i = 0; i < 21; i++)
        movieMap[i] = i;

      PostCreateModalService.addMovie('movie2', movieMap, movieList);

      expect(movieMap.movie2).toBeUndefined();

      expect(movieList.movie2).toEqual(2);
    });

    it('add movie already existed test', function() {

      var movieList = {
        movie1: 1,
        movie2: 2,
        movie3: 3
      };

      var movieMap = {
        movie1: 1,
        movie2: 3,
        movie3: 2
      };

      PostCreateModalService.addMovie('movie2', movieMap, movieList);

      expect(movieMap.movie2).toEqual(3);

      expect(movieList.movie2).toEqual(2);
    });

    it('add tag successful test', function() {

      var tags = {
        tag1: "1",
        tag2: "2",
        tag3: "3"
      };

      PostCreateModalService.addTag('tag7', tags);

      expect(tags.tag7).toEqual('');
    });

    it('add tag too many test', function() {

      var tags = {
        tag1: "1",
        tag2: "2",
        tag3: "3",
        tag4: "4",
        tag5: "5"
      };

      PostCreateModalService.addTag('tag7', tags);

      expect(tags.tag7).toBeUndefined();
    });

    it('add tag already existed test', function() {

      var tags = {
        tag1: "1",
        tag2: "2",
        tag3: "3",
        tag4: "4",
        tag5: "5"
      };

      PostCreateModalService.addTag('tag3', tags);

      expect(tags.tag3).toEqual('3');
    });

  });


});
