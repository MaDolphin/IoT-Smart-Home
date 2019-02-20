import { FormControl } from '@angular/forms';
import { expect } from 'chai';
import { spy } from 'sinon';
import { validate, ValidationError } from '@shared/generic-form/validator';
import { assertDate } from '@shared/generic-form/validator/asserts';
import { SetDefaultDateFormat } from '@shared/generic-form/validator/asserts/date';

describe('Generic Form', () => {

  describe('Validators', () => {

    describe('Date', () => {

      let control: FormControl;

      beforeEach(() => {
        control = new FormControl(null);
        SetDefaultDateFormat('DD.MM.YYYY');
      });

      it('Invalid date formats', () => {
        const formatInvalidDates = [
          'invalid',
          '201',
          '2',
          '2010',
          '2010-',
          '2010-05',
          '2010-0',
          '2010-05-1',
          '11.05.20211',
          '201-255-10',
        ];

        for (const dateString of formatInvalidDates) {
          control.setValue(dateString);
          const assert = spy();
          assertDate.apply({ _obj: control, assert });
          expect(assert.calledTwice).to.be.true;
          expect(assert.args[0][0], `'${dateString}' is in incorrect format`).to.be.false;
          expect(assert.args[1][0], `'${dateString}' is not a valid date`).to.be.false;
        }

      });

      it('Invalid dates', () => {
        const invalidDates = [
          '01.13.2010',
          '35.01.2018',
        ];

        for (const dateString of invalidDates) {
          control.setValue(dateString);
          const assert = spy();
          assertDate.apply({ _obj: control, assert });
          expect(assert.calledTwice).to.be.true;
          expect(assert.args[1][0], `'${dateString}' is not a valid date`).to.be.false;
        }
      });

      it('Invalid leap year', () => {
        control.setValue('29.02.2011');
        const assert = spy();
        assertDate.apply({ _obj: control, assert });
        expect(assert.calledTwice).to.be.true;
        expect(assert.args[1][0]).to.be.false;
      });

      it('Valid leap year', () => {
        control.setValue('29.02.2012');
        const assert = spy();
        assertDate.apply({ _obj: control, assert });
        expect(assert.calledTwice).to.be.true;
        expect(assert.args[0][0]).to.be.true;
        expect(assert.args[1][0]).to.be.true;
      });

      it('Valid', () => {
        control.setValue('12.12.2010');
        const assert = spy();
        assertDate.apply({ _obj: control, assert });
        expect(assert.calledTwice).to.be.true;
        expect(assert.args[0][0]).to.be.true;
        expect(assert.args[1][0]).to.be.true;
      });

      it('is.date invalid', () => {
        control.setValue('invalid');
        try {
          validate(control).is.date;
          expect.fail('not throw', 'to throw');
        } catch (e) {
          expect(e).instanceOf(ValidationError);
        }
      });

      it('is.date valid', () => {
        control.setValue('12.12.2012');
        try {
          validate(control).is.date;
        } catch (e) {
          expect.fail('throwed', 'not to throw', e);
        }
      });

    });

  });

});
