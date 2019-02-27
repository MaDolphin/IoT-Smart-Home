/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { TestBed } from '@angular/core/testing';
import 'rxjs/add/observable/of';
import { Observable } from 'rxjs/Observable';
import { MoneyControl } from '@shared/architecture/forms/controls/money.control';
import { NumberToMoneyPipe } from '@shared/pipes/number-to-money.pipe';
import { MoneyToNumberPipe } from '@shared/pipes/money-to-number.pipe';
import { FormControlHelper } from '@testutils/form-control';
import { A } from '@testutils/mocking';
import { loadArchitectureModule } from '@testutils/utilities';

describe('Architecture', () => {
  describe('Forms', () => {
    describe('Controls', () => {
      describe('Money Control', () => {

        beforeEach(loadArchitectureModule);

        it(
          'onChange',
          () => FormControlHelper.ChangeTest(FormControlHelper.CreateMoneyFormControl(), '1.500,90', '1.200,80'),
        );

        it('handelChange', () => {
          let control = FormControlHelper.CreateMoneyFormControl();
          control.onValidChange.subscribe(value => expect(value).toEqual(1200033));
          control.setValue('12.000,33');
        });

        it(
          'required without additional validators',
          () => FormControlHelper.RequiredTest(FormControlHelper.CreateMoneyFormControl(), '1.500,90'),
        );

        it(
          'required with additional validators',
          () => FormControlHelper.RequiredTest(FormControlHelper.CreateMoneyFormControl(() => null), '1.500,90'),
        );

        it(
          'default Validator checks',
          () => FormControlHelper.ValidatorTest(
            FormControlHelper.CreateMoneyFormControl(),
            ['1.000,50'],
            ['1,000.50']
          ),
        );

        it('moneyValue', () => {

          const mfc = FormControlHelper.CreateMoneyFormControl();

          expect(mfc.moneyValue).toBeNull();

          const numberInputs: number[] = [0, 1.5, 100.4];
          const stringInputs: string[] = ['0', '1.5', '100.4'];
          const numberToMoney = TestBed.get(NumberToMoneyPipe);
          const moneyToNumber = TestBed.get(MoneyToNumberPipe);

          for (const num of numberInputs) {
            mfc.setValue(num);

            expect(mfc.moneyValue).toEqual(
              moneyToNumber.transform(numberToMoney.transform(num)),
              `${num} should be saved as ${mfc.moneyValue}`
            );
          }

          for (const str of stringInputs) {
            mfc.setValue(str);

            expect(mfc.moneyValue).toEqual(
              moneyToNumber.transform(str),
              `${str} should be saved as ${mfc.moneyValue}`
            );
          }

          mfc.setValue('invalid input');
          expect(mfc.moneyValue).toEqual(0);

        });

        // if the function setValue is used
        // the validator will not be triggerd
        it('IsMoney valid', () => {

          const validInputs: string[] = [
            '0,00',
            '1',
            '1,0',
            '1,00',
            '1,05',
            '1.000,5',
            '1.000,50',
          ];

          for (const input of validInputs) {
            expect(MoneyControl.IsMoney({value: input} as any)).toBeNull(`${input} is valid`);
          }

        });

        it('IsMoney invalid', () => {

          const invalidInputs: string[] = [
            '0.0',
            '0.00',
            '1.0',
            '1.00',
            '1.05',
            '1,000.5',
            '1,000.50',
            'sdf',
          ];

          for (const input of invalidInputs) {
            expect(MoneyControl.IsMoney({value: input} as any)).not.toBeNull(`${input} is invalid`);
          }

        });

        it('balance empty', () => {

          const mfc = FormControlHelper.CreateMoneyFormControl();

          expect(mfc.balance).toBeNull();

        });

        it('balance sync', () => {

          const mfc0 = FormControlHelper.CreateMoneyFormControl(() => null);

          expect(mfc0.balance).toBeNull();

          const sub: number = 100;

          const mfc1 = FormControlHelper.CreateMoneyFormControl(() => sub);

          expect(mfc1.balance).toEqual(-sub as any);

          const moneyInput: number[] = [
            100,
            4,
            4.34,
            5692.34,
            320123,
          ];

          for (const money of moneyInput) {

            mfc1.setValue(money * 100);
            expect(mfc1.balance).toEqual((money * 100 - sub) as any, `${money} - ${sub} = ${money - sub}`);

          }

        });

        it('balance async', A(async () => {

          const mfc0 = FormControlHelper.CreateMoneyFormControl(() => Observable.of(null));

          if (mfc0.balance) {
            expect(await mfc0.balance.toPromise()).toBeNull();
          } else {
            throw new Error('balance is null');
          }

          const sub: number = 100;

          const mfc1 = FormControlHelper.CreateMoneyFormControl(() => Observable.of(sub));

          if (mfc1.balance) {
            expect(await mfc1.balance.toPromise()).toEqual(-sub);
          } else {
            throw new Error('balance is null');
          }

          const moneyInput: number[] = [
              100,
              4,
              4.34,
              5692.34,
              320123,
          ];

          for (const money of moneyInput) {

              mfc1.setValue(money * 100);

              expect(mfc1.balance).not.toBeNull();
              expect(mfc1.balance instanceof Observable).toBeTruthy();

              if (mfc1.balance === null) {
                  throw new Error('compiler null fix');
              }

              expect(await mfc1.balance.toPromise()).toEqual(money * 100 - sub, `${money} - ${sub} = ${money - sub}`);

          }

        }));

      });

    });
  });

});
