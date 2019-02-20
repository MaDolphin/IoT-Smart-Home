import { Pipe, PipeTransform } from '@angular/core';
import { Logger } from '@upe/logger';
import * as moment from 'moment';

export const NORMAL_DATE_FORMAT = 'DD.MM.YYYY - HH:mm';

@Pipe({
  name: 'unixToDateString',
  pure: true,
})
export class UnixDateToStringPipe implements PipeTransform {

  private logger: Logger = new Logger({name: 'DateToStringPipe', flags: ['pipe'], muted: true});

  transform(date: number | null, format = NORMAL_DATE_FORMAT): string {
    this.logger.debug('transform', [date]);
    return date ? moment(date).format(format) : '';
  }
}
