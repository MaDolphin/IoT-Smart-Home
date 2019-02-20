import { Injectable } from '@angular/core';
import { DateToStringPipe } from '@shared/pipes/date-to-string.pipe';
import { StringToDatePipe } from '@shared/pipes/string-to-date.pipe';
import { AddValidator } from '@shared/generic-form/generic-form/decorators/validator';
import { GenericFormControl } from '@shared/generic-form/generic-form';
import { IGenericFormGroup } from '@shared/generic-form/generic-form/generic-form-group.interface';
import { validate, ValidationError } from '../validator';
import { DEFAULT_DATE_FORMAT, isDate, isInNormalDateFormat } from '@shared/generic-form/validator/asserts/date';

@Injectable()
export class DateFormControl<G extends IGenericFormGroup = IGenericFormGroup> extends GenericFormControl<string, G> {

  private _dateToStringPipe = new DateToStringPipe();
  private _stringToDatePipe = new StringToDatePipe();

  private _actualValue: string | Date ;

  public getModelValue(): any {
    return this._dateToStringPipe.transform(this._stringToDatePipe.transform(this.value), 'ISO')
  }

  public setModelValue(modelValue: string) {
    this.setValue(this._dateToStringPipe.transform(this._stringToDatePipe.transform(modelValue), DEFAULT_DATE_FORMAT as any))
  }

  setValue(value: string, options?: {
    onlySelf?: boolean;
    emitEvent?: boolean;
    emitModelToViewChange?: boolean;
    emitViewToModelChange?: boolean;
  }): void {
    this.actualValue = value;
    super.setValue(value, options);
  }

  patchValue(value: string, options?: {
    onlySelf?: boolean;
    emitEvent?: boolean;
    emitModelToViewChange?: boolean;
    emitViewToModelChange?: boolean;
  }): void {
    this.setValue(value, options);
  }

  public set actualValue(value: string | Date) {
    this._actualValue = value;
  }

  public get actualValue(): string | Date {
    return this._actualValue;
  }

  @AddValidator
  public isDateValidator() {
    this.assert(!this.actualValue || isInNormalDateFormat(this.actualValue.toString()) || this.actualValue instanceof Date,
      'Bitte geben Sie ein gültiges Datum ein');
    this.assert(!this.actualValue || isDate(this.actualValue) || this.actualValue instanceof Date,
      'Bitte geben Sie ein gültiges Datum ein');

    validate(this).is.date;
  }

  private assert(condition: boolean, error: string) {
    if (!condition) {
      throw new ValidationError(error);
    }
  }

}
