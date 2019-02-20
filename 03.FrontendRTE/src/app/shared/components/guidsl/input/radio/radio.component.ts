import { Component, Input } from '@angular/core';
import { SelectFormControl } from '@shared/generic-form/controls/select.form-control';

@Component({
  selector: 'app-radio',
  templateUrl: './radio.component.html',
})
export class RadioComponent {

  @Input() public modelFormControl: SelectFormControl;

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
