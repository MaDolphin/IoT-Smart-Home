import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Logger } from '@upe/logger';
import { TypedJSON } from '@upe/typedjson';
import 'rxjs/add/observable/fromPromise';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/delay';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/toPromise';
import { Observable } from 'rxjs/Observable';
import { JsonApiService, JsonResponse } from '@jsonapiservice/json-api.service';
import { NotificationService } from '@shared/notification/notification.service';
import { User } from '@shared/user/user';
import { Token } from './token';

@Injectable()
export class AuthService {

  private _isLoggin: boolean = false;
  private logger: Logger = new Logger({name: 'AuthService', flags: ['service']});

  constructor(private router: Router,
              private api: JsonApiService,
              private notification: NotificationService) {
  }

  public get isLoggedIn(): Observable<boolean> {
    return this._isLoggin ?
        Observable.of(this._isLoggin) :
        this.hasValidToken();
  }

  public get user(): User {
      return TypedJSON.parse(`{
          "userId": "` + Token.getUserId() + `",
          "firstName": "Lehrstuhl für Software Engineering",
          "lastName": " ",
          "picture": "https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png"
        }`, User);

  }

  public get exp(): string {
    return Token.getExpirationDate();
  }

  public get instanceName(): string {
    return Token.getInstanceName();
  }

  /**
   * store the URL so we can redirect after logging in
   * @type {string}
   * @private
   */
  private _redirectUrl: string = '';

  public set redirectUrl(url: string) {
    if (!url.match('auth')) {
      this._redirectUrl = url;
    }
  }

  public login(userid: string, password: string, resource: string): Observable<boolean> {
    return this.api.post('/auth/login', {
      username: userid,
      password: password,
      resource: resource
    }, JsonApiService.HeaderJson).map((response: JsonResponse) => {
      this.logger.info('login token: ', response);
      const token: Token = TypedJSON.parse(JSON.stringify(response.json), Token);
      // if (response.headers.has('X-AUTH')) {
      this.loginHandle(token);
      return true;
    }).catch((err) => {
      {
        this.notification.notificationOkOnly('Login Fehler', 'Es ist ein Fehler bei der Anmeldung aufgetreten! ' +
            'Überprüfen Sie Ihre Angaben und versuchen Sie es erneut.', undefined);
      }
      return Observable.of(false)
    }).share();
  }

  public hasValidToken(): Observable<boolean> {
    const token = Token.GetToken();

    if (!(token && token.jwt)) {
      return Observable.of(false);
    }

    // if !JWT aber Shibboleth -> erzeuge neuen Token

    return this.api.get(
        `/auth/tokens/${token.jwt}/validity`, true, true, undefined, false)
        .map(() => true)
        .catch((err) => {
          this.logger.error('MAF0x008F: invalid token', err);
          Token.RemoveToken();
          return Observable.of(false);
        }).do((status) => this._isLoggin = status);
  }

  public shibboleth(): Observable<boolean> {
    this.loginHandle(new Token());
    return Observable.of(true);
  }

  public logout(): void {
    Token.RemoveToken();
    this.router.navigateByUrl('/auth/login');
  }

  private loginHandle(token: Token): void {
    Token.SetToken(token);
    this.router.navigate([this._redirectUrl]);
  }

}
