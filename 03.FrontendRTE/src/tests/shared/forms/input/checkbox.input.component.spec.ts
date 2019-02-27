/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { DebugElement } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { FormularControl } from '@shared/architecture/forms/controls/formular.control';
import { CheckBoxInputComponent } from '@components/input/checkbox/checkbox-input.component';

describe('Forms', () => {

  describe('Inputs', () => {

    describe('Checkbox Input Component', () => {

      let fixture: ComponentFixture<CheckBoxInputComponent>;
      let cbic: CheckBoxInputComponent;
      let input: DebugElement;

      const name = 'test-name';

      beforeEach(async(() => {
        TestBed.configureTestingModule({
                                         declarations: [
                                           CheckBoxInputComponent,
                                         ],
                                         imports:      [
                                           ReactiveFormsModule,
                                         ],
                                       }).compileComponents().then(() => {
          fixture               = TestBed.createComponent(CheckBoxInputComponent);
          cbic                  = fixture.componentInstance;
          cbic.name             = name;
          cbic.modelFormControl = new FormularControl<boolean>();
          // extract DOM Elements
          input                 = fixture.debugElement.query(By.css('[type=checkbox]'));

          // first change detection
          fixture.detectChanges();
        });
      }));

      afterEach(() => cbic.modelFormControl.reset());

      xit('check and uncheck', () => {

        expect(cbic.modelFormControl.value).toBeFalsy();

        input.nativeElement.click();

        expect(cbic.modelFormControl.value).toBeTruthy();

        input.nativeElement.click();

        expect(cbic.modelFormControl.value).toBeFalsy();

      });

      xit('required', () => {

        cbic.modelFormControl.updateValueAndValidity();

        expect(cbic.modelFormControl.valid).toBeTruthy('default should be not required');

        cbic.required = true;

        cbic.modelFormControl.updateValueAndValidity();

        expect(cbic.modelFormControl.invalid).toBeTruthy('input should be required');

        cbic.required = false;

        cbic.modelFormControl.updateValueAndValidity();

        expect(cbic.modelFormControl.valid).toBeTruthy('input should be not required');

      });

      xit('htmlId', () => {

        expect(cbic.htmlId).toEqual(`${name}-form-group`);

      });

    });

  });

});
