/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { DateFormControl } from '@shared/generic-form/controls';
import { expect } from 'chai';
import { SetDefaultDateFormat } from '@shared/generic-form/validator/asserts/date';
import { TestBed } from '@angular/core/testing';

describe('Generic Form', () => {

  describe('Controls', () => {

    describe('Date', () => {

      beforeEach(() => {
        SetDefaultDateFormat('DD.MM.YYYY');
        TestBed.configureTestingModule({providers: [DateFormControl]})
      });

      it('setModelValue', () => {

        const form: DateFormControl = TestBed.get(DateFormControl);

        form.setModelValue('2018-04-02T00:00:00.000Z');

        expect(form.value).to.eq('02.04.2018');

      });

      it('getModelValue', () => {

        const form: DateFormControl = TestBed.get(DateFormControl);
        form.setValue('02.04.2018');

        expect(form.getModelValue()).to.eq('2018-04-02T00:00:00.000Z');

      });

    });

  });

});