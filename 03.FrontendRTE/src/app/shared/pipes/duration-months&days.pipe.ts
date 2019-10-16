/* (c) https://github.com/MontiCore/monticore */

import { Pipe, PipeTransform } from '@angular/core';
import { Logger } from '@upe/logger';
import { StringToDatePipe } from './string-to-date.pipe';

@Pipe({
  name: 'inMonthsAndDays',
  pure: false,
})
export class DurationInMonthsAndDays implements PipeTransform {

  private logger: Logger = new Logger({ name: 'DurationInMonthsAndDays', flags: ['pipe'] });

  constructor(private stringToDate: StringToDatePipe) {
  }

  static daysInMonth(month, year): number {
    return new Date(year, month, 0).getDate();
  }

  transform(value: Array<Date | string | null>,
    yearsStr: string = 'J',
    monthsStr: string = 'M',
    daysStr: string = 'T',
    defaultMessage: string = 'wird automatisch berechnet',
    validateMessage: string = 'Der Projektstart muss vor dem Projektende liegen'): string {

    if (value.length !== 2) {
      this.logger.error('MAF0x00C9: Only to dates are allowed');
      throw new Error('MAF0x00C9: Only to dates are allowed');
    }

    const startDate: Date = this.stringToDate.transform(value[0]) as any;
    const endDate: Date = this.stringToDate.transform(value[1]) as any;
    if (!startDate || !endDate) {
      this.logger.error('MAF0x0093: invalid Date', {
        start: value[0],
        startDate: startDate,
        end: value[1],
        endDate: endDate
      });
      return defaultMessage;
    }
    endDate.setDate(endDate.getDate() + 1);
    let months = (endDate.getMonth() - startDate.getMonth()) + ((endDate.getFullYear() - startDate.getFullYear()) * 12);
    let days = endDate.getDate() - startDate.getDate();
    let years: number = 0;
    if (days < 0) {
      days = days + DurationInMonthsAndDays.daysInMonth(startDate.getMonth() + 1, startDate.getFullYear());
      months = months - 1;
    }

    if (months > 0 && days < 3) {
      // abrunden bei wenigen Resttagen
      days = 0;
    }

    if (months >= 0 && days > (DurationInMonthsAndDays.daysInMonth(endDate.getMonth() + 1, endDate.getFullYear()) - 3)) {
      // aufrunden bei fast vollständigen Monaten
      days = 0;
      months++;
    }

    if (months >= 12) {
      years = Math.floor(months / 12);
      months = months % 12;
    }
    let retStr: string = '';
    if (years !== 0) {
      retStr = retStr + `${years}${yearsStr} `
    }
    if (months !== 0) {
      retStr = retStr + `${months}${monthsStr} `
    }
    if (days !== 0) {
      retStr = retStr + `${days}${daysStr}`
    }
    return retStr;
  }
}
