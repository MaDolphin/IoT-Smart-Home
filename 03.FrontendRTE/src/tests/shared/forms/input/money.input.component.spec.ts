/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { DebugElement } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { TextMaskModule } from 'angular2-text-mask';
import { FormularControl } from '@shared/architecture/forms/controls/formular.control';
import { MoneyInputComponent } from '@components/input/money/money-input.component';

describe('Forms', () => {

  describe('Inputs', () => {

    describe('Money Input Component', () => {

      let fixture: ComponentFixture<MoneyInputComponent>;
      let mic: MoneyInputComponent;
      let input: DebugElement;

      const name = 'test-name';

      beforeEach(async(() => {
        TestBed.configureTestingModule({
                                         declarations: [
                                           MoneyInputComponent,
                                         ],
                                         imports:      [
                                           ReactiveFormsModule,
                                           TextMaskModule,
                                         ],
                                       }).compileComponents().then(() => {
          fixture              = TestBed.createComponent(MoneyInputComponent);
          mic                  = fixture.componentInstance;
          mic.name             = name;
          mic.modelFormControl = TestBed.get(FormularControl);
          // extract DOM Elements
          input                = fixture.debugElement.query(By.css(`input`));

          // first change detection
          fixture.detectChanges();
        });
      }));

      afterEach(() => mic.modelFormControl.reset());

      xit('valid input', () => {

        input.nativeElement.value = '1000';

        expect(mic.modelFormControl.valid).toBeTruthy('the written money value should be valid');

        expect(mic.modelFormControl.value).toEqual(1000);

      });

      xit('invalid input', () => {

        input.nativeElement.value = '1000';

        expect(mic.modelFormControl.valid).toBeTruthy('the written money value should be valid');

        expect(mic.modelFormControl.value).toEqual(1000);

      });

      xit('required', () => {

        mic.modelFormControl.updateValueAndValidity();

        expect(mic.modelFormControl.valid).toBeTruthy('default should be not required');

        mic.required = true;

        mic.modelFormControl.updateValueAndValidity();

        expect(mic.modelFormControl.invalid).toBeTruthy('input should be required');

        mic.required = false;

        mic.modelFormControl.updateValueAndValidity();

        expect(mic.modelFormControl.valid).toBeTruthy('input should be not required');

      });

      xit('htmlId', () => {

        expect(mic.htmlId).toEqual(`${name}-form-group`);

      });

    });

  });

});
