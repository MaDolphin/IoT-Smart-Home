/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Pipe, PipeTransform } from '@angular/core';
import { Logger } from '@upe/logger';
import * as moment from 'moment';

export const NORMAL_DATE_REGEX = /^[0-9][0-9]?\.[0-9][0-9]?\.[0-9]{4}$/;
export const ISO_DATE_REGEX = /[0-9]{4}-[0-9]{2}.[0-9]{2}.[0-9]{2}:[0-9]{2}:/;
export const NORMAL_DATE_FORMAT = 'DD.MM.YYYY';
export const ISO_DATE_FORMAT = 'YYYY-MM-DDTHH:mm:ss.sssZ';

@Pipe({
  name: 'toDate',
  pure: true,
})
export class StringToDatePipe implements PipeTransform {

  private logger: Logger = new Logger({name: 'StringToDatePipe', flags: ['pipe']});

  transform(date: string | Date | null): Date | null {
    if (date instanceof Date) {
      return date;
    }

    let dateObj: Date | null = null;

    if (!!(NORMAL_DATE_REGEX).test(date)) {
      dateObj = moment(date, NORMAL_DATE_FORMAT).isValid() ? moment(date, NORMAL_DATE_FORMAT).toDate() : null;
    }

    if (!!(ISO_DATE_REGEX).test(date)) {
      dateObj = moment(date, ISO_DATE_FORMAT).isValid() ? moment(date, ISO_DATE_FORMAT).toDate() : null;
    }

    if ((dateObj + '') === 'Invalid Date') {
      this.logger.error(`MAF0x0096: transform - invalid date >>${date}<<`, { date, match: (date as any).match('[0-9]{2}\.[0-9]{2}\.[0-9]{4}'), moment: moment(date as any, 'DD.MM.YYYY').toDate()});
    }

    return (dateObj + '') !== 'Invalid Date' || true ? dateObj : null;
  }
}
