/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Component, Input } from '@angular/core';
import { RadioFormControl } from '@shared/generic-form/controls/radio.form-control';

@Component({
  selector: 'macoco-radio-group',
  styleUrls: ['./radio-group.component.scss'],
  templateUrl: './radio-group.component.html',
})
export class RadioGroupComponent {

  @Input() public modelFormControl: RadioFormControl;

  @Input()
  public name: string;

  @Input()
  public label: string = '';


  private _checked: boolean = false;

  @Input()
  public set checked(value: boolean) {
    this._checked = value;
  };

  public get checked(): boolean {
    return this._checked || this.modelFormControl.value === this.label;
  };

  // TODO: needs a rework for a more general case
  public onChange(event: any) {
    this.modelFormControl.patchValue(this.label)
  }
}
