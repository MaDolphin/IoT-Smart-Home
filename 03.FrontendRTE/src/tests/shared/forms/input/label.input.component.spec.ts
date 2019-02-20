import { DebugElement } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { LabelInputComponent } from '@components/input/label/label-input.component';
import { LabelFormControl } from '@shared/generic-form/controls/label.form-control';

describe('Forms', () => {

  describe('Inputs', () => {

    describe('Label Input Component', () => {

      let fixture: ComponentFixture<LabelInputComponent>;
      let lic: LabelInputComponent;
      let input: DebugElement;

      const name = 'test-name';

      beforeEach(async(() => {
        TestBed.configureTestingModule({
                                         declarations: [
                                           LabelInputComponent,
                                         ],
                                         imports:      [
                                           ReactiveFormsModule,
                                         ],
                                       }).compileComponents().then(() => {
          fixture              = TestBed.createComponent(LabelInputComponent);
          lic                  = fixture.componentInstance;
          lic.name             = name;
          lic.modelFormControl = TestBed.get(LabelFormControl);
          // extract DOM Elements
          input                = fixture.debugElement.query(By.css('input'));

          // first change detection
          fixture.detectChanges();
        });
      }));

      afterEach(() => lic.modelFormControl.reset());

      xit('write text', () => {
        // TODO, test infrastructure not working
        const text = 'fgbpanöxfvret';

        input.nativeElement.value = text;



        expect(lic.modelFormControl.valid).toBeTruthy('any text should be valid');
        expect(lic.modelFormControl.value).toEqual(text, 'written text and saved text should be equal');

      });

      xit('maxChipLength', () => {

        const text = 'fgbpanöxfvret';

        lic.maxChipLength = 5;

        input.nativeElement.value = text;

        expect(lic.modelFormControl.invalid).toBeTruthy('the written must be text is to long');
        expect(lic.modelFormControl.value).toEqual(text.substr(0, 2), 'only the first 2 chars should be saved');

      });

      xit('maxChips', () => {

        lic.maxChips = 1;

        const text = '1';

        input.nativeElement.value = text;

        input.triggerEventHandler('keyup.enter', {});

        // input element should be hidden now

      });


      xit('required', () => {

        lic.modelFormControl.updateValueAndValidity();

        expect(lic.modelFormControl.valid).toBeTruthy('default should be not required');

        lic.required = true;

        lic.modelFormControl.updateValueAndValidity();

        expect(lic.modelFormControl.invalid).toBeTruthy('input should be required');

        lic.required = false;

        lic.modelFormControl.updateValueAndValidity();

        expect(lic.modelFormControl.valid).toBeTruthy('input should be not required');

      });

      xit('htmlId', () => {

        expect(lic.htmlId).toEqual(`${name}-form-group`);

      });

    });

  });

});
