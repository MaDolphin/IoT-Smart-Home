/* (c) https://github.com/MontiCore/monticore */

import { CONTEXT_TEST } from '../config';
import { GenericFormControl } from '../generic-form-control';
import { GenericFormGroup } from '../generic-form-group';
import { ValidatorFn } from '../validator-fn';

export type ContextDeps = (this: GenericFormControl<any>, root: GenericFormGroup<any>) => GenericFormControl<any> | GenericFormControl<any>[];

export function ContextTest(fnc: ValidatorFn, ...changeTrigger: ContextDeps[]) {
  return function (target, propertyKey) {
    if (!Reflect.hasMetadata(CONTEXT_TEST, target, propertyKey)) {
      Reflect.defineMetadata(CONTEXT_TEST, [], target, propertyKey);
    }
    Reflect.getMetadata(CONTEXT_TEST, target, propertyKey).push({fnc, changeTrigger});
  }
}
