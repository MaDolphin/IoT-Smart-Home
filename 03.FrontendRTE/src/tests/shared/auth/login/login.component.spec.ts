/* tslint:disable:no-unused-variable */

import { DebugElement } from '@angular/core';
import { async, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { AlertModule } from 'ngx-bootstrap';
import { Observable } from 'rxjs';
import { AuthService } from '@shared/auth/auth.service';
import { LoginComponent } from '@shared/auth/login/login.component';
import { LoadingModule } from '@shared/layout/loading/loading.module';
import { NotificationService } from '@shared/notification/notification.service';

xdescribe('Component: Login', () => {

  let loginSpy;
  let shibbolethSpy;
  let fixture;
  let auth;
  let userIdInput: DebugElement;
  let passwordInput: DebugElement;
  let signInButton: DebugElement;
  let shibbolethButton: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
                                     declarations: [
                                       LoginComponent,
                                     ],
                                     providers:    [
                                       {
                                         provide:  AuthService,
                                           NotificationService,
                                         useValue: {
                                           login:      (userid: string, password: string) => Observable.of(true),
                                           shibboleth: () => Observable.of(true),
                                         },
                                       },
                                     ],
                                     imports:      [
                                       FormsModule,
                                       LoadingModule,
                                       AlertModule.forRoot(),
                                     ],
                                   }).compileComponents().then(() => {
      fixture = TestBed.createComponent(LoginComponent);
      auth    = fixture.debugElement.injector.get(AuthService);

      // login functions allways return true;
      loginSpy      = spyOn(auth, 'login').and.returnValue(Observable.of(true));
      shibbolethSpy = spyOn(auth, 'shibboleth').and.returnValue(Observable.of(true));

      // extract DOM Elements
      let inputs       = fixture.debugElement.queryAll(By.css('input'));
      userIdInput      = inputs[0];
      passwordInput    = inputs[1];
      signInButton     = fixture.debugElement.query(By.css('.pull-right'));
      shibbolethButton = fixture.debugElement.query(By.css('.pull-left'));

      // first change detection
      fixture.detectChanges();
    });
  }));

  it('Buttons properties', () => {
    expect(signInButton).toBeDefined();
    expect(shibbolethButton).toBeDefined();
  });

  it('SignIn Button is on startup disabled', () => {
    fixture.detectChanges();
    expect(loginSpy.calls.any()).toBeFalsy();
    expect(shibbolethSpy.calls.any()).toBeFalsy();
    expect(signInButton.nativeElement.disabled).toBeTruthy();
  });

  it('Shibboleth Button is on startup disabled', () => {
    fixture.detectChanges();
    expect(loginSpy.calls.any()).toBeFalsy();
    expect(shibbolethSpy.calls.any()).toBeFalsy();
    expect(shibbolethButton.nativeElement.disabled).toBeTruthy();
  });

  it('Input Boxes properties', () => {
    expect(userIdInput).toBeDefined();
    expect(passwordInput).toBeDefined();
    expect(userIdInput.nativeElement.type).toEqual('text');
    expect(userIdInput.nativeElement.name).toEqual('userId');
    expect(userIdInput.nativeElement.required).toBeTruthy();
    expect(passwordInput.nativeElement.type).toEqual('password');
    expect(passwordInput.nativeElement.name).toEqual('password');
    expect(passwordInput.nativeElement.required).toBeTruthy();
  });

  it('Login with admin:pass', () => {
    async(() => {
      expect(loginSpy).toBeDefined();

      userIdInput.nativeElement.value = 'admin';
      userIdInput.nativeElement.dispatchEvent(new Event('input'));
      passwordInput.nativeElement.value = 'pass';
      passwordInput.nativeElement.dispatchEvent(new Event('input'));
      fixture.detectChanges();

      fixture.whenStable().then(() => {
          fixture.detectChanges();
          expect(signInButton.nativeElement.disabled).toBeFalsy();
          let evt;
          try {
              // Chrome, Safari, Firefox
              evt = new MouseEvent('click');
          } catch (e) {
              // PhantomJS (wat!)
              evt = document.createEvent('MouseEvent');
              evt.initMouseEvent('click', true, false);
          }
          signInButton.triggerEventHandler('click', evt);
          fixture.detectChanges();
          expect(loginSpy).toHaveBeenCalledTimes(1);
          expect(loginSpy).toHaveBeenCalledWith('admin', 'pass');
        });
      });
  });

  it('Login with shibboleth', () => {
    expect(shibbolethSpy).toBeDefined();
    let evt;
    try {
      // Chrome, Safari, Firefox
      evt = new MouseEvent('click');
    } catch (e) {
      // PhantomJS (wat!)
      evt = document.createEvent('MouseEvent');
      evt.initMouseEvent('click', true, false);
    }
    shibbolethButton.triggerEventHandler('click', evt);
    fixture.detectChanges();
    expect(shibbolethSpy).toHaveBeenCalledTimes(1);
  });

});
