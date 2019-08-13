/* (c) https://github.com/MontiCore/monticore */

import { Pipe, PipeTransform } from '@angular/core';
import { Logger } from '@upe/logger';

const EMPTY_VALUE: number = -999999999;

@Pipe({
  name: 'toHour',
  pure: false,
})
export class HourPipe implements PipeTransform {

  private logger: Logger = new Logger({name: 'HourPipe', flags: ['pipe']});

  transform(value: number): string {
    return value === EMPTY_VALUE ? '' : value / 100 + 'h';
  }
}
