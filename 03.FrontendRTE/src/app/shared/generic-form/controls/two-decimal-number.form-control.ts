/* (c) https://github.com/MontiCore/monticore */

import { Injectable } from '@angular/core';
import { AddValidator } from '../generic-form/decoretors/validator';
import { IGenericFormGroup } from '../generic-form/generic-form-group.interface';
import { ValidationError } from '../validator';
import { PatternFormControl } from './pattern.form-control';

export const TWO_DECIMAL_NUMBER_REGEX = /^[0-9]+$|^[0-9]*(\.|,)[0-9]*$/;

@Injectable()
export class TwoDecimalNumberFormControl<G extends IGenericFormGroup = IGenericFormGroup> extends PatternFormControl<G> {

  public pattern = TWO_DECIMAL_NUMBER_REGEX;

  public getModelValue() {

    if (this.value && this.pattern.test(this.value)) {
      let value = this.value.toString().replace(',', '.');

      if (value === '.') {
        value = '0';
      }
      let result: number = Number(value) * 100;
      result = parseFloat(this.toFixed(result, 0));

      return result;

    } else {
      return 0;
    }
  }

  public setModelValue(value: number) {
    if (!!value) {
      this.setValue(this.toFixed(value / 100, 2).toString().replace('.', ','));
    }
  }

  @AddValidator
  public matchPatternValidator() {
    if (!(!this.value || this.pattern.test(this.value))) {
      throw new ValidationError('Bitte geben Sie eine Nummer ein');
    }
  }

  private toFixed(num, fixed): string {
    let re = new RegExp('^-?\\d+(?:\.\\d{0,' + (fixed || -1) + '})?');
    return num.toString().match(re)[0];
  }

}
