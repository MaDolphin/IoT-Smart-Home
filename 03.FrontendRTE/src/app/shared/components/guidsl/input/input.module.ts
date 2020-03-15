/* (c) https://github.com/MontiCore/monticore */

import { CommonModule } from '@angular/common';
import { ModuleWithProviders, NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  MatAutocompleteModule,
  MatButtonModule,
  MatCardModule,
  MatCheckboxModule,
  MatChipsModule,
  MatDatepickerModule,
  MatInputModule,
  MatMenuModule,
  MatRadioModule,
  MatSelectModule,
  MatTabsModule,
} from '@angular/material';
import { RouterModule } from '@angular/router';
import { FormDirectivesModule, InputGroupDirectivesModule } from '@upe/ngx-bootstrap-directives';
import { TextMaskModule } from 'angular2-text-mask';
import { ChartsModule } from 'ng2-charts';

import { PipesModule } from '@shared/pipes';
import { AutoCompleteInputComponent } from './auto-complete/auto-complete.component';
import { CheckBoxInputComponent } from './checkbox/checkbox-input.component';
import { DateInputComponent } from './date/date-input.component';
import { DropDownInputComponent } from './drop-down/drop-down-input.component';

import { GenericInputComponent } from './generic/generic-input.component';
import { HourInputComponent } from './hour/hour-input.component';
import { MoneyInputComponent } from './money/money-input.component';
import { PercentageInputComponent } from './percentage/percentage-input.component';
import { RadioComponent } from './radio/radio.component';
import { TextInputComponent } from './text/text-input.component';
import { TextareaInputComponent } from './textarea/textarea-input.component';
import { LabelInputComponent } from '@shared/components/guidsl/input/label/label-input.component';
import { InputComponent } from '@shared/components/guidsl/input/input.component';
import { FocusService } from '@shared/components/guidsl/input/focus.service';
import { TextMaskInputComponent } from '@shared/components/guidsl/input/text-mask/text-mask-input.component';
import { TextPatternInputComponent } from '@shared/components/guidsl/input/text-pattern/text-pattern-input.component';
import { RadioGroupComponent } from '@shared/components/guidsl/input/radio-group/radio-group.component';
import { MultiDropDownInputComponent } from "@components/input/multi-drop-down/multi-drop-down-input.component";

@NgModule({
  declarations: [
    MoneyInputComponent,
    HourInputComponent,
    TextInputComponent,
    DateInputComponent,
    GenericInputComponent,
    CheckBoxInputComponent,
    PercentageInputComponent,
    TextareaInputComponent,
    AutoCompleteInputComponent,
    InputComponent,
    DropDownInputComponent,
    RadioComponent,
    RadioGroupComponent,
    LabelInputComponent,
    TextMaskInputComponent,
    TextPatternInputComponent,
    MultiDropDownInputComponent
  ],
  imports: [
    CommonModule,
    FlexLayoutModule,
    ChartsModule,
    TextMaskModule,
    MatTabsModule,
    RouterModule,
    ReactiveFormsModule,
    PipesModule.forArchitecture(),
    MatDatepickerModule,
    MatCardModule,
    FormsModule,
    FormDirectivesModule,
    InputGroupDirectivesModule,
    MatAutocompleteModule,
    MatSelectModule,
    MatRadioModule,
    MatCheckboxModule,
    MatChipsModule,
    MatInputModule,
    MatMenuModule,
    MatButtonModule
  ],
  exports: [
    MoneyInputComponent,
    HourInputComponent,
    TextInputComponent,
    DateInputComponent,
    GenericInputComponent,
    CheckBoxInputComponent,
    PercentageInputComponent,
    TextareaInputComponent,
    MultiDropDownInputComponent,
    AutoCompleteInputComponent,
    InputComponent,
    LabelInputComponent,
    DropDownInputComponent,
    RadioComponent,
    RadioGroupComponent,
    TextMaskInputComponent,
    TextPatternInputComponent
  ],
  providers: [
    FocusService
  ],
  entryComponents: [MoneyInputComponent]
})
export class InputModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule:  InputModule,
    };
  }
}
