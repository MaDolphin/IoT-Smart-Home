/* (c) https://github.com/MontiCore/monticore */

import { Component, ElementRef, EventEmitter, Injectable, Output, ViewChild } from '@angular/core';
import { DateAdapter, NativeDateAdapter } from '@angular/material';
import { DateControl } from '@shared/architecture/forms/controls/date.control';
import { AutoCompleteDatePipe } from '@shared/pipes/auto-complete-date.pipe';
import { StringToDatePipe } from '@shared/pipes/string-to-date.pipe';
import { AbstractInputComponent } from '../abstract.input.component';
import { DateToStringPipe } from '@shared/pipes/date-to-string.pipe';
import { ValidationError } from '@shared/generic-form/validator';
import { isDate } from '@shared/generic-form/validator/asserts/date';

@Injectable()
export class CustomDateAdapter extends NativeDateAdapter {

  public strToDate: StringToDatePipe;
  public dateToStr: DateToStringPipe;
  public autoComplete: AutoCompleteDatePipe;

  init() {
    if (!this.strToDate) {
      this.strToDate = new StringToDatePipe();
      this.dateToStr = new DateToStringPipe();
      this.autoComplete = new AutoCompleteDatePipe(this.strToDate);
    }
  }

  format(date: Date, displayFormat: Object): string {
    if (!this.isValid(date)) {
      throw Error('NativeDateAdapter: Cannot format invalid date.');
    }

    this.init();
    return this.dateToStr.transform(date);
  }

  parse(value: any): Date | null {
    this.init();
    value = this.autoComplete.transform(value);
    return this.strToDate.transform(value);
  }

  deserialize(value: string): Date | null {
    this.init();
    value = this.autoComplete.transform(value);
    return this.strToDate.transform(value);
  }

  getFirstDayOfWeek(): number {
    return 1;
  }

}

@Component({
  selector: 'macoco-date-input',
  templateUrl: './date-input.component.html',
  styleUrls: ['./date-input.component.scss', '../input.scss'],
  providers: [
    { provide: DateAdapter, useClass: CustomDateAdapter }
  ]
})
export class DateInputComponent extends AbstractInputComponent<DateControl> {

  @Output('dateChanged') onChange = new EventEmitter<string>();

  constructor() {
    super();
  }

  @ViewChild('dateInput') dateInput: ElementRef;
  @ViewChild('dateInlineInput') dateInlineInput: ElementRef;

  // TODO: dubious validation implemetation, need very specific CustomDateAdapter?
  validate() {
    let input: ElementRef;
    if (this.inline) {
      input = this.dateInlineInput;
    } else {
      input = this.dateInput;
    }
    if (input === undefined || input === null) return;

    let value = input.nativeElement.value;
    (<any>this.modelFormControl).actualValue = value;
    if (!value) {
      this.modelFormControl.setErrors(null);
      return;
    }
    let error = 'Bitte geben Sie ein gültiges Datum ein';

    this.modelFormControl.markAsDirty();
    this.modelFormControl.markAsTouched();
    if (!this.isValid(value)) {
      this.setErrors(error);
    } else {
      if (!isDate(value)) {
        this.setErrors(error);
      } else {
        let errors = this.modelFormControl.errors;
        if (errors && errors.msg) {
          if (errors.msg === error || errors.msg === error) {
            this.modelFormControl.setErrors(null);
          }
        }
      }
    }
  }

  isValid(date: string): boolean {
    const dateRegex = /^[0-9]{2}\.[0-9]{2}\.[0-9]{4}$/;
    return dateRegex.test(date);
  }

  public dateChange(event) {
    this.onChange.emit(event.value);
  }

  private setErrors(error: string): void {
    this.modelFormControl.setErrors(new ValidationError(error));
    this.modelFormControl.errors.error = error;
  }

}
