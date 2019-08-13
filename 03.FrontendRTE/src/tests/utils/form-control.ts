/* (c) https://github.com/MontiCore/monticore */

import { TestBed } from '@angular/core/testing';
import { AbstractControl, ValidatorFn } from '@angular/forms';
import { Observable } from 'rxjs/Observable';
import { FormularControl } from '@shared/architecture/forms/controls/formular.control';
import { Balance, MoneyControl } from '@shared/architecture/forms/controls/money.control';
import { MoneyToNumberPipe } from '@shared/pipes/money-to-number.pipe';
import { NumberToMoneyPipe } from '@shared/pipes/number-to-money.pipe';

export class FormControlHelper {

  public static ChangeTest(
    mfc: FormularControl<any>,
    validInput: any,
    invalidInput: any,
  ): void {

    mfc.setValidators((control: AbstractControl) => control.value === validInput ? null : { error: 'err' });

    let counter = 0;

    // onValidChange will execute if a value can be transformed into a truthy
    // value by handelChange function
    mfc.onValidChange.subscribe(() => counter++);

    const count = 10;

    for (let i = 0; i < count; i++) {
      mfc.setValue(validInput);
      mfc.setValue(invalidInput);
    }

    // number of calls depends on implementation of intermediate functions, such as setValue
    expect(counter).toBeGreaterThanOrEqual(
      2 * count,
      'the onValidChange function should be called if the value is set'
    );

  }

  public static RequiredTest(mfc: FormularControl<any>, input: any): void {

    mfc.updateValueAndValidity();
    mfc.markAsTouched();
    expect(mfc.valid).toBeTruthy();

    mfc.setValue(input);

    mfc.updateValueAndValidity();
    expect(mfc.valid).toBeTruthy();

    mfc.reset();

    mfc.required = true;

    mfc.updateValueAndValidity();
    expect(mfc.valid).toBeFalsy();

    mfc.setValue(input);

    mfc.updateValueAndValidity();
    expect(mfc.valid).toBeTruthy();

  }

  public static ValidatorTest(
    mfc: FormularControl<any>,
    validInputs: any[],
    invalidInputs: any[],
  ): void {

    for (const valid of validInputs) {
      mfc.setValue(valid);
      expect(mfc.valid).toBeTruthy(`expect '${valid}' to be an valid input`);
    }

    for (const invalid of invalidInputs) {
      mfc.setValue(invalid);
      expect(mfc.valid).toBeFalsy(`expect '${invalid}' to be an invalid input`);
    }

  }

  public static CreateMoneyFormControl<B extends Balance = Observable<number | null>>(
    subMoneyValue: (() => number | Observable<number | null> | null) | null = null,
    ...validator: ValidatorFn[]
  ): MoneyControl<B> {

    const mtn: MoneyToNumberPipe = TestBed.get(MoneyToNumberPipe);
    const ntm: NumberToMoneyPipe = TestBed.get(NumberToMoneyPipe);

    return new MoneyControl<B>(null, ntm, mtn, subMoneyValue, ...validator);

  }

}
