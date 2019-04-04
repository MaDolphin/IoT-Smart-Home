/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

/*
import { Injectable } from '@angular/core';
import { Control, GenericFormControl, GenericFormGroup, Validator } from '@shared/generic-form/ngx-forms';
import { CheckBoxFormControl, DateFormControl } from '@shared/generic-form/controls';
import { TestBed } from '@angular/core/testing';
import { validate } from '@shared/generic-form/validator';
import { expect } from 'chai';
import { SetDefaultDateFormat } from '@shared/generic-form/validator/asserts/date';
import { AddValidator } from '@shared/generic-form/generic-form/decoretors/validator';
import { ADD_VALIDATOR } from '@shared/generic-form/generic-form/config';
import { Group } from '@shared/generic-form/generic-form/decoretors';
import moment = require('moment');

@Injectable()
@Group()
class FormValidator extends GenericFormGroup<any> {

  @Control()
  @Validator(function () {
    validate(this).is.after(moment('2000-01-01').toDate());
  })

  @Validator(function (endDate: DateFormControl) {
    validate(this).is.before(endDate);
  }, 'endDate')
  public startDate: DateFormControl = undefined as any;

  @Control()
  public after2018: CheckBoxFormControl = undefined as any;

  @Control()
  @Validator(function (after2018: CheckBoxFormControl) {
    if (after2018.value) {
      validate(this).is.after(moment('2019-01-01').toDate());
    }
  }, 'after2018')
  @Validator(function (startDate: DateFormControl) {
    validate(this).is.after(startDate);
  }, 'startDate')
  public endDate: DateFormControl = undefined as any;

}

describe('Generic Form', () => {

  describe('Decorator', () => {

    describe('@Validator', () => {

      beforeAll(() => SetDefaultDateFormat('DD.MM.YYYY'));

      afterAll(() => SetDefaultDateFormat('YYYY-MM-DD'));

      beforeEach(() => {
        TestBed.configureTestingModule({providers: [FormValidator]});
      });

      it('is start date after 01.01.2000', () => {

        const form: FormValidator = TestBed.get(FormValidator).init();

        form.startDate.updateValueAndValidity();

        expect(form.startDate.valid, 'initial').true;

        form.startDate.setValue('01.01.1999');

        form.startDate.updateValueAndValidity();

        expect(form.startDate.invalid, '01.01.1999 is before 01.01.2001').true;

        form.startDate.setValue('01.01.2001');

        form.startDate.updateValueAndValidity();

        console.log(form.startDate.errors);

        expect(form.startDate.valid, '01.01.2001 is after 01.01.2000').true;

      });

      it('circular validator - start before end and end after start', () => {

        const form: FormValidator = TestBed.get(FormValidator).init();

        form.startDate.updateValueAndValidity();
        form.endDate.updateValueAndValidity();

        expect(form.startDate.valid, 'initial').true;
        expect(form.endDate.valid, 'initial').true;

        form.startDate.setValue('01.01.2010');
        form.endDate.setValue('01.01.2011');

        form.startDate.updateValueAndValidity();
        form.endDate.updateValueAndValidity();

        expect(form.startDate.valid, '01.01.2010 is before 01.01.2011').true;
        expect(form.endDate.valid, '01.01.2011 is after 01.01.2010').true;

        form.startDate.setValue('01.01.2015');

        form.startDate.updateValueAndValidity();
        form.endDate.updateValueAndValidity();

        expect(form.startDate.invalid, '01.01.2015 is not before 01.01.2011').true;
        expect(form.endDate.invalid, '01.01.2011 is not after 01.01.2015').true;

        form.endDate.setValue('01.01.2018');

        form.startDate.updateValueAndValidity();
        form.endDate.updateValueAndValidity();

        expect(form.startDate.valid, '01.01.2015 is before 01.01.2018').true;


      });


    });

    describe('@AddValidator', () => {

      class TestFormControl extends GenericFormControl<any> {

        @AddValidator
        public testValidator() {
        }

      }

      it('add validator method to metadata', () => {

        expect(Reflect.hasMetadata(ADD_VALIDATOR, TestFormControl.prototype), 'Form Control prototype should have an add-validator entry').true;

        expect(Reflect.getMetadata(ADD_VALIDATOR, TestFormControl.prototype), 'The length should be one').to.have.length(1);

        expect(Reflect.getMetadata(ADD_VALIDATOR, TestFormControl.prototype)[0], 'And the value should be eq the method pointer').eq(TestFormControl.prototype.testValidator);

      });

      it('add validator method to validator function array', () => {

        const control = new TestFormControl();

        expect(control.validatorFunctions).have.length(1);

        expect(control.validatorFunctions[0]).eq(control.testValidator);

      })

    });

  });

});
*/