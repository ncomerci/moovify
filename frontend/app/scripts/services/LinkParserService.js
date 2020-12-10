'use strict';
// source: https://gist.github.com/deiu/9335803
define(['frontend'], function(frontend) {

  frontend.service('LinkParserService', function() {

    this.parse = function (link)  {

      let linkexp = /<[^>]*>\s*(\s*;\s*[^\(\)<>@,;:"\/\[\]\?={} \t]+=(([^\(\)<>@,;:"\/\[\]\?={} \t]+)|("[^"]*")))*(,|$)/g;
      let paramexp = /[^\(\)<>@,;:"\/\[\]\?={} \t]+=(([^\(\)<>@,;:"\/\[\]\?={} \t]+)|("[^"]*"))/g;

      let matches = link.match(linkexp);
      let rels = {};
      for (let i = 0; i < matches.length; i++) {
        let split = matches[i].split('>');
        let href = split[0].substring(1);
        let ps = split[1];
        let s = ps.match(paramexp);
        for (let j = 0; j < s.length; j++) {
          let p = s[j];
          let paramsplit = p.split('=');
          let name = paramsplit[0];
          let rel = paramsplit[1].replace(/["']/g, '');
          rels[rel] = href;
        }
      }

      rels = Object.entries(rels).map(([rel, link]) => {

        let ans = {rel: rel, url: link};

        let urlParams = new URLSearchParams(link.match(/\?.*$/)[0]);

        urlParams.forEach((value, key) => ans[key] = value);

        return ans;
      });

      let ans = {}

      rels.forEach(entry => ans[entry.rel] = Object.assign({}, entry));

      Object.keys(ans).forEach(entry => delete ans[entry].rel);

      ans.isInFirstPage = () => ans.prev === undefined;
      ans.isInLastPage = () => ans.next === undefined;
      ans.isOnlyPage = () => ans.isInFirstPage() && ans.isInLastPage();
      ans.currentPage = ans.isInFirstPage() ? 0 : parseInt(ans.prev.pageNumber) + 1;
      ans.pageSize = parseInt(ans.first.pageSize);

      return ans;
    }

  });
});
