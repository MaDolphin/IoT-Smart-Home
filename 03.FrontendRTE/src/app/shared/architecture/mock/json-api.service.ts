/* (c) https://github.com/MontiCore/monticore */

import { Injectable } from '@angular/core';
import { Headers, Http, Request, RequestMethod, RequestOptions, Response, ResponseOptions } from '@angular/http';
import { Router } from '@angular/router';
import { Logger } from '@upe/logger';
import { TypedJSON } from '@upe/typedjson';
import { deprecated } from 'deprecated-decorator';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/delay';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/timeoutWith';
import { Observable } from 'rxjs/Rx';
import { Token } from '../../auth/token';
import { LoadingService } from '../../layout/loading/loading.service';
import { NotificationService } from '../../notification/notification.service';
import { MontiGemError } from '../../utils/montigem.error';
import { ApiService } from '../services/api.service';
import { StringWrapper } from '../services/primitive-wrappers/string-wrapper';
import { DialogCallbackTwo } from '../../utils/dialog/dialog.callback';

export type JsonResponse = { json: any | null, headers: Headers | null, status: number, cached: boolean };

@Injectable()
export class JsonApiService {

  logger: Logger;

  public static get HeaderPlain(): Headers {
    return new Headers({
      'Content-Type': 'text/plain',
      Accept: 'application/json'
    });
  }

  public static get HeaderForm(): Headers {
    return new Headers({
      'Content-Type': 'application/x-www-form-urlencoded',
      Accept: 'application/json'
    });
  }

  public static get HeaderJson(): Headers {
    return new Headers({
      'Content-Type': 'application/json',
      Accept: 'application/json'
    });
  }

  public static HeaderJSONWithJWT(auth: string): Headers {
    return new Headers({
      'Content-Type': 'application/json',
      Accept: 'application/json',
      'X-AUTH': auth
    });
  }

  public static get HeaderPDF(): Headers {
    return new Headers({
      'Content-Type': 'application/pdf',
      Accept: 'application/pdf'
    });
  }

  public static get HeaderOctetStream(): Headers {
    return new Headers({
      'Content-Type': 'application/octet-stream',
      Accept: 'application/json'
    });
  }

  public static get HeaderCSV(): Headers {
    return new Headers({
      'Content-Type': 'text/comma-separated-values; charset=UTF-8',
      Accept: 'application/json'
    });
  }

  /**
   * Deserialize json to MontiGemError
   * @param json {any}
   * @return {MontiGemError}
   */
  public static deserializeError(json: any, logger: Logger): MontiGemError {
    let obj = MontiGemError.create('MAF0xFFFF', 'Error', 'could not deserialize the error message');

    if (json !== undefined) {
      try {
        obj = MontiGemError.fromJson(ApiService.deserialize(json, MontiGemError));
      } catch (e) {
        logger.error('MAF0x008C: deserialize', {error: e, json: json, type: MontiGemError});
      }
    }

    return obj;
  }

  private _observers: Map<string, Observable<JsonResponse>>;

  constructor(private http: Http,
              private loading: LoadingService,
              private noti: NotificationService,
              private router: Router) {
    this._observers = new Map<string, Observable<JsonResponse>>();
    this.logger = new Logger({name: 'JsonApiService', flags: ['service', 'http']});
  }

  /**
   * Add base url and config base url to url
   * @param {string} url
   * @returns {string}
   */
  public static buildUrl(url: string): string {
    return JsonApiService.getBaseUrl() + 'montigem-be/api' + url;
  }

  public static getBaseUrl(): string {
    return location.protocol + '//' + location.hostname + (
        location.port ? ':' + location.port : ''
    ) + '/';
  }

  /**
   * Create a get request. If a request with the same url is in progress
   * the methode return the a shred Observale. If the request is already success
   * the methode return an stub Observable with the cached data.
   *
   * The headers param is optional. If not set the content type is set to
   * application/json
   *
   * The force param is optional. If set to true the cached data will not be
   * used, but if a same request is in progress this request will be used
   * @param {string} url
   * @param {boolean} force
   * @param {boolean} loadingIndicator
   * @param {Headers} headers
   * @returns {Observable<JsonResponse>}
   */
  public get(url: string, force?: boolean, loadingIndicator?: boolean, headers?: Headers, autoCatch: boolean = true): Observable<JsonResponse> {
    if (this._observers.has(url)) {
      // if `this.observable` is set then the request is in progress
      // return the `Observable` for the ongoing request
      return (this._observers.get(url) as any).map((response: JsonResponse) => {
        response.cached = true;
        return response;
      });
    } else {
      if (loadingIndicator) {
        this.loading.start();
      }
      // create the request, store the `Observable` for subsequent subscribers
      this._observers.set(url, this.request(new RequestOptions({
        url: JsonApiService.buildUrl(url),
        headers: headers,
        method: RequestMethod.Get
      }), autoCatch).map((response: JsonResponse) => {

        // when the cached data is available we don't need the `Observable` reference anymore
        this._observers.delete(url);

        if (loadingIndicator) {
          this.loading.stop();
        }

        response.cached = false;

        // make it shared so more than one subscriber can get the result
        return response;
      }).catch((error: Response | any) => {
        // when the cached data is available we don't need the `Observable` reference anymore
        this._observers.delete(url);
        return Observable.throw(error);
      }).share());
      return this._observers.get(url) as any;
    }
  }

  /**
   * Create a post request
   *
   * The headers param is optional. If not set the content type is set to
   * application/json
   *
   * @param {string} url
   * @param {any} body
   * @param {Headers} headers
   * @returns {Observable<JsonResponse>}
   */
  public post(url: string, body: any, headers?: Headers, autoCatch: boolean = true): Observable<JsonResponse> {
    return this.request(new RequestOptions({
      url: JsonApiService.buildUrl(url),
      headers: headers,
      method: RequestMethod.Post,
      body: body
    }), autoCatch);

  }

  /**
   * Create a put request
   *
   * The headers param is optional. If not set the content type is set to
   * application/json
   *
   * @param {string} url
   * @param {any} body
   * @param {Headers} headers
   * @returns {Observable<JsonResponse>}
   */
  public put(url: string, body: any, headers?: Headers, autoCatch: boolean = true): Observable<JsonResponse> {
    return this.request(new RequestOptions({
      url: JsonApiService.buildUrl(url),
      headers: headers,
      method: RequestMethod.Put,
      body: body
    }), autoCatch);
  }

  /**
   * Create a delete request
   *
   * The headers param is optional. If not set the content type is set to
   * application/json
   *
   * @param {string} url
   * @param {Headers} headers
   * @returns {Observable<JsonResponse>}
   */
  public delete(url: string, headers?: Headers, autoCatch: boolean = true): Observable<JsonResponse> {
    return this.request(new RequestOptions({
      url: JsonApiService.buildUrl(url),
      headers: headers,
      method: RequestMethod.Delete
    }), autoCatch);
  }

  /**
   * @deprecated Use the get method
   * @param url
   * @param headers
   * @returns {Observable<R>}
   */
  @deprecated('get')
  public fetch(url: string, headers?: Headers): Observable<any> {
    if (!headers) {
      headers = new Headers();
    }
    return this.http.get(JsonApiService.getBaseUrl() + 'assets/api' + url, {headers: headers})
        .delay(100)
        .map((response: Response) => {
          if (response.status >= 400) {
            return null;
          }
          let json = response.json();
          if (!json) {
            return null;
          }
          return json;
        })
        .catch(this.handleError);
  }

  executeRequest(autoCatch: boolean, mapFnc, catchFnc, requestOptions): Observable<JsonResponse> {
    let json: any = {};

    if (requestOptions.url.endsWith('/auth/login')) {
      json = {jwt: "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI2ZjU0MTU1Yy03NjU1LTQ5MDQtOTgzZC1lY2Y3OTcwNTA3ZDciLCJzdWIiOiJhZG1pbiRUZXN0REIiLCJpYXQiOjE1NTExNzQyOTcsImV4cCI6MTU1MTE3ODE5OH0.xec8Ew2etQsNcAOGjMvZR_FBuLEmoAEoDBD4bX_gIlQ",
        refreshToken: "refresh",
        expirationDate: "2030-12-31T00:00:00.000Z"};
    } else if (requestOptions.url.endsWith('/validity')) {
      json = {result: true};
    }

    return Observable.of({ json: json, headers: new Headers(), status: 200, cached: true });
  }

  /**
   * Add base url to url
   * @param {string} url
   * @returns {string}
   */
  private addBaseUrl(url: string): string {
    return JsonApiService.getBaseUrl() + url;
  }

  /**
   * Create a http request
   *
   * The method attribute has the default value get
   *
   * The headers attribute has the default value application/json
   *
   * @param {RequestOptions} requestOptions
   * @returns {Observable<JsonResponse>}
   */
  private request(requestOptions: RequestOptions, autoCatch: boolean): Observable<JsonResponse> {
    // if url is not set - throw error
    if (!requestOptions.url) {
      // TODO : def Error Code
      return Observable.throw('request url not set');
    }

    // if request method is not set - set to get
    if (!requestOptions.method) {
      requestOptions.method = RequestMethod.Get;
    }

    // if request method get, .... and the body is not set - throw error
    if (requestOptions.method === RequestMethod.Post || requestOptions.method === RequestMethod.Put) {
      if (!requestOptions.body) {
        return Observable.throw('request body not set');
      }
    }

    // if headers is not set - create header
    if (!requestOptions.headers) {
      requestOptions.headers = new Headers();

    }

    // if content type is not set - set to application/json
    if (!requestOptions.headers.has('Content-Type')) {
      requestOptions.headers.append('Content-Type', 'application/json');
    }

    if (requestOptions.url.indexOf('/users/bootstrapuser') === -1
        && requestOptions.url.indexOf('/login') === -1
        && requestOptions.url.indexOf('/activations') === -1
        && requestOptions.url.indexOf('/forgotpwd/temptoken') === -1
        && requestOptions.url.indexOf('/forgotpwd/users') === -1
        && requestOptions.url.indexOf('/users/me/password') === -1) {

      // add auth token if set
      if (Token.GetToken()) {
        const token = Token.GetToken();
        if (token && token.jwt) {
          requestOptions.headers.append('X-AUTH', token.jwt);
        } else {
          return Observable.throw('X-AUTH Token not set!');
        }
      }

    }

    let mapFnc = (response: Response): JsonResponse => {
      if (response.status >= 400) {
        this.logger.error(`MAF0x008D: request() - mapFnc`, {
          requestOptions: requestOptions,
          err: response.text()
        });
      }
      if (response.headers) {
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.indexOf('application/json') !== -1) {
          return {
            json: response.json(),
            headers: response.headers,
            status: response.status,
            cached: false
          };
        }
      } else {
        this.logger.error('MAF0x00BA: Response header is not set!');
        throw new Error('MAF0x00BA: Response header is not set!');
      }
      this.logger.debug('response contains is not typeof json', response);
      return {
        json: null,
        headers: response.headers,
        status: response.status,
        cached: false
      };
    };

    let catchFnc = (error: Response | any) => {
      let errMsg: string;
      this.loading.stop();
      if (error instanceof Response && error.text().length) {
        let response: Response = error as Response;
        errMsg = response.json().error;
      } else {
        errMsg = error.toString();
      }
      if (error.status === 401) {
        console.log(' Found 401!');
        this.show401Dialog(this.noti, this.router);
        return Observable.of(null);
      } else if (error.status === 403) {
        console.log(' Found 403!');
        this.show403Dialog(this.noti, this.router);
        return Observable.of(null);
      } else {
        this.logger.error('MAF0x008E:' + errMsg, {requestOptions: requestOptions, response: error});
        return Observable.throw(error);
      }
    };

    if (requestOptions.headers.get('X-AUTH')) {
      const token = Token.GetToken();
      if (token) {
        if (token.expirationDate) {
          // compare current date to expiration date minus a threshold for stability
          if (Date.now() >= token.expirationDate.getTime() - 5000) {
            // retrieve new jwt
            const options: RequestOptions = new RequestOptions({
              url: JsonApiService.buildUrl(`/auth/tokens/${token.jwt}/refresh`),
              headers: JsonApiService.HeaderJson,
              method: RequestMethod.Post,
              body: TypedJSON.stringify(new StringWrapper(token.refreshToken))
            });
            return (this.http.request(new Request(options)))
                .timeoutWith(2500, Observable.of({
                  json: null,
                  headers: null,
                  status: 404,
                  cached: false
                }))
                .concatMap((response: Response) => {
                  const newToken: Token = TypedJSON.parse(response.json(), Token);
                  Token.SetToken(newToken);
                  if (requestOptions.headers) {
                    if (newToken.jwt) {
                      requestOptions.headers.set('X-AUTH', newToken.jwt);
                      return this.executeRequest(autoCatch, mapFnc, catchFnc, requestOptions);
                    } else {
                      return Observable.throw('X-Auth token is not set!');
                    }
                  } else {
                    return Observable.throw('RequestOption header is not set!');
                  }
                }).catch(catchFnc);
          }
          else {
            return this.executeRequest(autoCatch, mapFnc, catchFnc, requestOptions);
          }
        } else {
          // return this.executeRequest(autoCatch, mapFnc, catchFnc, requestOptions);
          return Observable.throw('Token expirationDate is not set!');
        }
      } else {
        return this.executeRequest(autoCatch, mapFnc, catchFnc, requestOptions);
        // return Observable.throw('Token is not set!');
      }
    }
    else {
      return this.executeRequest(autoCatch, mapFnc, catchFnc, requestOptions);
    }
  }

  private extractData(response: Response): JsonResponse | null {
    if (response.status !== 200) {
      return null;
    }
    let json = response.json();
    if (!json) {
      return null;
    }
    return {json: json, headers: response.headers, status: response.status, cached: false};
  }

  private handleError(error: any | Response) {
    let errMsg: string;
    if (error instanceof Response) {
      const body = error.json() || '';
      const err = body.error || JSON.stringify(body);
      errMsg = `${error.status} - ${error.statusText || ''} ${err}`;
      if (error.status === 401) {
        console.log(' Found 401!');
        this.show401Dialog(this.noti, this.router);
      }
      if (error.status === 403) {
        console.log(' Found 403!');
        this.show403Dialog(this.noti, this.router);
      }
    } else {
      errMsg = error.message ? error.message : error.toString();
    }
    errMsg = 'MAF0x00BB: ' + errMsg;
    return Observable.throw(errMsg);
  }

  public show403Dialog(noti: NotificationService, router: Router) {
    noti.notificationOkOnly(
        'Nicht berechtigt (403 Forbidden)',
        'Ihnen fehlt die nötige Berechtigung, um die gewünschte Aktion durchzuführen.',
        undefined
    );
  }

  public show401Dialog(noti: NotificationService, router: Router) {
    noti.notificationOkOnly(
        'Nicht autorisiert (401 Unauthorized)',
        'Sie sind nicht angemeldet. Die Anfrage kann nicht ohne gültige Authentifizierung durchgeführt werden.',
        new DialogCallbackTwo(
            () => {
              router.navigateByUrl('/auth/login')
            },
            () => {
              router.navigateByUrl('/auth/login')
            }
        )
    );
  }

}
