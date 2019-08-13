/* (c) https://github.com/MontiCore/monticore */

import { FormControl } from '@angular/forms';
import { expect } from 'chai';
import { spy } from 'sinon';
import { validate, ValidationError } from '@shared/generic-form/validator';
import { before } from '@shared/generic-form/validator/asserts';
import { SetDefaultDateFormat } from '@shared/generic-form/validator/asserts/date';

describe('Generic Form', () => {

  describe('Validators', () => {

    describe('Before', () => {

      let control: FormControl;

      beforeEach(() => {
        control = new FormControl(null);
        SetDefaultDateFormat('DD.MM.YYYY');
      });

      it('Invalid', () => {
        control.setValue('12.12.2012');
        const assert = spy();
        before.apply({ _obj: control, assert }, ['12.12.2010']);
        expect(assert.calledOnce).to.be.true;
        expect(assert.args[0][0]).to.be.false;
      });

      it('Valid', () => {
        control.setValue('12.12.2010');
        const assert = spy();
        before.apply({ _obj: control, assert }, ['12.12.2012']);
        expect(assert.calledOnce).to.be.true;
        expect(assert.args[0][0]).to.be.true;
      });

      it('is.before invalid', () => {
        control.setValue('12.12.2030');
        try {
          validate(control).is.before(new Date(Date.now()));
          expect.fail('not throw', 'to throw');
        } catch (e) {
          expect(e).instanceOf(ValidationError);
        }
      });

      it('is.before valid', () => {
        control.setValue('12.12.2012');
        try {
          validate(control).is.before(new Date(Date.now()));
        } catch (e) {
          expect.fail('throwed', 'not to throw', e);
        }
      });

    });

  });

});
