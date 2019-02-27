/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { TestBed } from '@angular/core/testing';
import { FormularControl } from '@shared/architecture/forms/controls/formular.control';
import { DateControl } from '@shared/architecture/forms/controls/date.control';
import { DateToStringPipe } from '@shared/pipes/date-to-string.pipe';
import { StringToDatePipe } from '@shared/pipes/string-to-date.pipe';
import { FormControlHelper } from '@testutils/form-control';
import { loadArchitectureModule } from '@testutils/utilities';

describe('Architecture', () => {
  describe('Forms', () => {
    describe('Controls', () => {
      describe('Date Control', () => {

        beforeEach(loadArchitectureModule);

        it(
          'onChange',
          () => FormControlHelper.ChangeTest(new DateControl(
                  '31.12.2017',
                  TestBed.get(StringToDatePipe),
                  TestBed.get(DateToStringPipe),
              ), '31.12.2017', '30.12.2016'),
        );

        it('handelChange', () => {
          let control = new DateControl(null, TestBed.get(StringToDatePipe), TestBed.get(DateToStringPipe));
          control.onValidChange.subscribe(value => {
            if (value) {
              expect(value.getTime()).toEqual(new Date(2000, 0, 12).getTime());
            } else {
              throw new Error('value should have been not null');
            }
          });
          control.setValue('12.01.2000');
        });

        it(
          'required without additional validators',
          () => FormControlHelper.RequiredTest(new DateControl(
                  null,
                  TestBed.get(StringToDatePipe),
                  TestBed.get(DateToStringPipe),
              ), '31.12.2017'),
        );

        it(
          'required with additional validators',
          () => FormControlHelper.RequiredTest(new DateControl(
                  null,
                  TestBed.get(StringToDatePipe),
                  TestBed.get(DateToStringPipe),
                  () => null,
              ), '31.12.2017'),
        );

        it(
          'default Validator checks',
          () => FormControlHelper.ValidatorTest(new DateControl(
            null,
            TestBed.get(StringToDatePipe),
            TestBed.get(DateToStringPipe),
          ), ['31.12.2017', '1.1.2000'], ['12.31.2017', '31.02.2000', null, '', undefined]),
        );

        it('IsDate validator check', () => {

          const validValues = ['31.12.2017', '1.1.2000', null, ''];
          const invalidDate = ['12.31.2017', '31.02.2000'];
          const invalidFormat = ['01.01.20'];

          let control = new FormularControl();

          for (const valid of validValues) {
            control.setValue(valid);
            expect(DateControl.IsDate(control)).toBeNull();
          }

          let res;
          for (const invalid of invalidDate) {
            control.setValue(invalid);
            res = DateControl.IsDate(control);
            expect(res).not.toBeNull(`${invalid} should be an invalid date`);
            if (res && res.error) {
              expect(res.error).toBe('Datum ist nicht korrekt', `${invalid} should be an invalid date`);
            }
          }

          for (const invalid of invalidFormat) {
            control.setValue(invalid);
            res = DateControl.IsDate(control);
            expect(res).not.toBeNull(`${invalid} should be a date with invalid format`);
            if (res && res.error) {
              expect(res.error).toBe(`Format: TT.MM.JJJJ`, `${invalid} should have an invalid format`);
            }
          }

        });

      });

    });
  });

});
