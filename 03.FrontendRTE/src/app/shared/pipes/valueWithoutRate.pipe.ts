import { Pipe, PipeTransform } from '@angular/core';
import { Logger } from '@upe/logger';

@Pipe({
  name: 'withoutRate',
  pure: false,
})
export class ValueWithoutRatePipe implements PipeTransform {

  private logger: Logger = new Logger({name: 'ValueWithoutRatePipe', flags: ['pipe']});

  transform(value: number, rate: number): number {
    return value * 100 / (100 + rate);
  }
}
