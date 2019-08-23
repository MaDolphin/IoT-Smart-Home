/* (c) https://github.com/MontiCore/monticore */

import { Pipe, PipeTransform } from '@angular/core';
import { Logger } from '@upe/logger';

@Pipe({
  name: 'MoneyToNumber',
  pure: true,
})
export class MoneyToNumberPipe implements PipeTransform {

  private logger: Logger = new Logger({ name: 'MoneyToNumberPipe', flags: ['pipe'], muted: true });

  transform(value: string | null): number {
    let num: number = 0;
    let decimalPlaces: number = 2;

    if ( value == null ) {
      return num;
    }

    // return number without transformation

    if (typeof value === 'number') {
      return value;
    }


    // point used as comma, replace it with comma
    let lastIndex = value.lastIndexOf('.');
    if (value.length > 3 && lastIndex >= value.length - 3) {
      value = value.substr(0, lastIndex) + ',' + value.substr(lastIndex + 1);
    }

    value = value.replace(/\./g, '');

    // remove currency symbol
    if ( value.indexOf && value.indexOf('€') !== -1 )
      value = value.substring(0, value.lastIndexOf(' €'));

    let decimals: number = 0;
    let indexComma = value.indexOf(',');

    // count decimal places
    if ( indexComma === -1 )
      decimals = 0;
    else
      decimals = value.length - (indexComma + 1);

    // append decimals until decimal places
    for ( let i = decimals; i < decimalPlaces; i++) {
      value += '0';
    }

    value = value.replace(/,/, '');

    num = Number(value);
    if (isNaN(num)) {
      this.logger.debug('isNaN', value);
      num = 0;
    }
    this.logger.debug('result', {value: value, num: num});

    return num;
  }
}
