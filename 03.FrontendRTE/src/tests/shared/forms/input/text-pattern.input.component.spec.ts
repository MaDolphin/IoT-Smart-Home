/* (c) https://github.com/MontiCore/monticore */

import { DebugElement } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { TextMaskModule } from 'angular2-text-mask';
import { FormularControl } from '@shared/architecture/forms/controls/formular.control';
import { NUMBER_MASK } from '@shared/architecture/forms/controls/money.control';
import { TextMaskInputComponent } from '@components/input/text-mask/text-mask-input.component';

describe('Forms', () => {

  describe('Inputs', () => {

    describe('Text Mask Input Component', () => {

      let fixture: ComponentFixture<TextMaskInputComponent>;
      let tpic: TextMaskInputComponent;
      let input: DebugElement;

      const name = 'test-name';

      beforeEach(async(() => {
        TestBed.configureTestingModule({
                                         declarations: [
                                           TextMaskInputComponent,
                                         ],
                                         imports:      [
                                           ReactiveFormsModule,
                                           TextMaskModule,
                                         ],
                                       }).compileComponents().then(() => {
          fixture               = TestBed.createComponent(TextMaskInputComponent);
          tpic                  = fixture.componentInstance;
          tpic.name             = name;
          tpic.modelFormControl = new FormularControl<string>();
          tpic.mask             = NUMBER_MASK;
          // extract DOM Elements
          input                 = fixture.debugElement.query(By.css('input'));

          // first change detection
          fixture.detectChanges();
        });
      }));

      afterEach(() => tpic.modelFormControl.reset());

      xit('required', () => {

        tpic.modelFormControl.updateValueAndValidity();

        expect(tpic.modelFormControl.valid).toBeTruthy('default should be not required');

        tpic.required = true;

        tpic.modelFormControl.updateValueAndValidity();

        expect(tpic.modelFormControl.invalid).toBeTruthy('input should be required');

        tpic.required = false;

        tpic.modelFormControl.updateValueAndValidity();

        expect(tpic.modelFormControl.valid).toBeTruthy('input should be not required');

      });

      xit('htmlId', () => {

        expect(tpic.htmlId).toEqual(`${name}-form-group`);

      });

    });

  });

});
