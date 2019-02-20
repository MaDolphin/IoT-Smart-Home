import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { FormularControl } from './formular.control';

export class PercentageControl extends FormularControl<string, number> {

  public static IsPercentage(control: AbstractControl): ValidationErrors | null {
    // Regex to check if input percentage has acceptable format
    let percentageRegex = /^(0*((100((\.|\,)0*)?)?|(([0-9]([0-9])?)?)((\.|\,)[0-9]*)?))$/;
    if (!control.value || percentageRegex.test(control.value)) {
      return null;
    } else {
      return {
        error: `Der Prozentwert ist eine Zahl zwischen 0 und 100`
      };
    }
  }

  public constructor(formState: string | null = null,
                     ...validator: ValidatorFn[]) {
    super(formState, PercentageControl.IsPercentage, ...validator);
    this.logger.addFlag('percentage');
  }

  public setValue(value: string | number | null, options?: {
    onlySelf?: boolean;
    emitEvent?: boolean;
    emitModelToViewChange?: boolean;
    emitViewToModelChange?: boolean;
  }): void {
    if (typeof value === 'number') {
      value = value.toString();
    }
    if (!!!value) {
      value = '';
    }
    super.setValue(value, options);
  }

  protected handelChange(value: string | null): number | null {
    if ( value ) {
      return Number(value.replace(',', '.'));
    }
    return null;
  }

  public setValidators(newValidator: ValidatorFn | ValidatorFn[]) {
    let validators: ValidatorFn[] = this.mergeValidators(
          PercentageControl.IsPercentage,
          newValidator);

      super.setValidators(validators);
  }

}
