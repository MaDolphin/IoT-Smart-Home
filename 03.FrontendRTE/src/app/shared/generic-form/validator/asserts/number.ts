/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { FormControl } from '@angular/forms';
import { Assertion } from '../assertion';
import { flag } from '../utils/flag';

export function isNumber(value: any): boolean {
  if (!value) {
    value = '0';
  }
  if (typeof value === 'string') {
    value = value.replace(',', '.')
  }

  return !isNaN(Number(value || 0));
}

export function toNumber(value: any): number {
  if (value) {
    if (typeof value === 'string') {
      return Number(value.replace(',', '.'));
    }
    return value;
  }
  return 0;
}

export function number(this: Assertion) {
  const control: FormControl = flag(this, 'object');

  flag(this, 'number', true);

  this.assert(isNumber(control.value || 0), 'Bitte geben Sie eine Nummer ein');

}
