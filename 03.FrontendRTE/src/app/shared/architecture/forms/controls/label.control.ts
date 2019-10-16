/* (c) https://github.com/MontiCore/monticore */

import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { FormularControl } from './formular.control';

export class LabelControl extends FormularControl<string[], string[]> {

  public static IsLabel(control: AbstractControl): ValidationErrors | null {
    console.error('TODO LabelControl');
    /*
    if (!control.value || !!control.value.match(`^${NORMAL_DATE_REGEX}$`)) {

      if (!control.value || moment(control.value, NORMAL_DATE_FORMAT).isValid()) {
        return null;
      } else {
        return {
          error: 'Datum ist nicht korrekt'
        };
      }

    } else {
      return {
        error: `Format: TT.MM.JJJJ`
      };
    }
*/
    return {
      error: `Test Error`
    };
  }

  public constructor(formState: string[] | null = null,
                     ...validator: ValidatorFn[]) {
    super(formState, ...validator, LabelControl.IsLabel);
    this.logger.addFlag('label');
  }

  public setValue(value: string[] | null): void {

    super.setValue(value);
    this.updateValueAndValidity();
  }

  public setValidators(newValidator: ValidatorFn | ValidatorFn[]) {
    let validators: ValidatorFn[] = this.mergeValidators(
          LabelControl.IsLabel,
          newValidator);

      super.setValidators(validators);
  }

  protected handelChange(value: string[] | null): string[] | null {
    // return this.stringToDate.transform(value);
    console.log('TODO: handle change');
    return ['hallo'];
  }

}
