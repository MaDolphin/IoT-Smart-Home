/* (c) https://github.com/MontiCore/monticore */

import { Pipe, PipeTransform } from '@angular/core';
import { Logger } from '@upe/logger';

@Pipe({
  name: 'withRate',
  pure: false,
})
export class ValueWithRatePipe implements PipeTransform {

  private logger: Logger = new Logger({name: 'ValueWithRatePipe', flags: ['pipe']});

  transform(value: number, rate: number): number {
    return value / 100 * (100 + rate);
  }
}
