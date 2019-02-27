/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { getTestBed, inject } from '@angular/core/testing';

import { BaseRequestOptions, Headers, Http, Response, ResponseOptions, XHRBackend } from '@angular/http';
import { MockBackend, MockConnection } from '@angular/http/testing';
import { JsonApiService } from '@jsonapiservice/json-api.service';
import { loadArchitectureModuleWithCustomHttpServiceFactory } from '@testutils/utilities';
import { A, MockToken, RemoveMockTocken } from '@testutils/mocking';

const headers = new Headers({'content-type': 'application/json'});

describe('JsonApiService', () => {
  let mockBackend: MockBackend;
  let apiService: JsonApiService;
  beforeEach(() => {
    MockToken();

    loadArchitectureModuleWithCustomHttpServiceFactory({
      deps: [MockBackend, BaseRequestOptions],
      provide: Http,
      useFactory: (backend: XHRBackend, options: BaseRequestOptions): Http => {
        return new Http(backend, options);
      },
    });

    mockBackend = getTestBed().get(MockBackend);
    apiService  = getTestBed().get(JsonApiService);
  });

  afterAll(() => {
    RemoveMockTocken();
  });

  it('should create an injected instance', inject([JsonApiService], (injectedService: JsonApiService) => {
    expect(injectedService).toBeDefined();
  }));

  it('should create an instance instance 2', () => {
    expect(apiService).toBeDefined();
  });
  it('get()', A(async () => {
    mockBackend.connections.subscribe((connection: MockConnection) => {
      // wrap in timeout to simulate server api call
      setTimeout(() => {
        // make sure the URL is correct
        expect(connection.request.url).toMatch(/\/testPath\/3/);
        connection.mockRespond(
          new Response(
            new ResponseOptions({
                                  headers: headers,
                                  body:    {
                                    id:              3,
                                    contentRendered: '<p><b>Demo</b></p>',
                                    contentMarkdown: '*Demo*',
                                  },
                                })),
        );
      }, 5);
    });

    let fakeResult = await apiService.get('/testPath/3', true, true, headers, true).toPromise();
    let jsonData   = fakeResult.json;
    expect(jsonData.id).toBe(3);
    expect(jsonData.contentMarkdown).toBe('*Demo*');
    expect(jsonData.contentRendered).toBe('<p><b>Demo</b></p>');
  }));

  it('post()', A(async () => {
      mockBackend.connections.subscribe((connection: MockConnection) => {
          // make sure the URL is correct
          expect(connection.request.url).toMatch(/\/testPath\/3/);
          expect(connection.request.method).toBe(1); // 1:POST

          let jsonData = JSON.parse(connection.request.getBody());

          expect(jsonData.id).toBe(3);
          expect(jsonData.contentMarkdown).toBe('*Demo*');
          expect(jsonData.contentRendered).toBe('<p><b>Demo</b></p>');


      });

      let body = {
          id:              3,
          contentRendered: '<p><b>Demo</b></p>',
          contentMarkdown: '*Demo*',
      };

      apiService.post('/testPath/3', body, headers, true);
  }));

    it('put()', () => {

        mockBackend.connections.subscribe((connection: MockConnection) => {
            // make sure the URL is correct
            expect(connection.request.url).toMatch(/\/testPath\/3/);
            expect(connection.request.method).toBe(2); // 2:PUT

            let jsonData = JSON.parse(connection.request.getBody());

            expect(jsonData.id).toBe(3);
            expect(jsonData.contentMarkdown).toBe('*Demo*');
            expect(jsonData.contentRendered).toBe('<p><b>Demo</b></p>');


        });

        let body = {
            id:              3,
            contentRendered: '<p><b>Demo</b></p>',
            contentMarkdown: '*Demo*',
        };

        apiService.put('/testPath/3', body, headers, true);
    });

    it('delete()', () => {
        mockBackend.connections.subscribe((connection: MockConnection) => {
            // make sure the URL is correct
            expect(connection.request.url).toMatch(/\/testPath\/3/);
            expect(connection.request.method).toBe(3); // 3:DELETE
        });

        apiService.delete('/testPath/3', headers, true);
    });

});
