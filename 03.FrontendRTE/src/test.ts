import 'zone.js/dist/zone-testing';
import { getTestBed } from '@angular/core/testing';
import { BrowserDynamicTestingModule, platformBrowserDynamicTesting } from '@angular/platform-browser-dynamic/testing';
import { Logger } from '@upe/logger';
import localeDe from '@angular/common/locales/de';
import localeDeExtra from '@angular/common/locales/extra/de';
import { registerLocaleData } from '@angular/common';
import { TYPED_JSON_GLOBAL_SETTINGS } from '@upe/typedjson';

declare const require: any;

Logger.DISABLED = true;
TYPED_JSON_GLOBAL_SETTINGS.required = false;

registerLocaleData(localeDe, 'de-DE', localeDeExtra);
registerLocaleData(localeDe, 'de', localeDeExtra);

// First, initialize the Angular testing environment.
getTestBed().initTestEnvironment(
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting()
);
// Then we find all the tests.
const context = require.context('./tests', true, /\.spec\.ts$/);
// And load the modules.
context.keys().map(context);