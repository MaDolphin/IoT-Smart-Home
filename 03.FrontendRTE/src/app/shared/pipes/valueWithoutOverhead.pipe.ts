/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Pipe, PipeTransform } from '@angular/core';
import { Logger } from '@upe/logger';

@Pipe({
  name: 'withoutOverhead',
  pure: false,
})
export class ValueWithoutOverheadPipe implements PipeTransform {

  private logger: Logger = new Logger({name: 'ValueWithoutOverheadPipe', flags: ['pipe']});

  transform(value: number, rate: number): number {
    return value * (100 - rate) / 100;
  }
}
