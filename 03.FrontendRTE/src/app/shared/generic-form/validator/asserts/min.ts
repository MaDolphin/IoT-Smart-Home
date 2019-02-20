import { FormControl } from '@angular/forms';
import { Assertion } from '../assertion';
import { flag } from '../utils/flag';
import { isNumber, toNumber } from './number';

export function assertMin(this: Assertion, min: number, forceText: boolean = false) {
  const control: FormControl = flag(this, 'object');

  if (control.value) {
    if ((flag(this, 'number') || isNumber(control.value)) && !forceText) {
      this.assert(toNumber(control.value) >= min, `The entered number must be gather than ${min}`);
    } else {
      this.assert(control.value.length >= min, `Minimum ${min} characters required`);
    }
  }

}
