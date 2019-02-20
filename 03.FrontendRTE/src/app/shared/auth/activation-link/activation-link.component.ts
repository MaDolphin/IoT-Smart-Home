import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router, UrlSegment } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { JsonApiService, JsonResponse } from '@shared/architecture/services/json-api.service';
import { NotificationService } from '@shared/notification/notification.service';

@Component({
  selector: 'app-activation-link',
  templateUrl: './activation-link.component.html',
  styleUrls: ['../auth.scss']
})
export class ActivationLinkComponent implements OnInit {

  public isPwCompatible: boolean = false;
  public barLabel: string = 'Passwort Stärke:';
  private id: string = '';
  private key: string = '';
  private resource: string = '';
  private isActivation: boolean = false;
  private isPwForgot: boolean = false;
  private xAuthHeader: string = '';

  constructor(private _api: JsonApiService,
              private _router: Router,
              private _route: ActivatedRoute,
              private _notificationService: NotificationService) {
  }

  private _pw1: string = '';

  public get pw1(): string {
    return this._pw1;
  }

  public set pw1(pw: string) {
    this._pw1 = pw;
    this.isPwCompatible = this._pw1 === this.pw2 ? true : false;
  }

  private _pw2: string = '';

  public get pw2(): string {
    return this._pw2;
  }

  public set pw2(pw: string) {
    this._pw2 = pw;
    this.isPwCompatible = this._pw1 === this.pw2 ? true : false;
  }

  ngOnInit() {

    this._route.queryParams.subscribe((params) => {
      if (params['id'] && params['key'] && params['resource']) {
        this.id += params['id'];
        this.key += params['key'];
        this.resource += params['resource'];
      }
    });

    this._route.url.subscribe((urls: UrlSegment[]) => {
      let url: string = urls[urls.length - 1].path;
      this.isActivation = url.indexOf('activations') !== -1 ? true : false;
      this.isPwForgot = url.indexOf('forgotPwd') !== -1 ? true : false;
    });

    if (this.id !== '' && this.key !== '' && this.resource !== '') {
      if (this.isActivation) {
        this._api.post('/activations/' + this.id + '/' + this.key + '/' + this.resource, {}, JsonApiService.HeaderJson).map((response: JsonResponse) => {
          if (response.headers) {
            let header: string | null = response.headers.get('X-AUTH');
            this.xAuthHeader = header ? header : '';
          }
          if (this.xAuthHeader === '') {
            this._notificationService.notificationOkOnly('Verifizierung Fehlgeschlagen',
                'Das System konnte den verwendeten Link nicht verifizieren. Bitte wenden Sie sich an Ihren Administrator.');
            this._router.navigate(['../login'])
          }
          return true;
        }).catch(
            () => {
              this._router.navigate(['../login']);
              return Observable.of(false);
            }).share().toPromise();
      } else if (this.isPwForgot) {
        this._api.post('/forgotpwd/temptoken/' + this.id + '/' + this.key + '/' + this.resource, {}, JsonApiService.HeaderJson).map((response: JsonResponse) => {
          if (response.headers) {
            let header: string | null = response.headers.get('X-AUTH');
            this.xAuthHeader = header ? header : '';
          }
          if (this.xAuthHeader === '') {
            this._notificationService.notificationOkOnly('Verifizierung Fehlgeschlagen',
                'Das System konnte den verwendeten Link nicht verifizieren. Bitte wenden Sie sich an Ihren Administrator.');
            this._router.navigate(['../login'])
          }
          return true;
        }).catch(() => {
          this._router.navigate(['../login']);
          return Observable.of(false);
        }).share().toPromise();
      }
    } else {
      this._notificationService.notificationOkOnly('Verifizierung Fehlgeschlagen',
          'Das System konnte den verwendeten Link nicht verifizieren. Bitte wenden Sie sich an Ihren Administrator.');
      this._router.navigate(['../login'])
    }
  }

  public async onSubmit(form: NgForm, event: Event) {
    event.preventDefault();
    if (this.resource !== '' && this.xAuthHeader && (this.isActivation || this.isPwForgot)) {
      this._api.put('/domain/users/me/password/' + this.resource, {value: this.pw2}, JsonApiService.HeaderJSONWithJWT(this.xAuthHeader)).map((response: JsonResponse) => {
        return true;
      }).catch(() => Observable.of(false)).subscribe((value) => {
            this._router.navigate(['../login'])
          }
      );
    } else {
      this._notificationService.notificationOkOnly('Authorisierung Fehlgeschlagen',
          'Leider fehlt Ihnen die Berechtigung das Passwort neu zu setzen. Bitte wenden Sie sich an Ihren Administrator.');
      this._router.navigate(['../login'])
    }
  }

}
