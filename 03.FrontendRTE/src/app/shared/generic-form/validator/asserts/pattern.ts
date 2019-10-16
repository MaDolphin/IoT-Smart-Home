/* (c) https://github.com/MontiCore/monticore */

import { FormControl } from '@angular/forms';
import { Assertion } from '../assertion';
import { flag } from '../utils/flag';
import { ValidationError } from '../validation.error';

export function pattern(this: Assertion, regexp: string | RegExp) {
  const control: FormControl = flag(this, 'object');

  if (!(control instanceof FormControl)) {
    throw new ValidationError('only works with generic form controls');
  }

  this.assert(
    control.value === null || control.value.match(regexp) !== null,
    `control does not match regexp '${regexp}'`,
  );

}
