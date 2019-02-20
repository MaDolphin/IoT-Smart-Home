import { Pipe, PipeTransform } from '@angular/core';
import { Logger } from '@upe/logger';

@Pipe({
  name: 'toPercent',
  pure: false,
})
export class ToPercentPipe implements PipeTransform {

  private logger: Logger = new Logger({name: 'ToPercentPipe', flags: ['pipe']});

  transform(value: number): string {
    if (value == null) {
      return '';
    }
    return value / 100 + '%';
  }
}
