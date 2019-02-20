import { Pipe, PipeTransform } from '@angular/core';
import { Logger } from '@upe/logger';
import * as moment from 'moment';
import { NORMAL_DATE_FORMAT } from './string-to-date.pipe';

export type DateFormats = 'DD.MM.YYYY' | 'YYYY-MM-DDTHH:mm:ss.sssZ' | 'DD.MM.YYYY HH:mm' | 'ISO';

@Pipe({
  name: 'toDateString',
  pure: true,
})
export class DateToStringPipe implements PipeTransform {

  private logger: Logger = new Logger({name: 'DateToStringPipe', flags: ['pipe'], muted: true});

  transform(date: Date | null, format: DateFormats = NORMAL_DATE_FORMAT): string {
    this.logger.debug('transform', [date]);
    if ((date + '') !== 'Invalid Date') {
      if (format === 'ISO') {
        return date ? new Date(date.getTime() - (date.getTimezoneOffset() * 60000)).toISOString() : '';
      } else {
        return date ? moment(date).format(format) : '';
      }
    }

    return '';
  }
}
