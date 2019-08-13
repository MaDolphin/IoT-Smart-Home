/* (c) https://github.com/MontiCore/monticore */

import { FormControl } from '@angular/forms';

export function mockControl(value: any): FormControl {
  return { value: value } as any;
}
