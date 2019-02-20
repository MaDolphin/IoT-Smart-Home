import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { Observable } from 'rxjs/Observable';
import createNumberMask from 'text-mask-addons/dist/createNumberMask';
import { MoneyToNumberPipe } from '@sharedpipes/money-to-number.pipe';
import { NumberToMoneyPipe } from '@sharedpipes/number-to-money.pipe';
import { FormularControl } from './formular.control';

export const NUMBER_MASK = createNumberMask({
  prefix: '',
  suffix: '',
  thousandsSeparatorSymbol: '.',
  allowDecimal: true,
  decimalSymbol: ',',
  integerLimit: 10,
  modelClean: true
});

export const NEGATIVE_NUMBER_MASK = createNumberMask({
  prefix: '',
  suffix: '',
  thousandsSeparatorSymbol: '.',
  allowNegative: true,
  allowDecimal: true,
  decimalSymbol: ',',
  integerLimit: 10,
  modelClean: true
});

export type Balance = number | Observable<number | null> | null;

export class MoneyControl<B extends Balance = Observable<number | null>> extends FormularControl<string, number> {

  public static IsMoney(control: AbstractControl): ValidationErrors | null {

    const value: string = control.value ? control.value.trim() : '';

    if (value) {

      if (!!value.match(/^[+-]?[0-9]{1,3}(?:\.?[0-9]{3})*(?:,[0-9]{0,2})?$/)) {
        return null;
      } else {
        return {error: 'Bitte einen korrekten Betrag eingeben'};
      }
    }

    return null;
  }

  public get balance(): B | null {
    let balance: B | null = null;
    if (this.subMoneyValue) {
      const subMoneyValue = this.subMoneyValue();
      const moneyValue: number = (this.moneyValue !== null ? this.moneyValue : 0);
      if (subMoneyValue instanceof Observable) {
        balance = subMoneyValue.map((sub: number | null) => {
          return sub !== null ?
            moneyValue - sub :
            null;
        }) as any;
      } else {
        balance = (subMoneyValue !== null ?
          moneyValue - subMoneyValue :
          null) as any;
      }
    }

    return balance;
  }

  public get moneyValue(): number | null {
    return this._moneyValue;
  }

  private _moneyValue: number | null = null;

  public constructor(formState: string | null = null,
                     private _numberToMoney: NumberToMoneyPipe,
                     private _moneyToNumber: MoneyToNumberPipe,
                     public subMoneyValue: (() => number | Observable<number | null> | null) | null = null,
                     ...validator: ValidatorFn[]) {
    super(formState, MoneyControl.IsMoney, ...validator);
    // call handelChange so that the moneyValue member is set if a budget is passed with the
    // constructor
    this.handelChange(formState);
    this.logger.addFlag('budget');
  }

  // region overwrites

  public setValue(value: number | string | null): void {
    value = this.handelValueSet(value);
    if (this.value !== value) {
      super.setValue(value);
    }
  }

  public patchValue(value: number | string | null): void {
    value = this.handelValueSet(value);
    if (this.value !== value) {
      super.patchValue(value);
    }
  }

  // endregion

  protected handelChange(value: string | null): number | null {
    if (value) {
      this._moneyValue = this._moneyToNumber.transform(value);
    } else {
      this._moneyValue = null;
    }
    return this._moneyValue;
  }

  private handelValueSet(value: number | string | null): string {
    const temp = value;
    if (typeof value === 'number') {
      value = this._numberToMoney.transform(value);
    }
    console.log({temp, value});
    return value || '';
  }

  public updateMoney() {
    this.patchValue(this._numberToMoney.transform(this._moneyToNumber.transform(this.value)));
  }
/*
  public setValidators(v:ValidatorFn[]){
    let validators:ValidatorFn[] = v;
    validators.push(MoneyControl.IsMoney);
    super.setValidators(validators);
  }
  */
}
