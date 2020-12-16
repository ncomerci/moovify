// Karma configuration
// Generated on Tue Dec 15 2020 09:40:54 GMT-0300 (Argentina Standard Time)

module.exports = function(config) {
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '../..',


    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine', 'requirejs'],


    // list of files / patterns to load in the browser
    files: [
      'app/tests/test-build.js',
      { pattern: 'app/scripts/*.js', included: false },
      { pattern: 'app/scripts/**/*.js', included: false },
      { pattern: 'bower_components/**/*.js', included: false },
      { pattern: 'static_dependencies/**/*.js', included: false },
      { pattern: 'node_modules/angular-mocks/angular-mocks.js', included: false },
      { pattern: 'node_modules/phantomjs-polyfill-includes/includes-polyfill.js', included: false },
      { pattern: 'app/tests/**/*.test.js', included: false }
    ],


    // list of files / patterns to exclude
    exclude: [
      'app/scripts/build.js'
    ],


    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
    },


    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress'],


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: false,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: ['PhantomJS'],


    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: true,

    // Concurrency level
    // how many browser should be started simultaneous
    concurrency: Infinity
  })
}
