import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-generic-input',
  templateUrl: './generic-input.component.html',
  styleUrls: ['./generic-input.component.scss']
})
export class GenericInputComponent {

  @Input() public isValid: boolean = false;
  @Input() public isInvalid: boolean = false;
  @Input() public htmlId: string;
  @Input() public label: string;
  @Input() public labelAbove: boolean = false;
  @Input() public error: string;
  @Input() public additionalLabel: boolean = false;
  @Input() public icon: string | null = null;
  @Input() public iconType: string = 'fa-icon';
  @Input() public customIcon: string | null = null;
  @Input() public noPaddingOrMargin: boolean = false;

  public get hasLabel(): boolean {
    return !!this.label;
  }

}