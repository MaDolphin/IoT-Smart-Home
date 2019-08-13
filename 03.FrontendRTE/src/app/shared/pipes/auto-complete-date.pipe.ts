/* (c) https://github.com/MontiCore/monticore */

import { Pipe, PipeTransform } from '@angular/core';
import { StringToDatePipe } from './string-to-date.pipe';

@Pipe({
  name: 'autoCompleteDate',
  pure: false,
})
export class AutoCompleteDatePipe implements PipeTransform {

  constructor(private stringToDate: StringToDatePipe) {
  }

  transform(value: string): string {
    if (value && typeof value === 'string') {
      value = value.trim();
      if (new RegExp(/^(0[1-9]|[1-9]|1\d|2\d|3[01])[\.,](0[1-9]|[1-9]|1[012])[\.,](\d{4}|\d{1}|\d{2})$/).test(value)) {
        const autoCompletedDate = (value
          .replace(/,/g, '.')
          .split('.')
          .map((partialDate: string, index: number) => {
            if (index < 2) {
              if (partialDate.length === 1) {
                return `0${partialDate}`;
              }
            } else if (partialDate.length === 2) {
              return `20${partialDate}`;
            } else if (partialDate.length === 1) {
              return `200${partialDate}`;
            }
            return partialDate;
          }).join('.'));

        if (!!this.stringToDate.transform(autoCompletedDate)) {
          return autoCompletedDate;
        }
      }
    }

    return value;
  }
}
