/* (c) https://github.com/MontiCore/monticore */

import { Injectable } from '@angular/core';
import { Http, RequestOptions, Response, ResponseContentType } from '@angular/http';
import { Logger } from '@upe/logger';
import { Observable } from 'rxjs/Rx';
import { JsonApiService } from '@jsonapiservice/json-api.service';
import { Token } from '../auth/token';

@Injectable()
export class DownloadFileService {

  logger: Logger;

  // TODO : consider changing/moving to a separate file
  public static get preRequestOptionsPDF(): RequestOptions {
    return new RequestOptions({
      headers: JsonApiService.HeaderJson,
    });
  }

  public static get requestOptionsPDF(): RequestOptions {
    return new RequestOptions({
      headers: JsonApiService.HeaderPDF,
      responseType: ResponseContentType.Blob
    });
  }

  constructor(
    private http: Http,
    private jsonApiService: JsonApiService
  ) {
    this.logger = new Logger({name: 'DownloadFileService', flags: ['service', 'http']});
  }

  // TODO : methods below are draft functions, used in a single case, rewrite as needed
  /**
   * Download file. Sends two consequent requests, first to ask server to prepare
   * file, second to retrieve file. Retrieved file defaults to json file.
   *
   * Parameter filename is optional, default value is defined on the server side,
   * if there is no default value on the server, defaults to 'file.json'.
   *
   * @param {string} url
   * @param {RequestOptions} preDownloadOptions
   * @param {RequestOptions} downloadOptions
   * @param {string} filename
   * @returns {Observable<Response>}
   */
  public download(
    url: string, preDownloadOptions: RequestOptions,
    downloadOptions: RequestOptions, contentWindow?: Window) {

    // default to json requests
    if (!preDownloadOptions.headers) {
      preDownloadOptions.headers = JsonApiService.HeaderJson;
    }
    if (!downloadOptions.headers) {
      downloadOptions.headers = JsonApiService.HeaderJson;
    }

    // use JsonApiService post method, as desined specifically for json responses
    this.jsonApiService.post(url, preDownloadOptions.body, preDownloadOptions.headers).concatMap(file => {
      console.log(file);
      return this.get('/domain/form/pdf/' + file.json, downloadOptions);
    })
    .map((res: Response) => { this.logger.debug('response', res); return res; })
    .catch((error: Response | any) => {
      return Observable.throw(error);
    })
    .subscribe(
      (data: Response) => {
        // default values for response body
        let contentType = 'application/json';

        if (data.headers && data.headers.has('content-type')) {
          contentType = data.headers.get('content-type') as string;
        }

        let blob = new Blob([data.blob()], {type: contentType});
        // open pdf in a new window
        if (!contentWindow) {
          window.open(URL.createObjectURL(blob));
        } else {
          contentWindow.location.href = URL.createObjectURL(blob);
        }
      },
      err => this.logger.error('MAF0x0097: ' + err)
    );
  }

  /**
   * Create an authenticated get request.
   *
   * @param {string} url
   * @param {RequestOptions} requestOptions
   * @returns {Observable<Response>}
   */
  public get(url: string, requestOptions: RequestOptions): Observable<Response> {
    return this.http.get(this.buildUrl(url), this.authenticate(requestOptions));
  }

  /**
   * Add authentication token to request header
   *
   * @param {RequestOptions} requestOptions
   * @returns {RequestOptions}
   */
  private authenticate(requestOptions: RequestOptions): RequestOptions {
    if (requestOptions.headers) {
      if (Token.GetToken()) {
        const token = Token.GetToken();
        if (token && token.jwt) {
          requestOptions.headers.append('X-AUTH', token.jwt);
        }
      }
    }
    return requestOptions;
  }

  /**
   * Build complete url string
   *
   * @param {string} url
   * @returns {string}
   */
  private buildUrl(url: string): string {
    return location.protocol + '//' + location.hostname + (
      location.port ? ':' + location.port : ''
      ) + '/' + 'montigem-be/api' + url;
  }

}
