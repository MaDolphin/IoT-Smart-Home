/* (c) https://github.com/MontiCore/monticore */

import { Pipe, PipeTransform } from '@angular/core';
import { NumberToMoneyPipe } from './number-to-money.pipe';

@Pipe({
        name: 'toCurrency',
      })
export class CurrencyPipe implements PipeTransform {

  constructor(private numberToMoney: NumberToMoneyPipe) {
  }

  transform(value: number): string {
    return this.numberToMoney.transform(value) + ' €';
  }
}
