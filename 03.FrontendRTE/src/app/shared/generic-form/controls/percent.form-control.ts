/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Injectable } from '@angular/core';
import { AddValidator } from '../generic-form/decoretors/validator';
import { IGenericFormGroup } from '../generic-form/generic-form-group.interface';
import { ValidationError } from '../validator';
import { PatternFormControl } from './pattern.form-control';

export const PERCENT_REGEX = /^$|^[0-9]+(\.|,)?[0-9]*\s*%$/;
export const PERCENT_UNTRIMMED_REGEX = /^$|^[0-9]+(\.|,)?[0-9]*\s*%?$/;

@Injectable()
export class PercentFormControl<G extends IGenericFormGroup = IGenericFormGroup> extends PatternFormControl<G> {

  public pattern = PERCENT_UNTRIMMED_REGEX;

  public getModelValue() {

    if (this.value && this.pattern.test(this.value)) {
      let value: string = this.value.toString().replace(',', '.');
      value = value.replace('%', '').trim();

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

  // TODO: this seems to override parent validator, even if the parent control is used
  // i.e. this validator will be invoked when TwoDecimalNumberFormControl is used
  /*
  @AddValidator
  public matchPatternValidator() {
    if (!(!this.value || this.pattern.test(this.value))) {
      throw new ValidationError(`Der Prozentwert ist eine Zahl zwischen 0 und 100`);
    }
  }
  /**/

}
