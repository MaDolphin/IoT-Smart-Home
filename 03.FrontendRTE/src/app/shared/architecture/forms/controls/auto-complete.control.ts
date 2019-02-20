import { ValidationErrors, ValidatorFn } from '@angular/forms';
import 'rxjs/add/operator/startWith';
import { FormularControl } from './formular.control';

export class AutoCompleteControl<T extends string = string> extends FormularControl {


  public static IsContainInListFactory(bool: boolean, optionsFunc: () => string[] | null) {
    return (control: AutoCompleteControl): ValidationErrors | null => {
      const options = optionsFunc();
      if (control.allowNewInput || options === null || !control.value || options.indexOf(control.value) !== -1) {
        return null;
      } else if (bool === false) {
        return null;
      }
      return {error: 'Fehlerhafte Eingabe'};
    }
  }

  public allowNewInput: boolean = false;
  public get filteredOptions(): T[] {
    if (this.options) {

      if (this.filterByInput && this.value) {
        if (this.options.includes(this.value) || this.allowNewInput) {
          return this.options;
        }
        return this.options.filter((item: T) => new RegExp(`^${this.value}`, 'gi').test(item));
      }

      return this.options;

    } else {
      return [];
    }
  }

  /*  private static addDefaultValidator<T>(options: T[] | null, createDefaultValidator: boolean) {
        if (createDefaultValidator) {
          return AutoCompleteControl.IsContainInListFactory(() => options);
        }
    }*/

  public filterByInput: boolean = true;

  public constructor(formState: string = '',
                     public options: T[] | null = null,
                     public createDefaultValidator: boolean = true,
                     ...validator: ValidatorFn[]) {
    super(formState, AutoCompleteControl.IsContainInListFactory(createDefaultValidator, () => this.options), ...validator);
    // super(formState, ...validator.concat(createDefaultValidator ? [AutoCompleteControl.IsContainInListFactory(() => this.options)] : []));
    // super(formState, AutoCompleteControl.addDefaultValidator(options, createDefaultValidator, validator));
    this.logger.addFlag('auto complete');
  }

  public setValidators(newValidator: ValidatorFn | ValidatorFn[]) {
    let validators: ValidatorFn[] = this.mergeValidators(

          AutoCompleteControl.IsContainInListFactory(this.createDefaultValidator, () => this.options),
          newValidator);

      super.setValidators(validators);
  }



}
