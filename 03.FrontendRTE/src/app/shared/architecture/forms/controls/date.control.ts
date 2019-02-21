import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import * as moment from 'moment';
import { DateToStringPipe } from '@shared/pipes/date-to-string.pipe';
import { NORMAL_DATE_FORMAT, NORMAL_DATE_REGEX, StringToDatePipe } from '@shared/pipes/string-to-date.pipe';
import { FormularControl } from './formular.control';

export class DateControl extends FormularControl<string, Date> {

  public static IsDate(control: AbstractControl): ValidationErrors | null {
    if (!control.value || !!control.value.match(NORMAL_DATE_REGEX)) {

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

  }

  public constructor(formState: string | null = null,
                     private stringToDate: StringToDatePipe,
                     private dateToString: DateToStringPipe,
                     ...validator: ValidatorFn[]) {
    super(formState, ...validator, DateControl.IsDate);
    this.logger.addFlag('date');
  }

  public setValue(value: string | Date | null): void {
    if (typeof value !== 'string') {
      value = this.dateToString.transform(value);
    }
    super.setValue(value);
    this.updateValueAndValidity();
  }

  protected handelChange(value: string | null): Date | null {
    return this.stringToDate.transform(value);
  }

  public setValidators(newValidator: ValidatorFn | ValidatorFn[]) {
    let validators: ValidatorFn[] = this.mergeValidators(
          DateControl.IsDate,
          newValidator);

      super.setValidators(validators);
  }

}
