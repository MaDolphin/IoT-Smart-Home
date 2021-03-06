/* (c) https://github.com/MontiCore/monticore */
// Karma configuration file, see link for more information
// https://karma-runner.github.io/0.13/config/configuration-file.html

module.exports = function (config) {
  config.set({
               basePath:                 '',
               frameworks:               ['jasmine', '@angular-devkit/build-angular', 'chai', 'sinon-chai'],
               plugins:                  [
                 require('karma-jasmine'),
                 require('karma-chai-plugins'),
                   require('karma-firefox-launcher'),
                 require('karma-chrome-launcher'),
                 require('karma-phantomjs-launcher'),
                 require('karma-jasmine-html-reporter'),
                 require('karma-coverage-istanbul-reporter'),
                 require('@angular-devkit/build-angular/plugins/karma')
               ],
               client:                   {
                 clearContext: false // leave Jasmine Spec Runner output visible in browser
               },
               coverageIstanbulReporter: {
                 dir:                   require('path').join(__dirname, '../coverage'),
                 reports:               ['html', 'lcovonly'],
                 fixWebpackSourcePaths: true
               },
               reporters:                ['progress', 'kjhtml'],
               port:                     9876,
               colors:                   true,
               logLevel:                 config.LOG_INFO,
               autoWatch:                true,
               browsers:                 ['Chrome'],
               browserNoActivityTimeout: 60000,
               singleRun:                false,
               customLaunchers:          {
                 ChromeHeadlessForRoot: {
                   base:  'ChromeHeadless',
                   flags: ['--no-sandbox']
                 }
               }
             });
};
