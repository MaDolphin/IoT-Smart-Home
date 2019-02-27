/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { GenericFormControl, GenericFormGroup } from '../..';

export function extractPrototype(instance: any): any {
  if (!instance.constructor) {
    throw new Error('this obj is not a instance of a class');
  }

  if (!instance.constructor.prototype) {
    throw new Error('could not find the class prototype');
  }

  return instance.constructor.prototype;

}

export function getControlsByNames<T extends GenericFormGroup<any>>(this: T, controlNames): GenericFormControl<any>[] {
  return controlNames.map((controlName: string) => this.controls[controlName]);
}
