import { Pipe, PipeTransform } from '@angular/core';
import { Logger } from '@upe/logger';
import * as Big from 'big.js';

@Pipe({
  name: 'sum',
  pure: false,
})
export class SumPipe implements PipeTransform {

  private logger: Logger = new Logger({name: 'SumPipe', flags: ['pipe']});

  transform(values: number[]): number {
    return Number(values
      .map((num) => {
        if (isNaN(Number(num))) {
          num = 0;
          this.logger.warn('number is NaN');
        }
        return new Big(num);
      })
      .reduce((a: Big, b: Big) => a.plus(b), new Big(0)).toString());
  }
}