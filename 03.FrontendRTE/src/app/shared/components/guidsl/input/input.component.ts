/* (c) https://github.com/MontiCore/monticore */

import { Component, Input } from '@angular/core';

@Component({
  selector: 'macoco-input',
  templateUrl: './input.component.html',
  styleUrls: ['./input.scss']
})
export class InputComponent {

  @Input() public isValid: boolean          = false;
  @Input() public isInvalid: boolean        = false;
  @Input() public htmlId: string;
  @Input() public label: string;
  @Input() public labelAbove: boolean       = false;
  @Input() public error: string;
  @Input() public additionalLabel: boolean  = false;
  @Input() public icon: string | null       = null;
  @Input() public iconType: string | null   = null;
  @Input() public customIcon: string | null = null;

  public get hasLabel(): boolean {
    return !!this.label;
  }

}
