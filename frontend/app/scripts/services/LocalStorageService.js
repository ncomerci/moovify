'use strict';
define(['frontend'], function(frontend) {

  frontend.service('LocalStorageService', function ($window) {

    this.save = function(key, value){
      $window.localStorage.setItem(key, JSON.stringify(value));
    }

    this.get = function(key){
      return JSON.parse($window.localStorage.getItem(key));
    }

    this.delete = function(key){
      $window.localStorage.removeItem(key);
    }

  });
});
