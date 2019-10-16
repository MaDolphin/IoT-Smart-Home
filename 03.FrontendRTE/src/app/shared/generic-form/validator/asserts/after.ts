/* (c) https://github.com/MontiCore/monticore */

import { FormControl } from '@angular/forms';
import { Assertion } from '../assertion';
import { flag } from '../utils/flag';
import { isDate, toDate, toDateString } from './date';
import { isEmpty } from './empty';

export function after(this: Assertion, date: string | Date | FormControl) {
  const control: FormControl = flag(this, 'object') || this._obj;

  if (isEmpty(control)) {
    return;
  }

  if (flag(this, 'date') !== true) {
    // Assertion.create(control).is.date;
  }

  if (date instanceof FormControl) {
    if (isDate(date.value)) {
      date = date.value;
    } else {
      // if passed control is not a valid date skip
      return;
    }
  }

  if (typeof date === 'string') {
    date = toDate(date);
  }

  if (!(date instanceof Date)) {
    throw new Error(`passed date '${date}' is not valid`);
  }

  const controlDate = toDate(control.value);

  this.assert(controlDate.getTime() > date.getTime(), `date should be not after ${toDateString(date)}`);

}
