/* (c) https://github.com/MontiCore/monticore */

import { Component, OnInit } from '@angular/core';
import { PercentageControl } from '@shared/architecture/forms/controls/percentage.control';
import { AbstractInputComponent } from '../abstract.input.component';
import { TWO_DECIMAL_NUMBER_REGEX } from '@shared/generic-form/controls/two-decimal-number.form-control';
import { TWO_DECIMAL_NUMBER_UNTRIMMED_REGEX } from '@shared/generic-form/controls/percent-hour.form-control';

@Component({
  selector: 'macoco-percentage-input',
  templateUrl: './percentage-input.component.html',
  styleUrls: ['../input.scss']
})
export class PercentageInputComponent extends AbstractInputComponent<PercentageControl> implements OnInit {

  ngOnInit() {
    this.autoCompletePercentage();
  }

  public autoCompletePercentage(): void {
    let value = '';
    // reformat if value exists and a valid number
    if (this.modelFormControl.value
      && (!PercentageControl.IsPercentage(this.modelFormControl)
        || TWO_DECIMAL_NUMBER_REGEX.test(this.modelFormControl.value))) {
      // replace comma with dot
      value = this.modelFormControl.value.toString().replace(',', '.');
      // accept single dot
      if (value === '.') {
        value = '0';
      }
      // trim to two decimal points
      let valueStr = value.match(/^-?\d*(?:(\.|\,)\d{0,2})?/);
      if (valueStr) {
        // format string to have exactly two decimal point numbers
        value = Number(valueStr[0]).toFixed(2);
        // replace dot back to comma
        value = value.replace('.', ',');
      }
    } else {
      value = this.modelFormControl.value;
    }
    this.modelFormControl.setValue(value, {
      emitEvent: false,
      emitModelToViewChange: true,
      emitViewToModelChange: false,
      onlySelf: true
    });
  }

  public autoCompletePercentageInline(): void {
    let value = '';
    // reformat if value exists and a valid number
    if (this.modelFormControl.value
      && (!PercentageControl.IsPercentage(this.modelFormControl)
        || TWO_DECIMAL_NUMBER_UNTRIMMED_REGEX.test(this.modelFormControl.value))) {
      // replace comma with dot
      value = this.modelFormControl.value.toString().replace(',', '.').trim();
      // accept single dot
      if (value === '.') {
        value = '0';
      }
      // trim to two decimal points
      let valueStr = value.match(/^-?\d*(?:(\.|\,)\d{0,2})?/);
      if (valueStr) {
        // format string to have exactly two decimal point numbers
        value = Number(valueStr[0]).toFixed(2);
        // replace dot back to comma
        value = value.replace('.', ',');
      }
      value += ' %';
    } else {
      value = this.modelFormControl.value;
    }
    this.modelFormControl.setValue(value)
  }
}
