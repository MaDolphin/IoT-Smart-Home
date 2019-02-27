/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { AbstractControl, FormControl } from '@angular/forms';
import { Assertion } from '../assertion';
import { flag } from '../utils/flag';
import { ValidationError } from '../validation.error';

export function assertEqual(this: Assertion, val, msg) {
  const control: FormControl = flag(this, 'object');

  if (!(control instanceof AbstractControl)) {
    throw new ValidationError('only works with generic form controls');
  }

  this.assert(val === control.value, `expected #{this.value} to equal #{exp}`);
}