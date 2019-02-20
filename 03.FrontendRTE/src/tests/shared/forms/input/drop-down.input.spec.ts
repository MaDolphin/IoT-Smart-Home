import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { DropDownControl, DropDownControlOption, } from '@shared/architecture/forms/controls/drop-down.control';
import { DropDownInputComponent } from '@components/input/drop-down/drop-down-input.component';

describe('Forms', () => {

  describe('Inputs', () => {

    describe('Drop Down Input Component', () => {

      let fixture: ComponentFixture<DropDownInputComponent>;
      let ddic: DropDownInputComponent;

      const defaultOption                            = 'defaultOption';
      const options: DropDownControlOption<string>[] = [
        {option: 'option1', value: 'option1'},
        {option: 'option2', value: 'option2'},
        {option: 'option2', value: 'option2'},
      ];

      const name = 'test-name';

      beforeEach(async(() => {
        TestBed.configureTestingModule({
                                         declarations: [
                                           DropDownInputComponent,
                                         ],
                                         imports:      [
                                           ReactiveFormsModule,
                                         ],
                                       }).compileComponents().then(() => {
          fixture               = TestBed.createComponent(DropDownInputComponent);
          ddic                  = fixture.componentInstance;
          ddic.name             = name;
          ddic.modelFormControl = new DropDownControl<string>('null', options, defaultOption);

          // first change detection
          fixture.detectChanges();
        });
      }));

      afterEach(() => ddic.modelFormControl.reset());

      xit('init check', () => {

        expect(ddic.modelFormControl.value).toEqual('null');

        for (const item of options) {
          const domOptions = fixture.debugElement.query(By.css(`[value=${item.value}]`));
          expect(domOptions).toBeDefined(`drop down option '${item.value}' not found`);
        }

      });

      xit('htmlId', () => {

        expect(ddic.htmlId).toEqual(`${name}-form-group`);

      });

    });

  });

});
