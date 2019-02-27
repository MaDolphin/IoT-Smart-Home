/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Component, DebugElement, Injectable, OnInit } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { expect } from 'chai';
import { CheckBoxFormControl, TextFormControl } from '@shared/generic-form/controls';
import { GenericFormControl, GenericFormGroup } from '@shared/generic-form/generic-form';
import { AddIf, Control, Group, RemoveIf, Required } from '@shared/generic-form/generic-form/decorators';
import { validate } from '@shared/generic-form/validator';
import { A } from '@testutils/mocking';
import { AddValidator } from '@shared/generic-form/generic-form/decorators/validator';

interface IRegistration {
  username: string;
  password: string;
  email: string;
  newsLetter: boolean;
  newsLetterEmail?: string;
}

class EmailFormControl extends GenericFormControl<string> {

  @AddValidator
  public isAnEmailValidator() {
    return validate(this).to.be.an.email;
  }

}

@Injectable()
@Group()
class RegistrationForm extends GenericFormGroup<IRegistration> {

  @Control()
  @Required()
  public username: TextFormControl = undefined as any;

  @Control()
  @Required()
  public password: TextFormControl = undefined as any;

  @Control()
  @Required()
  public email: EmailFormControl = undefined as any;

  @Control()
  public newsLetter: CheckBoxFormControl = undefined as any;

  @Control()
  @Required()
  @AddIf((newsLetter) => {
    validate(newsLetter).is.checked;
  }, 'newsLetter')
  @RemoveIf((newsLetter) => {
    validate(newsLetter).is.not.checked;
  }, 'newsLetter')
  public newsLetterEmail: EmailFormControl = undefined as any;

}

@Component({
  template: `
              <form [formGroup]="form" (submit)="form.submit()">
                <input formControlName="username" name="username">
                <input formControlName="password" name="password">
                <input formControlName="email" name="email">
                <input formControlName="newsLetter" name="newsLetter">
                <input *ngIf="form.controls.newsLetterEmail" name="newsLetterEmail" formControlName="newsLetterEmail">
                <input id="submit-btn" type="submit">
              </form>
            `,
  providers: [RegistrationForm]
})
class RegistrationComponent implements OnInit {

  public ngOnInit(): void {
    this.form.init();
  }

  constructor(public form: RegistrationForm) {}

}

describe('Generic Form', () => {

  describe('Registration Form', () => {

    describe('Form Group', () => {

      beforeEach(() => {
        TestBed.configureTestingModule({ providers: [RegistrationForm] });
      });

      describe('With empty model', () => {

        let form: RegistrationForm;

        it('Create', () => {
          form = TestBed.get(RegistrationForm);

          expect(form, 'form instance should be created').to.be.not.undefined;

          expect(form.username, 'username form control instance should be **not** created').to.be.undefined;
          expect(form.password, 'username form control instance should be **not** created').to.be.undefined;
          expect(form.email, 'username form control instance should be **not** created').to.be.undefined;
          expect(form.newsLetter, 'username form control instance should be **not** created').to.be.undefined;
          expect(form.newsLetterEmail, 'username form control instance should be **not** created').to.be.undefined;

        });

        it('Init', () => {
          try {
            form.init();
          } catch (e) {
            console.log(e);
            expect.fail('throws an exception, throws not an exception', e.message);
          }

          expect(form.initialized).to.be.true;

          expect(form.username, 'username form control instance should be created').to.be.not.undefined;
          expect(form.password, 'username form control instance should be created').to.be.not.undefined;
          expect(form.email, 'username form control instance should be created').to.be.not.undefined;
          expect(form.newsLetter, 'username form control instance should be created').to.be.not.undefined;
          expect(form.newsLetterEmail, 'username form control instance should be created').to.be.not.undefined;

          expect(form.controls).has.ownProperty('username');
          expect(form.controls).has.ownProperty('password');
          expect(form.controls).has.ownProperty('email');
          expect(form.controls).has.ownProperty('newsLetter');
          expect(form.controls).has.not.ownProperty('newsLetterEmail');

          expect(form.username.validatorFunctions).to.have.length(1);
          expect(form.password.validatorFunctions).to.have.length(1);
          expect(form.email.validatorFunctions).to.have.length(2);
          expect(form.newsLetter.validatorFunctions).to.have.length(0);
          expect(form.newsLetterEmail.validatorFunctions).to.have.length(2);

        });

        it('Invalid', () => {

          expect(form.validate()).to.be.not.null;

          form.updateValueAndValidity();

          expect(form.invalid).to.be.true;

        });

        it('Valid without news letter email', () => {

          form.username.setValue('username');
          form.password.setValue('password');
          form.email.setValue('mail@mail.de');

          expect(form.validate()).to.be.null;

          form.updateValueAndValidity();

          expect(form.valid).to.be.true;

        });

        it('news letter checkbox', () => {

          expect(form.controls).has.not.ownProperty('newsLetterEmail');
          form.newsLetter.setValue(true);
          expect(form.controls).has.ownProperty('newsLetterEmail');
          form.newsLetter.setValue(false);
          expect(form.controls).has.not.ownProperty('newsLetterEmail');
          form.newsLetter.setValue(true);
          expect(form.controls).has.ownProperty('newsLetterEmail');
          form.newsLetter.setValue(false);
          expect(form.controls).has.not.ownProperty('newsLetterEmail');

        });

        it('Invalid with news letter checkbox', () => {

          form.newsLetter.setValue(true);

          expect(form.newsLetterEmail.value).to.be.null;

          expect(form.validate()).to.be.not.null;

          form.updateValueAndValidity();

          expect(form.invalid).to.be.true;

        });

        it('Valid with news letter email', () => {

          form.newsLetterEmail.setValue('mail@mail.de');

          expect(form.validate()).to.be.null;

          form.updateValueAndValidity();

          expect(form.valid).to.be.true;

        });

        it('Valid without news letter email', () => {

          form.newsLetterEmail.setValue('');

          expect(form.validate()).to.be.not.null;

          form.updateValueAndValidity();

          expect(form.invalid).to.be.true;

        });

      });

      describe('With model', () => {

        let form: RegistrationForm;
        let model: IRegistration = {
          username:        'modelUsername',
          password:        'modelPassword',
          email:           'modelEmail@mail.com',
          newsLetter:      false,
          newsLetterEmail: null,
        };

        it('Create', () => {
          form = TestBed.get(RegistrationForm);

          expect(form, 'form instance should be created').to.be.not.undefined;
          expect(form.username, 'username form control instance should be **not** created').to.be.undefined;
          expect(form.password, 'username form control instance should be **not** created').to.be.undefined;
          expect(form.email, 'username form control instance should be **not** created').to.be.undefined;
          expect(form.newsLetter, 'username form control instance should be **not** created').to.be.undefined;
          expect(form.newsLetterEmail, 'username form control instance should be **not** created').to.be.undefined;

        });

        it('Init', () => {
          try {
            form.init(JSON.parse(JSON.stringify(model)));
          } catch (e) {
            expect.fail('throws an exception, throws not an exception', e.message);
          }

          expect(form.initialized).to.be.true;

          expect(form.username, 'username form control instance should be created').to.be.not.undefined;
          expect(form.password, 'username form control instance should be created').to.be.not.undefined;
          expect(form.email, 'username form control instance should be created').to.be.not.undefined;
          expect(form.newsLetter, 'username form control instance should be created').to.be.not.undefined;
          expect(form.newsLetterEmail, 'username form control instance should be created').to.be.not.undefined;

          expect(form.controls).has.ownProperty('username');
          expect(form.controls).has.ownProperty('password');
          expect(form.controls).has.ownProperty('email');
          expect(form.controls).has.ownProperty('newsLetter');
          expect(form.controls).has.not.ownProperty('newsLetterEmail');

          expect(form.username.validatorFunctions).to.have.length(1);
          expect(form.password.validatorFunctions).to.have.length(1);
          expect(form.email.validatorFunctions).to.have.length(2);
          expect(form.newsLetter.validatorFunctions).to.have.length(0);
          expect(form.newsLetterEmail.validatorFunctions).to.have.length(2);

          expect(form.username.value).to.eq(model.username);
          expect(form.password.value).to.eq(model.password);
          expect(form.email.value).to.eq(model.email);
          expect(form.newsLetter.value).to.eq(model.newsLetter);
          expect(form.model).to.be.deep.eq(model);

          expect(form.validate()).to.be.null;

          form.updateValueAndValidity();

          expect(form.valid).to.be.true;

        });

        it('Valid without news letter email', () => {

          form.username.setValue('username');
          form.password.setValue('password');
          form.email.setValue('mail@mail.de');

          expect(form.validate()).to.be.null;

          form.updateValueAndValidity();

          expect(form.valid).to.be.true;

        });

        it('news letter checkbox', () => {

          expect(form.controls).has.not.ownProperty('newsLetterEmail');
          form.newsLetter.setValue(true);
          expect(form.controls).has.ownProperty('newsLetterEmail');
          form.newsLetter.setValue(false);
          expect(form.controls).has.not.ownProperty('newsLetterEmail');
          form.newsLetter.setValue(true);
          expect(form.controls).has.ownProperty('newsLetterEmail');
          form.newsLetter.setValue(false);
          expect(form.controls).has.not.ownProperty('newsLetterEmail');

        });

        it('Invalid with news letter checkbox', () => {

          form.newsLetter.setValue(true);

          expect(form.newsLetterEmail.value).to.be.null;

          expect(form.validate()).to.be.not.null;

          form.updateValueAndValidity();

          expect(form.invalid).to.be.true;

        });

        it('Valid with news letter email', () => {

          form.newsLetterEmail.setValue('mail@mail.de');

          expect(form.validate()).to.be.null;

          form.updateValueAndValidity();

          expect(form.valid).to.be.true;

        });

        it('Valid without news letter email', () => {

          form.newsLetterEmail.setValue('');

          expect(form.validate()).to.be.not.null;

          form.updateValueAndValidity();

          expect(form.invalid).to.be.true;

        });

      });

    });

    describe('Component', () => {

      let fixture: ComponentFixture<RegistrationComponent>;
      let component: RegistrationComponent;
      let debugElm: DebugElement;
      let submitInput: DebugElement;
      let usernameInput: DebugElement;
      let passwordInput: DebugElement;
      let emailInput: DebugElement;
      let newsLetter: DebugElement;
      let newsLetterEmail: DebugElement;

      beforeEach(A(async () => {
        await TestBed.configureTestingModule({
          declarations: [RegistrationComponent],
          imports:      [ReactiveFormsModule],
        }).compileComponents();

        fixture   = TestBed.createComponent(RegistrationComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        debugElm        = fixture.debugElement;
        submitInput     = debugElm.query(By.css('#submit-btn'));
        usernameInput   = debugElm.query(By.css('input[name=username]'));
        passwordInput   = debugElm.query(By.css('input[name=password]'));
        emailInput      = debugElm.query(By.css('input[name=email]'));
        newsLetter      = debugElm.query(By.css('input[name=newsLetter]'));
        newsLetterEmail = debugElm.query(By.css('input[name=newsLetterEmail]'));

      }));

      it('Create', () => {

        expect(fixture).to.be.not.undefined;
        expect(component).to.be.not.undefined;
        expect(debugElm).to.be.not.undefined;
        expect(submitInput).to.be.not.undefined;
        expect(usernameInput).to.be.not.undefined;
        expect(passwordInput).to.be.not.undefined;
        expect(emailInput).to.be.not.undefined;
        expect(newsLetter).to.be.not.undefined;
        expect(newsLetterEmail).to.be.not.undefined;
        expect(submitInput).to.be.not.null;
        expect(usernameInput).to.be.not.null;
        expect(passwordInput).to.be.not.null;
        expect(emailInput).to.be.not.null;
        expect(newsLetter).to.be.not.null;
        expect(newsLetterEmail).to.be.null;

      });

      it('Invalid Submit', () => {

        submitInput.triggerEventHandler('click', null);

        expect(component.form.invalid).to.be.true;

      });

      it('Valid without news letter email', (() => {

        const username = 'username';
        const password = 'password';
        const email    = 'mail@mail.de';

        usernameInput.nativeElement.value = username;
        passwordInput.nativeElement.value = password;
        emailInput.nativeElement.value    = email;

        usernameInput.nativeElement.dispatchEvent(new Event('input'));
        passwordInput.nativeElement.dispatchEvent(new Event('input'));
        emailInput.nativeElement.dispatchEvent(new Event('input'));

        fixture.detectChanges();

        expect(component.form.username.value).to.be.eq(username);
        expect(component.form.password.value).to.be.eq(password);
        expect(component.form.email.value).to.be.eq(email);

        submitInput.triggerEventHandler('click', null);

        expect(component.form.valid).to.be.true;

      }));

      it('news letter checkbox', () => {

        component.form.newsLetter.setValue(true);
        fixture.detectChanges();
        newsLetterEmail = debugElm.query(By.css('input[name=newsLetterEmail]'));
        expect(newsLetterEmail).to.be.not.null;
        component.form.newsLetter.setValue(false);
        fixture.detectChanges();
        newsLetterEmail = debugElm.query(By.css('input[name=newsLetterEmail]'));
        expect(newsLetterEmail).to.be.null;

      });

      it('Invalid with news letter checkbox', () => {

        const username = 'username';
        const password = 'password';
        const email    = 'mail@mail.de';

        usernameInput.nativeElement.value = username;
        passwordInput.nativeElement.value = password;
        emailInput.nativeElement.value    = email;

        usernameInput.nativeElement.dispatchEvent(new Event('input'));
        passwordInput.nativeElement.dispatchEvent(new Event('input'));
        emailInput.nativeElement.dispatchEvent(new Event('input'));

        component.form.newsLetter.setValue(true);
        fixture.detectChanges();
        submitInput.triggerEventHandler('click', null);

        expect(component.form.invalid).to.be.true;

      });

      it('Valid with news letter email', () => {

        component.form.newsLetter.setValue(true);
        fixture.detectChanges();

        newsLetterEmail = debugElm.query(By.css('input[name=newsLetterEmail]'));

        const username = 'username';
        const password = 'password';
        const email    = 'mail@mail.de';

        usernameInput.nativeElement.value   = username;
        passwordInput.nativeElement.value   = password;
        emailInput.nativeElement.value      = email;
        newsLetterEmail.nativeElement.value = email;

        newsLetterEmail.nativeElement.dispatchEvent(new Event('input'));
        usernameInput.nativeElement.dispatchEvent(new Event('input'));
        passwordInput.nativeElement.dispatchEvent(new Event('input'));
        emailInput.nativeElement.dispatchEvent(new Event('input'));

        fixture.detectChanges();

        expect(component.form.newsLetterEmail.value).to.be.eq(email);

        submitInput.triggerEventHandler('click', null);

        expect(component.form.valid).to.be.true;

      });

      it('Valid without news letter email', () => {

        component.form.newsLetter.setValue(true);
        fixture.detectChanges();

        newsLetterEmail = debugElm.query(By.css('input[name=newsLetterEmail]'));

        const username = 'username';
        const password = 'password';
        const email    = 'mail@mail.de';

        usernameInput.nativeElement.value   = username;
        passwordInput.nativeElement.value   = password;
        emailInput.nativeElement.value      = email;
        newsLetterEmail.nativeElement.value = ''; // set invalid email

        newsLetterEmail.nativeElement.dispatchEvent(new Event('input'));
        usernameInput.nativeElement.dispatchEvent(new Event('input'));
        passwordInput.nativeElement.dispatchEvent(new Event('input'));
        emailInput.nativeElement.dispatchEvent(new Event('input'));

        fixture.detectChanges();

        submitInput.triggerEventHandler('click', null);

        expect(component.form.invalid).to.be.true;

      });

    });

  });

});
