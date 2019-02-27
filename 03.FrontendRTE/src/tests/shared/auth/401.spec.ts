/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { inject, TestBed } from '@angular/core/testing';
import { BaseRequestOptions, Headers, Http, HttpModule, Response, ResponseOptions, XHRBackend } from '@angular/http';
import { MockBackend, MockConnection } from '@angular/http/testing';
import { MatDialogModule } from '@angular/material';
import { RouterTestingModule } from '@angular/router/testing';
import { JsonApiService } from '@jsonapiservice/json-api.service';
import { LoadingService } from '@shared/layout/loading/loading.service';
import { NotificationService } from '@shared/notification/notification.service';

const headers = new Headers({'content-type': 'application/json'});

describe('401', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                NotificationService,
                MockBackend,
                BaseRequestOptions,
                JsonApiService,
                LoadingService,
                {
                    deps: [MockBackend, BaseRequestOptions],
                    provide: Http,
                    useFactory: (mb: XHRBackend, options: BaseRequestOptions): Http => {
                        return new Http(mb, options);
                    },
                },
            ],
            imports: [
                HttpModule,
                RouterTestingModule,
              MatDialogModule,
            ],
        });
    });

    xit('get', inject([JsonApiService, MockBackend], async (api: JsonApiService, mockBackend: MockBackend) => {

        mockBackend.connections.subscribe((connection: MockConnection) => {
            if (connection.request.url === '200') {
                connection.mockRespond(new Response(new ResponseOptions({
                    status: 200,
                    body: {},
                    headers: headers,
                })))
            }
            if (connection.request.url === '401') {
                connection.mockRespond(new Response(new ResponseOptions({
                    status: 401,
                    body: {},
                    headers: headers,
                })))
            }

        });

        spyOn(api, 'show401Dialog');

        const result200 = await api.get('200').toPromise();

        expect(result200.status).toBe(200);

        expect(api.show401Dialog).not.toHaveBeenCalled();

        const result401 = await api.get('401').toPromise();

        expect(result401.status).toBe(401);

        expect(api.show401Dialog).toHaveBeenCalled();

    }));

});