/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { ENTER } from '@angular/cdk/keycodes';
import { Component, ElementRef, Input, ViewChild } from '@angular/core';
import { MatChipInputEvent } from '@angular/material';
import { LabelControl } from '@shared/architecture/forms/controls/label.control';
import { AbstractInputComponent } from '../abstract.input.component';

@Component({
  selector: 'macoco-label-input',
  templateUrl: './label-input.component.html',
  styleUrls: ['../input.scss', 'label-input.component.scss']
})
export class LabelInputComponent extends AbstractInputComponent<LabelControl> {

  @Input() public maxChips = 3;
  public maxChipLength = 10;

  private static COMMA = 188;

  public separatorKeysCodes = [ENTER, LabelInputComponent.COMMA];

  @Input() public removable: boolean = true;

  @ViewChild('inputField') inputField: ElementRef;

  add(event: MatChipInputEvent): void {
    let input = event.input;
    let value = event.value;

    let tags = [];

    if (this.modelFormControl.value)
      tags = this.modelFormControl.value;

    // Add our item
    if ((value || '').trim()) {
      tags.push(value);
      this.modelFormControl.setValue(tags);
    }

    // Reset the input value
    if (input) {
      input.value = '';
    }
  }

  remove(item: any): void {
    let index = this.modelFormControl.value.indexOf(item);

    if (index >= 0) {
      this.modelFormControl.value.splice(index, 1);
    }

    if (index === 0) {
      if (this.inputField) {
        this.inputField.nativeElement.focus();
      }
    }
  }

  public showInput() {
    if (!this.modelFormControl.value)
      return true;

    if (!this.maxChips)
      return true;

    if (this.modelFormControl.value.length >= this.maxChips)
      return false;

    return true;

  }

}
