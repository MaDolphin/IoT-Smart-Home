import { DebugElement } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { FormularControl } from '@shared/architecture/forms/controls/formular.control';
import { TextInputComponent } from '@components/input/text/text-input.component';

describe('Forms', () => {

  describe('Inputs', () => {

    describe('Text Input Component', () => {

      let fixture: ComponentFixture<TextInputComponent>;
      let tic: TextInputComponent;
      let input: DebugElement;

      const name = 'test-name';

      beforeEach(async(() => {
        TestBed.configureTestingModule({
                                         declarations: [
                                           TextInputComponent,
                                         ],
                                         imports:      [
                                           ReactiveFormsModule,
                                         ],
                                       }).compileComponents().then(() => {
          fixture              = TestBed.createComponent(TextInputComponent);
          tic                  = fixture.componentInstance;
          tic.name             = name;
          tic.modelFormControl = new FormularControl<string>();
          // extract DOM Elements
          input                = fixture.debugElement.query(By.css('input'));

          // first change detection
          fixture.detectChanges();
        });
      }));

      afterEach(() => tic.modelFormControl.reset());

      xit('write text', () => {

        const text = 'fgbpanöxfvret';

        input.nativeElement.value = text;

        expect(tic.modelFormControl.valid).toBeTruthy('any text should be valid');
        expect(tic.modelFormControl.value).toEqual(text, 'written text and saved text should be equal');

      });

      xit('maxlength', () => {

        const text = 'fgbpanöxfvret';

        tic.maxLength = 2;

        input.nativeElement.value = text;

        expect(tic.modelFormControl.invalid).toBeTruthy('the written must be text is to long');
        expect(tic.modelFormControl.value).toEqual(text.substr(0, 2), 'only the first 2 chars should be saved');

      });

      xit('required', () => {

        tic.modelFormControl.updateValueAndValidity();

        expect(tic.modelFormControl.valid).toBeTruthy('default should be not required');

        tic.required = true;

        tic.modelFormControl.updateValueAndValidity();

        expect(tic.modelFormControl.invalid).toBeTruthy('input should be required');

        tic.required = false;

        tic.modelFormControl.updateValueAndValidity();

        expect(tic.modelFormControl.valid).toBeTruthy('input should be not required');

      });

      xit('htmlId', () => {

        expect(tic.htmlId).toEqual(`${name}-form-group`);

      });

    });

  });

});
