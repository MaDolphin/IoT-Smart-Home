import { Pipe, PipeTransform } from '@angular/core';
import { Logger } from '@upe/logger';

@Pipe({
  name: 'relTo',
  pure: true,
})
export class GetRelativeValueToPipe implements PipeTransform {

  private logger: Logger = new Logger({name: 'GetRelativeValueToPipe', flags: ['pipe', 'pure']});

  transform(value: number, to: number, rounding: 'floor' | 'round' | 'ceil' = 'round'): number {
    if (rounding === 'floor') {
      return to !== 0 && value ? Math.floor(value / to * 100) : 0;
    }
    if (rounding === 'ceil') {
      return to !== 0 && value ? Math.ceil(value / to * 100) : 0;
    }
    return to !== 0 && value ? Math.round(value / to * 100) : 0;
  }

}
