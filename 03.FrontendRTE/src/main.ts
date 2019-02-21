import './polyfills.ts';

import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { enableProdMode } from '@angular/core';
import { environment } from './environments/environment';
import { AppModule } from './app/app.module';
import { Logger, LogType } from '@upe/logger';
import { TYPED_JSON_GLOBAL_SETTINGS } from '@upe/typedjson';

if (environment.production || location.hostname !== 'localhost') {
  enableProdMode();
  Logger.MUTED = false;
} else {
}

Logger.MuteType(LogType.DEBUG);

TYPED_JSON_GLOBAL_SETTINGS.required = false;

platformBrowserDynamic().bootstrapModule(AppModule).catch(err => new Logger({name: 'Main'}).error('bootstrap', err));
