import { Pipe, PipeTransform } from '@angular/core';
import { Logger } from '@upe/logger';

@Pipe({
  name: 'withOverhead',
  pure: false,
})
export class ValueWithOverheadPipe implements PipeTransform {

  private logger: Logger = new Logger({name: 'ValueWithOverheadPipe', flags: ['pipe']});

  transform(value: number, rate: number): number {
    return value * 100 / (100 - rate);
  }
}
