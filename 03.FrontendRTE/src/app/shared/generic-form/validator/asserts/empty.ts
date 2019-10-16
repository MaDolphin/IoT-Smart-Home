/* (c) https://github.com/MontiCore/monticore */

import { FormControl } from '@angular/forms';
import { Assertion } from '../assertion';
import { flag } from '../utils/flag';

export function isEmpty(control: FormControl) {
  return '' === control.value || control.value === null;
}

export function assertEmpty(this: Assertion) {
  const control: FormControl = flag(this, 'object');

  this.assert(
      isEmpty(control),
      'Bitte einen Wert eingeben',
  );
}
