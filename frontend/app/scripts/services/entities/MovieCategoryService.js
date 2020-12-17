'use strict';
define(['frontend'], function (frontend) {

  frontend.service('MovieCategoryService', function (){

    var movieCategories = {
      "action": "{{'ACTION' | translate }}",
      "adventure": "{{'ADVENTURE' | translate }}",
      "animation": "{{'ANIMATION' | translate }}",
      "comedy": "{{'COMEDY' | translate }}",
      "crime": "{{'CRIME' | translate }}",
      "documentary":"{{'DOCUMENTARY' | translate }}",
      "drama": "{{'DRAMA' | translate }}",
      "family": "{{'FAMILY' | translate }}",
      "fantasy": "{{'FANTASY' | translate }}",
      "history": "{{'HISTORY' | translate }}",
      "horror": "{{'HORROR' | translate }}",
      "music": "{{'MUSIC' | translate }}",
      "mystery": "{{'MYSTERY' | translate }}",
      "romance": "{{'ROMANCE' | translate }}",
      "scienceFiction": "{{'SCIENCE_FICTION' | translate }}",
      "tvMovie": "{{'TV_MOVIE' | translate }}",
      "thriller": "{{'THRILLER' | translate }}",
      "war": "{{'WAR' | translate }}",
      "western": "{{'WESTERN' | translate }}"
    }

    return {
      getMovieCategory: function (category) {
        return movieCategories[category];
      }
    }
  })
})
