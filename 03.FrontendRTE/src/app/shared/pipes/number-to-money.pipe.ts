import { DecimalPipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';
import * as Big from 'big.js';

@Pipe({
        name: 'toMoney',
        pure: true,
      })
export class NumberToMoneyPipe implements PipeTransform {

  constructor(private _number: DecimalPipe) {
  }

  transform(value: number | null): string | null {
    value = value ? value : 0;
    value = Number(Big(value).div(100).toString());
    return value !== null && !isNaN(value) ? `${this._number.transform(value, '1.2-2')}` : '';
  }

}
