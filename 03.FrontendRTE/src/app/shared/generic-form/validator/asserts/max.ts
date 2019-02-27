/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { FormControl } from '@angular/forms';
import { Assertion } from '../assertion';
import { flag } from '../utils/flag';
import { isNumber, toNumber } from './number';

export function assertMax(this: Assertion, max: number, forceText: boolean = false) {
  const control: FormControl = flag(this, 'object');

  if (control.value) {
    if ((flag(this, 'number') || isNumber(control.value)) && !forceText) {
      this.assert(toNumber(control.value) <= max, `Die eingegebene Nummer muss kleiner als ${max} sein.`);
    } else {
      this.assert(control.value.length <= max, `Es sind maximal  ${max} Zeichen erlaubt.`);
    }
  }

}
