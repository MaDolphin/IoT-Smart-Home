import { Component, DebugElement, Injectable, OnInit } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { expect } from 'chai';
import 'reflect-metadata';
import { TextFormControl } from '@shared/generic-form/controls';
import { GenericFormGroup } from '@shared/generic-form/generic-form';
import { Control, Group, Required } from '@shared/generic-form/generic-form/decorators';
import { A } from '@testutils/mocking';

@Injectable()
@Group()
class LoginForm extends GenericFormGroup<any> {

  @Control()
  @Required()
  public username: TextFormControl = undefined as any;

  @Control()
  @Required()
  public password: TextFormControl = undefined as any;

}

@Component({
  template: `
              <form [formGroup]="form" (submit)="form.submit()">
                <input formControlName="username" name="username">
                <input formControlName="password" name="password">
                <input id="submit-btn" type="submit">
              </form>
            `,
  providers: [LoginForm]
})
class LoginComponent implements OnInit {

  public ngOnInit(): void {
    this.form.init();
  }

  constructor(public form: LoginForm) {}

}

describe('Generic Form', () => {

describe('Login Form', () => {

  describe('Form Group', () => {

    beforeEach(() => {
      TestBed.configureTestingModule({ providers: [LoginForm] });
      form = TestBed.get(LoginForm);
    });

    let form: LoginForm;

    it('Create new instance', () => {
      expect(form.username).to.be.undefined;
      expect(form.password).to.be.undefined;

      expect(Object.keys(form.controls)).to.has.length(0);

    });

    it('Init', () => {

      form.init();

      expect(form.initialized).to.be.true;

      expect(form.username).to.be.not.undefined;
      expect(form.password).to.be.not.undefined;

      expect(form.username).to.be.instanceOf(TextFormControl);
      expect(form.password).to.be.instanceOf(TextFormControl);

      const controlKeys = Object.keys(form.controls);

      expect(controlKeys).to.has.length(2);
      expect(controlKeys).to.be.contains('username');
      expect(controlKeys).to.be.contains('password');

    });

    it('Invalid', () => {

      form.init();

      expect(form.validate()).to.be.not.null;

      form.updateValueAndValidity();

      expect(form.invalid).to.be.true;

    });

    it('Valid', () => {

      form.init();

      form.username.setValue('username');
      form.password.setValue('password');

      expect(form.validate()).to.be.null; // TODO : fix return type

      form.updateValueAndValidity();

      expect(form.valid).to.be.true;

    });

  });

  describe('Component only class', () => {

    let loginComponent: LoginComponent;
    let form: LoginForm;

    beforeEach(() => {
      TestBed.configureTestingModule({ providers: [LoginForm] });
      form = TestBed.get(LoginForm);
      loginComponent = new LoginComponent(form);
    });

    it('Constructor', () => {

      expect(loginComponent, 'Login Component var should be init').to.be.not.undefined;
      expect(loginComponent.form, 'The form property should be init in the constructor').to.be.not.undefined;

    });

    it('Form Init', () => {

      loginComponent.ngOnInit();

      expect(loginComponent.form.initialized).to.be.true;
      expect(loginComponent.form.controls).to.have.ownProperty('username');
      expect(loginComponent.form.controls).to.have.ownProperty('password');

    });

  });

  describe('Component', () => {

    let fixture: ComponentFixture<LoginComponent>;
    let component: LoginComponent;
    let debugElm: DebugElement;
    let submitInput: DebugElement;
    let usernameInput: DebugElement;
    let passwordInput: DebugElement;

    beforeEach(A(async () => {
      await TestBed.configureTestingModule({
        declarations: [LoginComponent],
        imports:      [ReactiveFormsModule],
      }).compileComponents();

      fixture   = TestBed.createComponent(LoginComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
      debugElm      = fixture.debugElement;
      submitInput   = debugElm.query(By.css('#submit-btn'));
      usernameInput = debugElm.query(By.css('input[name=username]'));
      passwordInput = debugElm.query(By.css('input[name=password]'));

    }));

    it('Create', () => {

      expect(fixture).to.be.not.undefined;
      expect(component).to.be.not.undefined;
      expect(debugElm).to.be.not.undefined;
      expect(submitInput).to.be.not.undefined;
      expect(usernameInput).to.be.not.undefined;
      expect(passwordInput).to.be.not.undefined;
      expect(submitInput).to.be.not.null;
      expect(usernameInput).to.be.not.null;
      expect(passwordInput).to.be.not.null;

    });

    it('Invalid Submit', () => {

      submitInput.triggerEventHandler('click', null);

      expect(component.form.invalid).to.be.true;

    });

    it('Valid Submit', (() => {

      const username = 'username';
      const password = 'password';

      usernameInput.nativeElement.value = username;
      passwordInput.nativeElement.value = password;

      usernameInput.nativeElement.dispatchEvent(new Event('input'));
      passwordInput.nativeElement.dispatchEvent(new Event('input'));

      fixture.detectChanges();

      expect(component.form.username.value).to.be.eq(username);
      expect(component.form.password.value).to.be.eq(password);

      submitInput.triggerEventHandler('click', null);

      expect(component.form.valid).to.be.true;

    }));

  });

});

});
