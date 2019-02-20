import { Injectable } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { expect } from 'chai';
import { spy, stub } from 'sinon';
import { TextFormControl } from '@shared/generic-form/controls';
import { SelectFormControl } from '@shared/generic-form/controls/select.form-control';
import { GenericFormGroup } from '@shared/generic-form/generic-form';
import { ADD_IF, DISABLED_IF, LABEL_IF, OPTIONS_IF, PLACEHOLDER_IF, READONLY_IF, REMOVE_IF, } from '@shared/generic-form/generic-form/config';
import { AddIf, Control, DisabledIf, Group, LabelIf, PlaceholderIf, ReadonlyIf, RemoveIf, RequiredIf, } from '@shared/generic-form/generic-form/decorators';
import { DefaultIf, OptionsIf } from '@shared/generic-form/generic-form/decorators/if';
import { GenericFormDecoratorError } from '@shared/generic-form/generic-form/generic-form.error';
import { validate } from '@shared/generic-form/validator';
import { deleteAllControlMetadatas } from './utils.spec';

@Injectable()
@Group()
class Form extends GenericFormGroup<any> {

  @Control()
  public username: TextFormControl = undefined as any;

  @Control()
  public password: TextFormControl = undefined as any;

  @Control()
  public email: TextFormControl = undefined as any;

  @Control()
  public select: SelectFormControl = undefined as any;


}

const DEFAULT_VALUE = 'default';

@Injectable()
@Group()
class DefaultIfForm extends GenericFormGroup<any> {

  @Control()
  @DefaultIf(function () {
    return DEFAULT_VALUE;
  })
  public username: TextFormControl = undefined as any;

}

describe('Generic Form', () => {

  describe('Decorators', () => {

    xdescribe('If', () => {

      afterEach(() => deleteAllControlMetadatas(Form));

      let form: Form;

      beforeEach(() => {
        TestBed.configureTestingModule({providers: [Form, DefaultIfForm]});
        form = TestBed.get(Form);
      });

      function testReflectMetadata(decoratorFactory, metadataKey) {
        it('Reflect metadata', () => {

          const fnc = () => {
          };

          const changeTrigger = ['password', 'username'];

          const dec = decoratorFactory(fnc, ...changeTrigger);

          dec(Form.prototype, 'username');

          expect(Reflect.hasMetadata(metadataKey, Form.prototype, 'username')).true;
          expect(Reflect.getMetadata(metadataKey, Form.prototype, 'username').fnc).eq(fnc);
          expect(Reflect.getMetadata(metadataKey, Form.prototype, 'username').changeTrigger).to.deep
            .eq(changeTrigger);

        });

        xit('Reflect metadata - use (this) control name as changeTrigger', () => {

          const fnc = () => {
          };

          const changeTrigger = ['username', 'password', 'username'];

          const dec = decoratorFactory(fnc, ...changeTrigger);

          try {
            dec(Form.prototype, 'username');
            expect.fail('if the (this) control name is passed as changeTrigger an exception should be thrown');
          } catch (e) {
            expect(e).to.be.instanceOf(GenericFormDecoratorError);
          }

        });
      }

      function testCalled(decoratorFactory, spyFnc = spy()) {
        it('called', () => {

          const dec = decoratorFactory(spyFnc);

          dec(Form.prototype, 'username');

          form.init();

          expect(
            spyFnc.calledOnce,
            `after init fnc should only be called once, instead called '${spyFnc.callCount}'`,
          ).true;

          form.username.setValue('username');

          expect(
            spyFnc.calledTwice,
            `after username control changed fnc should only be called twice, instead called '${spyFnc.callCount}'`,
          ).true;


        });
      }

      function testInjectChangeTriggers(decoratorFactory, spyFnc = spy()) {
        it('inject change triggers', () => {

          const dec = decoratorFactory(spyFnc, 'password', 'email');

          dec(Form.prototype, 'username');

          form.init();

          expect(
            spyFnc.calledOnce,
            `after init fnc should only be called once, instead called '${spyFnc.callCount}'`,
          ).true;

          form.password.setValue('password');

          expect(
            spyFnc.calledTwice,
            `after password control changed fnc should only be called twice, instead called '${spyFnc.callCount}'`,
          ).true;
          expect(spyFnc.args[1][0], `the first parameter should be the instance of the password control`)
            .eq(form.password);
          expect(spyFnc.args[1][1], `the second parameter should be the instance of the email control`)
            .eq(form.email);

          form.email.setValue('email');

          expect(
            spyFnc.calledThrice,
            `after password control changed fnc should only be called thrice, instead called '${spyFnc.callCount}'`,
          ).true;
          expect(spyFnc.args[2][0], `the first parameter should be the instance of the password control`)
            .eq(form.password);
          expect(spyFnc.args[2][1], `the second parameter should be the instance of the email control`)
            .eq(form.email);

        });
      }

      for (const item of [
        {
          decoratorFactory: AddIf,
          metadataKey:      ADD_IF,
          prop:             'Add',
        }, {
          decoratorFactory: RemoveIf,
          metadataKey:      REMOVE_IF,
          prop:             'Remove',
        },
      ]) {

        describe(`@${item.prop}If`, () => {

          testReflectMetadata(item.decoratorFactory, item.metadataKey);

          testInjectChangeTriggers(
            AddIf,
            stub().throwsException(item.decoratorFactory === AddIf ? 'ValidationError' : 'Error'),
          );

          it('called', () => {

            const stubFnc = stub().throwsException(item.decoratorFactory === AddIf ? 'ValidationError' : 'Error');

            const dec = item.decoratorFactory(stubFnc, 'username');

            dec(Form.prototype, 'username');

            form.init();

            expect(
              stubFnc.calledOnce,
              `after init fnc should only be called once, instead called '${stubFnc.callCount}'`,
            ).to.be.true;

            form.username.setValue('username');

            expect(
              stubFnc.calledThrice,
              `the control username is not added therefore the fnc should be called thrice, instead called '${stubFnc.callCount}'`,
            ).to.be.true;

          });

          it('action', () => {
            const dec = item.decoratorFactory(function () {
              validate(this).eq('username');
            });

            dec(Form.prototype, 'username');

            form.init();

            expect(
              Object.keys(form.controls),
              `only the password and email control should be added, instead '${Object.keys(form.controls)
                .join(',')}' are added`,
            ).have.length(item.decoratorFactory === AddIf ? 3 : 4);

            form.username.setValue('username');

            expect(
              Object.keys(form.controls),
              `all controls should now be added, instead only '${Object.keys(form.controls)
                .join(',')}' are added`,
            ).have.length(item.decoratorFactory === AddIf ? 4 : 3);

          });

        });

      }

      for (const item of [
        {
          decoratorFactory: ReadonlyIf,
          metadataKey:      READONLY_IF,
          prop:             'Readonly',
        },
        {
          decoratorFactory: DisabledIf,
          metadataKey:      DISABLED_IF,
          prop:             'Disabled',
        },
      ]) {

        describe(`@${item.prop}If`, () => {

          testReflectMetadata(item.decoratorFactory, item.metadataKey);

          testCalled(item.decoratorFactory);

          testInjectChangeTriggers(item.decoratorFactory);

          it('action', () => {

            const dec = item.decoratorFactory(function () {
              validate(this).eq('username');
            });

            dec(Form.prototype, 'username');

            form.init();

            expect(
              form.username[`is${item.prop}`],
              `after init the username control should **not** be ${item.prop.toLowerCase()}`,
            ).false;

            form.username.setValue('username');

            expect(
              form.username[`is${item.prop}`],
              `after username control changed the username control should be ${item.prop.toLowerCase()}`,
            ).true;

          });

        });

      }

      for (const item of [
        {
          decoratorFactory: PlaceholderIf,
          metadataKey:      PLACEHOLDER_IF,
          prop:             'Placeholder',
        }, {
          decoratorFactory: LabelIf,
          metadataKey:      LABEL_IF,
          prop:             'Label',
        },
      ]) {

        describe(`@${item.prop}If`, () => {

          testReflectMetadata(item.decoratorFactory, item.metadataKey);

          testCalled(item.decoratorFactory, stub().returns(''));

          testInjectChangeTriggers(item.decoratorFactory, stub().returns(''));

          it('action', () => {

            const dec = item.decoratorFactory(function () {
              if (this.value === 'username') {
                return 'username';
              } else {
                return 'str';
              }
            });

            dec(Form.prototype, 'username');

            form.init();

            expect(form.username[`${item.prop.toLowerCase()}`]).eq('str');

            form.username.setValue('username');

            expect(form.username[`${item.prop.toLowerCase()}`]).eq('username');

          });

        });

      }

      describe(`@OptionsIf`, () => {

        // TODO : refactor tests when option parameter is also used in other 'if' decorators
        it('Reflect metadata', () => {

          const fnc = () => {
          };

          const changeTrigger = ['password', 'username'];

          const dec = OptionsIf(fnc as any, {}, ...changeTrigger);

          dec(Form.prototype, 'username');

          expect(Reflect.hasMetadata(OPTIONS_IF, Form.prototype, 'username')).true;
          expect(Reflect.getMetadata(OPTIONS_IF, Form.prototype, 'username').fnc).eq(fnc);
          expect(Reflect.getMetadata(OPTIONS_IF, Form.prototype, 'username').changeTrigger).to.deep
            .eq(changeTrigger);

        });

        it('called', () => {

          const spyFnc = stub().returns(['']);

          const dec = OptionsIf(spyFnc);

          dec(Form.prototype, 'select');

          form.init();

          expect(
            spyFnc.calledOnce,
            `after init fnc should only be called once, instead called '${spyFnc.callCount}'`,
          ).true;

          form.select.setValue('any');

          expect(
            spyFnc.calledTwice,
            `after select control changed fnc should only be called twice, instead called '${spyFnc.callCount}'`,
          ).true;


        });

        it('inject change triggers', () => {

          const spyFnc = stub().returns(['']);

          const dec = OptionsIf(spyFnc, {}, 'password', 'email');

          dec(Form.prototype, 'select');

          form.init();

          expect(
            spyFnc.calledOnce,
            `after init fnc should only be called once, instead called '${spyFnc.callCount}'`,
          ).true;

          form.password.setValue('password');

          expect(
            spyFnc.calledTwice,
            `after password control changed fnc should only be called twice, instead called '${spyFnc.callCount}'`,
          ).true;
          expect(spyFnc.args[1][0], `the first parameter should be the instance of the password control`)
            .eq(form.password);
          expect(spyFnc.args[1][1], `the second parameter should be the instance of the email control`)
            .eq(form.email);

          form.email.setValue('email');

          expect(
            spyFnc.calledThrice,
            `after password control changed fnc should only be called thrice, instead called '${spyFnc.callCount}'`,
          ).true;
          expect(spyFnc.args[2][0], `the first parameter should be the instance of the password control`)
            .eq(form.password);
          expect(spyFnc.args[2][1], `the second parameter should be the instance of the email control`)
            .eq(form.email);

        });

        it('action default', () => {

          const dec = OptionsIf(function (username) {
            if (username.value) {
              return [username.value];
            } else {
              return [];
            }
          }, {}, 'username');

          dec(Form.prototype, 'select');

          form.init();

          expect(form.select.options).to.have.length(0);

          form.username.setValue('username');

          expect(form.select.options).to.have.length(1);
          expect(form.select.options[0]).to.be.not.undefined;
          expect(form.select.options[0].value).to.eq(form.username.value);

        });

        it('action skip self', () => {

          const stubFnc = stub().returns(['']);

          const dec = OptionsIf(stubFnc, { skipSelf: true }, 'username');

          dec(Form.prototype, 'select');

          form.init();

          expect(stubFnc.calledOnce).true;

          form.select.setValue('any');

          expect(stubFnc.calledOnce).true;

          form.username.setValue('username');

          expect(stubFnc.calledTwice).true;
          expect(stubFnc.args[0][0]).to.eq(form.username);
          expect(stubFnc.args[1][0]).to.eq(form.username);

        });

        it('action trigger once', () => {

          const stubFnc = stub().returns(['']);

          const dec = OptionsIf(stubFnc, { triggerOnce: true }, 'username');

          dec(Form.prototype, 'select');

          form.init();

          expect(stubFnc.calledOnce).true;

          form.username.setValue('username');

          expect(stubFnc.calledTwice).true;

          form.username.setValue('username');

          expect(stubFnc.calledTwice).true;

          form.username.setValue('username');

          expect(stubFnc.calledTwice).true;

        });

        it('action skip init call', () => {

          const stubFnc = stub().returns(['']);

          const dec = OptionsIf(stubFnc, { skipInitCall: true }, 'username');

          dec(Form.prototype, 'select');

          form.init();

          expect(stubFnc.called).false;

          form.username.setValue('username');

          expect(stubFnc.calledOnce).true;

          form.username.setValue('username');

          expect(stubFnc.calledTwice).true;

        });

      });

      describe(`@RequiredIf`, () => {

        xit('Reflect metadata', () => { // TOODO BM

          const fnc = () => {
          };

          const changeTrigger = ['password', 'username'];

          const dec = RequiredIf(fnc as any, 'my error', ...changeTrigger);

          dec(Form.prototype, 'username');

          expect(Reflect.hasMetadata(OPTIONS_IF, Form.prototype, 'username')).true;
          expect(Reflect.getMetadata(OPTIONS_IF, Form.prototype, 'username').fnc).eq(fnc);
          expect(Reflect.getMetadata(OPTIONS_IF, Form.prototype, 'username').changeTrigger).to.deep
            .eq(changeTrigger);
        });

      });

      describe('@DefaultIf', () => {

        let testForm: DefaultIfForm;

        beforeEach(() => {
          testForm = TestBed.get(DefaultIfForm);
        });

        it('control has not a value', () => {

          testForm.init();

          expect(testForm.username.value).eq(DEFAULT_VALUE);

        });

        it('control has a value', () => {

          testForm.init({username: 'username'});

          expect(testForm.username.value).not.eq(DEFAULT_VALUE);

        });


      });

    });

  });

});

