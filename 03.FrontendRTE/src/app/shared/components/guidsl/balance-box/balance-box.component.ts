/* (c) https://github.com/MontiCore/monticore */

import { Component, Input } from '@angular/core';
import { ColorType } from '@shared/types';

@Component({
  selector:    'macoco-balance-box',
  templateUrl: './balance-box.component.html',
  styleUrls:   ['./balance-box.component.scss'],
})

export class BalanceBoxComponent {

  @Input() public type: ColorType;
  @Input() public amount: number;
  @Input() public label: string;

}
