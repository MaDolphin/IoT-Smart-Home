/* (c) https://github.com/MontiCore/monticore */

import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { isEmailValid } from '@shared/architecture/data/utils/utils';
import { JsonApiService, JsonResponse } from '@jsonapiservice/json-api.service';
import { NotificationService } from '@shared/notification/notification.service';
import { HttpClient } from '@angular/common/http';
import { ForgotPasswordForm } from '@shared/auth/forgot/forgot.form';

@Component(
    {
      templateUrl: './forgot.component.html',
      styleUrls: ['../auth.scss'],
      providers: [ForgotPasswordForm]
    }
)
export class ForgotComponent implements OnInit {
  chairs: string[] = [];
  public email: string = '';

  public emailValidator: ((email: string) => boolean) = isEmailValid;

  constructor(private _api: JsonApiService, private _notificationService: NotificationService, private _router: Router, private http: HttpClient, public forms: ForgotPasswordForm) {

  }

  public async ngOnInit(): Promise<void> {
    this.forms.init();
    this.http.get(JsonApiService.buildUrl(`/domain/datasource/dbname`), {}).subscribe(data => {
      this.chairs = <string[]>data;
      this.forms.datenbank.setOptions(this.chairs);
      this.forms.datenbank.setModelValue('TestDB');
    });
  }

  public async onSubmit(form: NgForm, event: Event) {
    event.preventDefault();
    this._api.post('/forgotpwd/users/' + this.email + '/' + this.forms.datenbank.getModelValue(), {}, JsonApiService.HeaderJson).map((response: JsonResponse) => {
      this._notificationService.notificationOkOnly('Passwort zurücksetzen', 'Eine Email mit weiteren Informationen wurde Ihnen an die angegebene Adresse zugesandt.');
      this._router.navigate(['../login']);
      return true;
    }).catch(() => {
      this._notificationService.notificationOkOnly('Fehler', 'Im System ist ein Fehler beim Zurücksetzen des Passwortes aufgetreten. Bitte wenden Sie sich an Ihren Administrator', undefined, '');
      return Observable.of(false);
    }).share().toPromise();
  }
}
