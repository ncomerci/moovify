'use strict';
// source: https://gist.github.com/deiu/9335803
define(['frontend'], function(frontend) {

  frontend.service('LinkParserService', function() {

    this.parse = function (link) {

      var linkexp = /<[^>]*>\s*(\s*;\s*[^\(\)<>@,;:"\/\[\]\?={} \t]+=(([^\(\)<>@,;:"\/\[\]\?={} \t]+)|("[^"]*")))*(,|$)/g;
      var paramexp = /[^\(\)<>@,;:"\/\[\]\?={} \t]+=(([^\(\)<>@,;:"\/\[\]\?={} \t]+)|("[^"]*"))/g;

      var matches = link.match(linkexp);
      var rels = {};
      for (var i = 0; i < matches.length; i++) {
        var split = matches[i].split('>');
        var href = split[0].substring(1);
        var ps = split[1];
        var s = ps.match(paramexp);
        for (var j = 0; j < s.length; j++) {
          var p = s[j];
          var paramsplit = p.split('=');
          var name = paramsplit[0];
          var rel = paramsplit[1].replace(/["']/g, '');
          rels[rel] = href;
        }
      }

      rels = Object.keys(rels).map(function (rel) {

        var link = rels[rel];

        var ans = {rel: rel, url: link};

        var urlParams = new URLSearchParams(link.match(/\?.*$/)[0]);

        urlParams.forEach(function (value, key) {
          ans[key] = value
        });

        return ans;
      });

      var lastPage = parseInt(rels.find(function(entry) { return entry.rel === 'last' }).pageNumber);

      return lastPage;
    }

  });
});
