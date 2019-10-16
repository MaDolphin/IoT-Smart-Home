/* (c) https://github.com/MontiCore/monticore */

import { getTestBed, inject } from '@angular/core/testing';
import { BaseRequestOptions, Headers, Http, RequestMethod, Response, ResponseOptions, XHRBackend, } from '@angular/http';
import { MockBackend, MockConnection } from '@angular/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { setTimeout } from 'timers';
import { AuthService } from '@shared/auth/auth.service';
import { Overlay, ScrollStrategyOptions } from '@angular/cdk/overlay';
import { ScrollDispatcher } from '@angular/cdk/scrolling';
import { loadArchitectureModuleWithCustomHttpServiceFactory } from '@testutils/utilities';
import { A, RemoveMockTocken } from '@testutils/mocking';

const headers = new Headers({'content-type': 'application/json'});

describe('AuthService', () => {
  let mockBackend: MockBackend;
  let authService: AuthService;
  beforeEach(() => {
    loadArchitectureModuleWithCustomHttpServiceFactory({
      deps: [MockBackend, BaseRequestOptions],
      provide: Http,
      useFactory: (backend: XHRBackend, options: BaseRequestOptions): Http => {
        return new Http(backend, options);
      },
    }, [Overlay,
      ScrollDispatcher,
      ScrollStrategyOptions,
      AuthService], [RouterTestingModule]);
    mockBackend = getTestBed().get(MockBackend);
    authService = getTestBed().get(AuthService);
  });

  afterAll(() => {
    RemoveMockTocken();
  });

  it('should create an injected instance test 1', inject([AuthService], (injectedService: AuthService) => {
    expect(injectedService).toBeDefined();
  }));

  it('should create an instance instance test 2', () => {
    expect(authService).toBeDefined();
  });

  it('should run login function', A(async () => {
    mockBackend.connections.subscribe((connection: MockConnection) => {
      // wrap in timeout to simulate server api call
      setTimeout(() => {
        // is it the correct REST type for an insert? (POST)
        expect(connection.request.method).toBe(RequestMethod.Post);
        // okey dokey,
        connection.mockRespond(new Response(new ResponseOptions({
          status: 201,
          body: {
            jwt: 'token',
            refreshToken: 'token',
            expirationDate: '2017-07-05T13:25:46.301Z',
          },
          headers: headers,
        })));
      }, 5);
    });

    let result: boolean = await authService.login('admin', 'pass', 'TestDB').toPromise();
    expect(result).toBe(true, 'successResult should be true');
    expect(localStorage.getItem('jwt')).toBe('token');
    expect(localStorage.getItem('refreshToken')).toBe('token');
    expect(localStorage.getItem('expirationDate')).toBe('2017-07-05T13:25:46.301Z');
  }));
});
