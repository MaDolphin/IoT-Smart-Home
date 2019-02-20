// Protractor configuration file, see link for more information
// https://github.com/angular/protractor/blob/master/docs/referenceConf.js

/*global jasmine */
var SpecReporter = require('jasmine-spec-reporter').SpecReporter;
var DC = require('@upe/logger/dist/decorators').DECORATOR_CONFIG;
DC.disabled = true;
exports.config = {
  params: {
    hasRunResetDB: false
  },
  allScriptsTimeout: 260000,
  specs: [
      './specs/**/*.e2e-spec.ts',
  ],
  capabilities: {
    'browserName': 'chrome',
    'chromeOptions': {
      // How to set browser language (menus & so on)
      args: ['lang=de-DE'], // '--allow-insecure-localhost', '--headless', '--disable-gpu', '--window-size=800x600',
      // How to set Accept-Language header
      prefs: {
        intl: {accept_languages: "de-DE"},
      }
    }
  },
  directConnect: true,
  baseUrl: 'http://localhost:4200/',
  framework: 'jasmine',
  jasmineNodeOpts: {
    showColors: true,
    defaultTimeoutInterval: 999999,
    print: function () {
    }
  },
  useAllAngular2AppRoots: true,
  beforeLaunch: function () {
    require('ts-node').register({
      project: './e2e/tsconfig.e2e.json'
    });
    require('tsconfig-paths/register');
    require('reflect-metadata');
  },
  onPrepare: function () {

    setTimeout(function () {
      var origFn = browser.driver.controlFlow().execute;

      browser.driver.controlFlow().execute = function () {
        var args = arguments;

        // time to wait in ms
        origFn.call(browser.driver.controlFlow(), function () {
          return protractor.promise.delayed(100);
        });

        return origFn.apply(browser.driver.controlFlow(), args);
      };

      browser.driver.executeScript(function () {
        return {
          width: 1920,
          height: 1200
        };
      }).then(function (result) {
        browser.driver.manage().window().setSize(result.width, result.height);
      });
    });
    jasmine.getEnv().addReporter(new SpecReporter({spec: {displayStacktrace: true}}));
    browser.params.hasRunResetDB = false;
  }
};
