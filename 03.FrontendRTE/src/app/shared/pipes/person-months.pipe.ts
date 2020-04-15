/**
 * Transforms pure integer person months to two decimal points months number
 */
import {Pipe, PipeTransform} from '@angular/core';
import {DecimalPipe} from "@angular/common";
import * as Big from 'big.js';

@Pipe({name: 'personmonths'})
export class PersonMonthsPipe implements PipeTransform {

  private _number: DecimalPipe;

  constructor() {
    this._number = new DecimalPipe('de-DE');
  }

  transform(val: string): string {

    let value: number;

    value = val ? Number(val) : 0;
    //value = Number(Big(value).div(100).toString());

    let returnValue = value !== null && !isNaN(value) ? `${this._number.transform(value, '1.2-2')}` : '';
    returnValue = returnValue.replace(',', '.');
    return returnValue;

  }
}
