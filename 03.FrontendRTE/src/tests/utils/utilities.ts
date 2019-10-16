/* (c) https://github.com/MontiCore/monticore */

import { CommonModule } from '@angular/common';
import { async, TestBed } from '@angular/core/testing';
import { BaseRequestOptions, HttpModule } from '@angular/http';
import { MockBackend } from '@angular/http/testing';
import { MatDialogModule } from '@angular/material';
import { RouterTestingModule } from '@angular/router/testing';
import { TypedJSON } from '@upe/typedjson';
import { ArchitectureModule } from '@shared/architecture/architecture.module';
import { Token } from '@shared/auth/token';
import { LoadingService } from '@shared/layout/loading/loading.service';
import { SettingsService } from '@shared/layout';
import { NotificationService } from '@shared/notification/notification.service';
import { LOCALE_ID } from '@angular/core';

export const loadArchitectureModule = async(() => {

  const token = TypedJSON.parse(JSON.stringify({
                                                 jwt:            'token',
                                                 refreshToken:   'token',
                                                 expirationDate: '2050-01-01',
                                               }), Token);

  Token.SetToken(token);
  loadArchitectureModuleFactory();
});

export function loadArchitectureModuleFactory(
  providers: any[] = [],
  imports: any[] = [],
  declarations: any[] = [],
): Promise<any> {
  return loadArchitectureModuleWithCustomHttpServiceFactory(providers, imports, declarations);
}

export function loadArchitectureModuleWithCustomHttpServiceFactory(
  http: any,
  providers: any[] = [],
  imports: any[] = [],
  declarations: any[] = [],
): Promise<any> {
  return TestBed.configureTestingModule({
    declarations: declarations.concat([]),
    providers: [
      MockBackend,
      BaseRequestOptions,
      SettingsService,
      NotificationService,
      http,
      LoadingService,
      { provide: LOCALE_ID, useValue: 'de-DE' },
    ].concat(providers),
    imports: [
      ArchitectureModule.forRoot(),
      CommonModule,
      HttpModule,
      RouterTestingModule,
                    MatDialogModule,
    ].concat(imports),
  }).compileComponents();
}

export function serialize(obj: object, type: new () => any) {
  return TypedJSON.stringify(TypedJSON.parse(JSON.stringify(obj), type));
}

export function serializeTest(obj: object, type: new () => any) {
  return expect(JSON.parse(serialize(obj, type))).toEqual(obj);
}

export function deserializeTest(obj: object, type: new () => any, minObj: object = {}) {
  expect(TypedJSON.parse(JSON.stringify(obj), type)).toContain(obj);
  expect(TypedJSON.parse(JSON.stringify(minObj), type)).toEqual(minObj);
}

export const clone = (obj: any): any => JSON.parse(JSON.stringify(obj));
