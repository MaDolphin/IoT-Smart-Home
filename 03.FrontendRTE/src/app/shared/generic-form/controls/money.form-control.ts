/* (c) https://github.com/MontiCore/monticore */

import { DecimalPipe } from '@angular/common';
import { Injectable } from '@angular/core';
import { MoneyToNumberPipe } from '@shared/pipes/money-to-number.pipe';
import { NumberToMoneyPipe } from '@shared/pipes/number-to-money.pipe';
import { AddValidator } from '@shared/generic-form/generic-form/decoretors/validator';
import { GenericFormControl } from '@shared/generic-form/generic-form';
import { IGenericFormGroup } from '@shared/generic-form/generic-form/generic-form-group.interface';
import { validate } from '../validator';

@Injectable()
export class MoneyFormControl<G extends IGenericFormGroup = IGenericFormGroup> extends GenericFormControl<string, G> {

  private _moneyToNumberPipe = new MoneyToNumberPipe();
  private _numberToMoneyPipe = new NumberToMoneyPipe(new DecimalPipe('de'));

  public getModelValue(): any {
    return this._moneyToNumberPipe.transform(this.value);
  }

  public setModelValue(modelValue: string) {
    this.setValue(this._numberToMoneyPipe.transform(this._moneyToNumberPipe.transform(modelValue)));
  }

  reset(formState: string | null = null, options?: {
    onlySelf?: boolean;
    emitEvent?: boolean;
  }): void {
    super.reset(formState, options);
    this.setModelValue('');
  }

  public updateMoney() {
    this.setModelValue(this.value);
  }

  @AddValidator
  public isMoneyValidator() {
    validate(this).is.money;
  }

}
