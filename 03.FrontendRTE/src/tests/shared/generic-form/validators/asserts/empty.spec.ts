/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { FormControl } from '@angular/forms';
import { expect } from 'chai';
import { validate } from '@shared/generic-form/validator';
import { assertEmpty } from '@shared/generic-form/validator/asserts';

describe('Generic Form', () => {

  describe('Validators', () => {

    describe('Empty', () => {

      let control: FormControl;

      beforeEach(() => {
        control = new FormControl(null);
      });

      it('Invalid', () => {
        expect(() => assertEmpty.apply({ _obj: control })).to.throw;
      });

      it('Valid', () => {
        control.setValue('val');
        expect(() => assertEmpty.apply({ _obj: control })).not.to.throw;
      });

      it('is.empty', () => {
        expect(() => validate(control).is.empty).to.throw;
        control.setValue('val');
        expect(() => validate(control).is.empty).to.not.throw;
      });

      it('is.not.empty', () => {
        expect(() => validate(control).is.not.empty).to.not.throw;
        control.setValue('val');
        expect(() => validate(control).is.not.empty).to.throw;
      });

    });

  });

});
