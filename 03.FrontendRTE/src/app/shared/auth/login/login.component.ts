/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { LoadingService } from '@shared/layout/loading/loading.service';
import { AuthService } from '@shared/auth/auth.service';
import { HttpClient } from '@angular/common/http';
import { LoginForm } from "@shared/auth/login/login.form";


@Component(
    {
      templateUrl: './login.component.html',
      styleUrls: ['../auth.scss'],
      providers: [LoginForm]
    }
)
export class LoginComponent implements OnInit {
  public user = {username: '', password: ''};

  constructor(private auth: AuthService, private loading: LoadingService, private http: HttpClient, public forms: LoginForm) {
  }

  public async ngOnInit(): Promise<void> {
    this.forms.init();
  }

  public async onSubmit(form: NgForm, event: Event) {
    event.preventDefault();
    this.loading.start();
    const result = this.auth.login(form.value.username, form.value.password, "TestDB").toPromise();
    if (!result) {
      alert('Der Benutzername oder das Passwort ist falsch! Bitte versuchen Sie es erneut.');
      this.loading.stop();
    }
  }

  shibbolethLogin() {
    this.auth.shibboleth().subscribe();
  }

}
