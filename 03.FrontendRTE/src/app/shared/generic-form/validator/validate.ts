/* (c) https://github.com/MontiCore/monticore */

import { FormControl } from '@angular/forms';

import { Assertion } from './assertion';

export function validate(val: FormControl, message?: string): Assertion {
  return Assertion.create(val, message);
}
