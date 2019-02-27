/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { FormControlHelper } from '@testutils/form-control';
import { AutoCompleteControl } from '@shared/architecture/forms/controls/auto-complete.control';

describe('Architecture', () => {
  describe('Forms', () => {
    describe('Controls', () => {
      describe('Auto Complete Control', () => {

        // Simple polyfill for 'includes' method. Available polyfill package is somewhat buggy.
        beforeAll(() => {
          if (!Array.prototype.includes) {
            Array.prototype.includes = function() {'use strict';
              return Array.prototype.indexOf.apply(this, arguments) !== -1;
            };
          }
        });

        it('should accept simple',
          () => FormControlHelper.RequiredTest(
            new AutoCompleteControl<string>('Drittmittelprojekt', ['Drittmittelprojekt']), 'Drittmittelprojekt'),
        );

        it('should accept multiple',
          () => FormControlHelper.RequiredTest(
            new AutoCompleteControl<string>('Drittmittelprojekt', ['DFG', 'Drittmittelprojekt']), 'Drittmittelprojekt'),
        );

        it('Default value change check', () => {

          const options = ['a', 'b', 'c', 'd'];
          const validValues = ['a', 'c'];
          const invalidValues = ['x', 'y'];

          let control = new AutoCompleteControl('', options);

          for (const valid of validValues) {
            control.setValue(valid);
            expect(control.valid).toBeTruthy(`${valid} should be a valid value`);
          }

          let res;
          for (const invalid of invalidValues) {
            control.setValue(invalid);
            expect(control.invalid).toBeTruthy(`${invalid} should be a invalid value`);
          }

        });

        it('IsContainInListFactory validator check', () => {

          const options = ['a', 'b', 'c', 'd'];
          const validValues = ['a', 'c'];
          const invalidValues = ['x', 'y'];

          let control = new AutoCompleteControl('', options);

          for (const valid of validValues) {
            control.setValue(valid);
            expect((AutoCompleteControl.IsContainInListFactory(true, () => options))(control))
              .toBeNull(`${valid} should be a valid value`);
          }

          let res;
          for (const invalid of invalidValues) {
            control.setValue(invalid);
            res = (AutoCompleteControl.IsContainInListFactory(true, () => options))(control);
            expect(res).not.toBeNull(`${invalid} should be an invalid value`);
            if (res && res.error) {
              expect(res.error).toBe('Fehlerhafte Eingabe', `${invalid} should be an invalid value`);
            }
          }

        });

        it('filteredOptions check', () => {

          const options = ['aa', 'bb', 'ab', 'ba'];

          const input = 'b';
          let control = new AutoCompleteControl(input, options);

          for (const option of control.filteredOptions) {
            expect(['bb', 'ba']).toContain(option);
            expect(['aa', 'ab', 'b']).not.toContain(option);
          }

        });

      });
    });
  });
});