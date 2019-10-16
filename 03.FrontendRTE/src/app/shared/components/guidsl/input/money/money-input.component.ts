/* (c) https://github.com/MontiCore/monticore */

import { Component, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Balance, MoneyControl, NEGATIVE_NUMBER_MASK, NUMBER_MASK, } from '@shared/architecture/forms/controls/money.control';
import { AbstractInputComponent } from '@shared/components/guidsl/input/abstract.input.component';

@Component({
  selector: 'macoco-money-input',
  templateUrl: './money-input.component.html',
  styleUrls: ['../input.scss']
})
export class MoneyInputComponent extends AbstractInputComponent<MoneyControl> implements OnInit {

  ngOnInit() {
    if (this.allowNegative) {
      this.mask = NEGATIVE_NUMBER_MASK;
    }
  }

  // return null so the property can faster be checked
  public get hasBalance(): Observable<boolean> | null {
    return this.modelFormControl.balance && this.balanceLabel ? this.modelFormControl.balance.map((value) => value !== null) : null;
  }

  public get balance(): Balance | null {
    return this.modelFormControl.balance;
  }

  @Input() private balanceLabel: boolean = true;

  @Input() public allowNegative: boolean = true;

  public mask: Array<string | RegExp> = NUMBER_MASK;

  public onFocus() {
    if (!this.disabled && this.modelFormControl.value === '0,00') {
      this.modelFormControl.setValue('');
    }
  }

  updateMoney() {
    this.modelFormControl.updateMoney();
  }
}
