/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Component, OnInit } from '@angular/core';
import { HourControl } from '@shared/architecture/forms/controls/hour.control';
import { AbstractInputComponent } from '../abstract.input.component';

@Component({
  selector: 'macoco-hour-input',
  templateUrl: './hour-input.component.html',
  styleUrls: ['../input.scss']
})
export class HourInputComponent extends AbstractInputComponent<HourControl> implements OnInit {

  ngOnInit() {
    this.autoCompleteHour();
  }

  public autoCompleteHour(): void {
    let value = '';
    // reformat if value exists and a valid number
    if (this.modelFormControl.value && !HourControl.IsHours(this.modelFormControl)) {
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
    this.modelFormControl.setValue(value);
  }
}
