/* (c) https://github.com/MontiCore/monticore */

import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { FormularControl } from './formular.control';

export class HourControl extends FormularControl<string, number> {

  public static regexp: RegExp = /^[0-9]+$|^[0-9]*(\.|,)[0-9]*$/;

  public static IsHours(control: AbstractControl): ValidationErrors | null {
    // Regex to check if input percentage has acceptable format
    if (!control.value || HourControl.regexp.test(control.value)) {
      return null;
    } else {
      return {
        error: `Eine gültige Nummer muss eingegeben werden`
      };
    }
  }

  public constructor(formState: string | null = null,
    ...validator: ValidatorFn[]) {
    super(formState, HourControl.IsHours, ...validator);
    this.logger.addFlag('number');
  }

  public setValue(value: string | number | null): void {
    if (typeof value === 'number') {
      value = value.toString();
    }
    if (!value) {
      value = '';
    }
    super.setValue(value);
  }

  protected handelChange(value: string | null): number | null {
    if (value) {
      return Number(value.replace(',', '.'));
    }
    return null;
  }

  public setValidators(newValidator: ValidatorFn | ValidatorFn[]) {
    let validators: ValidatorFn[] = this.mergeValidators(HourControl.IsHours, newValidator);

    super.setValidators(validators);
  }

}
