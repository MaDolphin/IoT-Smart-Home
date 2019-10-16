/* (c) https://github.com/MontiCore/monticore */

import { DebugElement } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { FormDirectivesModule, InputGroupDirectivesModule } from '@upe/ngx-bootstrap-directives';
import { By } from '@angular/platform-browser';
import { PercentageControl } from '@shared/architecture/forms/controls/percentage.control';
import { PercentageInputComponent } from '@components/input/percentage/percentage-input.component';
import { InputComponent } from '@components/input/input.component';

describe('Forms', () => {

  describe('Inputs', () => {

    describe('Percentage Input Component', () => {

      let fixture: ComponentFixture<PercentageInputComponent>;
      let pic: PercentageInputComponent;
      let input: DebugElement;

      const name = 'test-name';

      beforeEach(async(() => {
        TestBed.configureTestingModule({
                                         declarations: [
                                           PercentageInputComponent,
                                           InputComponent
                                         ],
                                         imports:      [
                                           ReactiveFormsModule,
                                           FormDirectivesModule,
                                           InputGroupDirectivesModule
                                         ],
                                       })
                                       .overrideComponent(PercentageInputComponent, {
                                         // TODO: remove this part (template overriding)
                                         // use only part of the template, as anything else is irrelevant
                                         set: {
                                           template: `
                                             <input upeFormControl
                                                    [placeholder]="placeholder"
                                                    [name]="name"
                                                    (blur)="autoCompletePercentage()"
                                                    [formControl]="modelFormControl"
                                                    type="text">
                                          `
                                         }
                                       })
                                       .compileComponents().then(() => {
          fixture              = TestBed.createComponent(PercentageInputComponent);
          pic                  = fixture.componentInstance;
          pic.name             = name;
          pic.modelFormControl = new PercentageControl();
          // extract DOM Elements
          input                = fixture.debugElement.query(By.css('input'));

          // first change detection
          fixture.detectChanges();
        });
      }));

      afterEach(() => pic.modelFormControl.reset());

      it('valid input', () => {

        input.nativeElement.value = '00010.01910';
        input.nativeElement.dispatchEvent(new Event('input'));
        input.nativeElement.dispatchEvent(new Event('blur'));
        fixture.detectChanges();

        expect(pic.modelFormControl.valid).toBeTruthy('the written percentage value should be valid');

        expect(pic.modelFormControl.value).toEqual('10,01');

      });

      it('invalid input', () => {

        input.nativeElement.value = '100.01';
        input.nativeElement.dispatchEvent(new Event('input'));
        input.nativeElement.dispatchEvent(new Event('blur'));
        fixture.detectChanges();

        expect(pic.modelFormControl.valid).toBeFalsy('the written percentage value should be invalid');

        expect(pic.modelFormControl.value).toEqual('100,01', 'invalid value should be reformatted when possible');

      });

      xit('required', () => {

        pic.modelFormControl.updateValueAndValidity();

        expect(pic.modelFormControl.valid).toBeTruthy('default should be not required');

        pic.required = true;

        pic.modelFormControl.updateValueAndValidity();

        expect(pic.modelFormControl.invalid).toBeTruthy('input should be required');

        pic.required = false;

        pic.modelFormControl.updateValueAndValidity();

        expect(pic.modelFormControl.valid).toBeTruthy('input should be not required');

      });

      it('htmlId', () => {

        expect(pic.htmlId).toEqual(`${name}-form-group`);

      });

    });

  });

});
