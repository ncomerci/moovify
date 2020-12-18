var allTestFiles = []
var TEST_REGEXP = /(\.test)\.js$/i

// Get a list of all the test files to include
Object.keys(window.__karma__.files).forEach(function (file) {
  if (TEST_REGEXP.test(file)) {
    allTestFiles.push(file)
  }
})

require.config({
  // Karma serves files under /base, which is the basePath from your config file
  baseUrl: '/base/app/scripts',

  // dynamically load all test files
  deps: allTestFiles,

  // we have to kickoff jasmine, as it is asynchronous
  callback: window.__karma__.start,

  paths: {
    angular: '../../bower_components/angular/angular',
    'angular-route': '../../bower_components/angular-route/angular-route',
    'angular-translate': '../../bower_components/angular-translate/angular-translate',
    'angular-sanitize': '../../bower_components/angular-sanitize/angular-sanitize',
    'es5-shim': '../../bower_components/es5-shim/es5-shim',
    jquery: '../../bower_components/jquery/dist/jquery',
    json3: '../../bower_components/json3/lib/json3',
    lodash: '../../bower_components/lodash/dist/lodash',
    marked: '../../bower_components/marked/lib/marked',
    requirejs: '../../bower_components/requirejs/require',
    restangular: '../../bower_components/restangular/dist/restangular',
    'js-joda': '../../bower_components/js-joda/dist/js-joda',
    uikit: '../../static_dependencies/uikit/js/uikit',
    uikiticons: '../../static_dependencies/uikit/js/uikit-icons',
    'uikit-icons': '../../static_dependencies/uikit/js/uikit-icons',
    iconify: '../../static_dependencies/iconify/iconify',
    easymde: '../../static_dependencies/easymde/easyMde',
    purify: '../../bower_components/dompurify/dist/purify',
    dompurify: '../../bower_components/dompurify/dist/purify',
    'js-joda-timezone': '../../bower_components/js-joda-timezone/dist/js-joda-timezone',

    // Testing dependencies
    angularMocks: '../../node_modules/angular-mocks/angular-mocks',
    polyfillIncludesArray: '../../node_modules/phantomjs-polyfill-includes/includes-polyfill',
    polyfillObjectAssign: '../../node_modules/phantomjs-polyfill-object-assign/object-assign-polyfill',
    polyfillURLSearchParams: '../../node_modules/url-search-params-polyfill/index',
    polyfillFindArray: '../../node_modules/phantomjs-polyfill-find/find-polyfill'
  },

  shim: {
    angular: {
      deps: [
        'jquery'
      ],
      exports: 'angular'
    },
    'angular-route': {
      deps: [
        'angular'
      ]
    },
    uikiticons: {
      deps: [
        'uikit'
      ]
    },
    'angular-translate': {
      deps: [
        'angular'
      ]
    },
    'angular-sanitize': {
      deps: [
        'angular'
      ]
    },
    angularMocks: {
      deps: [
        'angular'
      ]
    }
  }


})
