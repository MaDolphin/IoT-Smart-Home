/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { DebugElement } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCalendar, MatDatepickerModule } from '@angular/material';
import { By } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { DateControl } from '@shared/architecture/forms/controls/date.control';
import { DateInputComponent } from '@components/input/date/date-input.component';
import { AutoCompleteDatePipe } from '@shared/pipes/auto-complete-date.pipe';
import { DateToStringPipe } from '@shared/pipes/date-to-string.pipe';
import { StringToDatePipe } from '@shared/pipes/string-to-date.pipe';

describe('Forms', () => {

  describe('Inputs', () => {

    xdescribe('Date Input Component', () => {

      let fixture: ComponentFixture<DateInputComponent>;
      let dic: DateInputComponent;
      let input: DebugElement;
      let calendarChild: MatCalendar<Date>;

      const name = 'test-name';

      beforeEach(async(() => {
        TestBed.configureTestingModule({
                                         declarations: [
                                           DateInputComponent,
                                           StringToDatePipe,
                                         ],
                                         imports:      [
                                           ReactiveFormsModule,
                                           MatDatepickerModule,
                                           NoopAnimationsModule
                                         ],
                                         providers:    [
                                           AutoCompleteDatePipe,
                                           StringToDatePipe,
                                           DateToStringPipe,
                                         ],
                                       })
                                       .overrideComponent(DateInputComponent, {
                                         // use only part of the template, as anything else is irrelevant
                                         set: {
                                           template: `
                                            <input class="form-control"
                                                  [placeholder]="placeholder"
                                                  (blur)="updateDate()"
                                                  [formControl]="modelFormControl"
                                                  [readonly]="readonly"
                                                  [name]="name"
                                                  type="text">
                                            <mat-calendar (selectedChange)="handleSelected($event); calendar.hide()"
                                                        [selected]="isValid(modelFormControl.value) ? (modelFormControl.value | toDate) : null"
                                                        (click)="$event.preventDefault()"
                                                        [minDate]="rerenderDummy">
                                            </mat-calendar>
                                          `
                                        }
                                      })
                                      .compileComponents().then(() => {
          fixture              = TestBed.createComponent(DateInputComponent);
          dic                  = fixture.componentInstance;
          dic.name             = name;
          dic.modelFormControl = new DateControl(null, TestBed.get(StringToDatePipe), TestBed.get(DateToStringPipe));
          input                = fixture.debugElement.query(By.css('input'));
          calendarChild = null;
          fixture.detectChanges();
        });
      }));

      afterEach(() => dic.modelFormControl.reset());

      it('full valid date input', () => {

        const date = '01.01.2017';

        expect(dic.modelFormControl.value).toEqual(null);

        input.nativeElement.value = date;
        input.nativeElement.dispatchEvent(new Event('input'));
        fixture.detectChanges();

        expect(dic.modelFormControl.valid).toBeTruthy();
      });

      it('full invalid date input', () => {
        const date = '01.13.2017';

        expect(dic.modelFormControl.value).toEqual(null);

        input.nativeElement.value = date;
        input.nativeElement.dispatchEvent(new Event('input'));
        fixture.detectChanges();

        expect(dic.modelFormControl.valid).toBeFalsy();

      });

      it('shorted valid date input', () => {

        const date = '1.1.17';

        expect(dic.modelFormControl.value).toEqual(null);

        input.nativeElement.value = date;
        input.nativeElement.dispatchEvent(new Event('input'));
        input.nativeElement.dispatchEvent(new Event('blur'));
        fixture.detectChanges();

        expect(dic.modelFormControl.valid).toBeTruthy();

      });

      it('shorted invalid date input', () => {

        const date = '1.13.17';

        expect(dic.modelFormControl.value).toEqual(null);

        input.nativeElement.value = date;
        input.nativeElement.dispatchEvent(new Event('input'));
        input.nativeElement.dispatchEvent(new Event('blur'));
        fixture.detectChanges();

        expect(dic.modelFormControl.valid).toBeFalsy();

      });

      xit('required', () => {

        dic.modelFormControl.updateValueAndValidity();

        expect(dic.modelFormControl.valid).toBeTruthy('default should be not required');

        dic.required = true;

        dic.modelFormControl.updateValueAndValidity();

        expect(dic.modelFormControl.invalid).toBeTruthy('input should be required');

        dic.required = false;

        dic.modelFormControl.updateValueAndValidity();

        expect(dic.modelFormControl.valid).toBeTruthy('input should be not required');

      });

      it('htmlId', () => {

        expect(dic.htmlId).toEqual(`${name}-form-group`);

      });

      it('checks input->calendar interaction', () => {

        let date = '1.1.17';

        expect(dic.modelFormControl.value).toEqual(null);
        input.nativeElement.value = date;

        input.nativeElement.dispatchEvent(new Event('input'));
        input.nativeElement.dispatchEvent(new Event('blur'));
        fixture.detectChanges();

        expect(calendarChild.activeDate).toEqual(new Date(2017, 0, 1));

        date = '12.10.1999';

        input.nativeElement.value = date;

        input.nativeElement.dispatchEvent(new Event('input'));
        input.nativeElement.dispatchEvent(new Event('blur'));
        fixture.detectChanges();

        expect(calendarChild.activeDate).toEqual(new Date(1999, 9, 12));
      });

      it('checks calendar->input interaction', () => {

        let now = new Date();
        // 14.02.(year_now - 1)
        let date = new Date(now.getFullYear() - 1, 1, 14);

        expect(dic.modelFormControl.value).toEqual(null);
        // there might actually be slight difference (not that we care)
        expect(calendarChild.activeDate).toBeGreaterThanOrEqual(now.setSeconds(now.getSeconds() - 2));
        expect(calendarChild.activeDate).toBeLessThanOrEqual(now.setSeconds(now.getSeconds() + 4));

        calendarChild._dateSelected(date);
        fixture.detectChanges();

        let pipe = new DateToStringPipe();
        expect(dic.modelFormControl.value).toEqual(pipe.transform(date));
      });

    });

  });

});
