import { Pipe, PipeTransform } from '@angular/core';
import { Logger } from '@upe/logger';

@Pipe({
  name: 'asString',
  pure: true,
})
export class NumberToStringPipe implements PipeTransform {

  private logger: Logger = new Logger({name: 'NumberToStringPipe', flags: ['pipe'], muted: true});

  transform(num: Number | null): string {
    this.logger.debug('transform', [num]);
    return num + '';
  }
}
