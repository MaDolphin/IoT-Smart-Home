/* (c) https://github.com/MontiCore/monticore */

import 'reflect-metadata';
import { AFTER_SUBMIT, AFTER_VALIDATE, BEFORE_SUBMIT, BEFORE_VALIDATE, SUBMIT } from '../config';
import { GenericFormGroup } from '../generic-form-group';

export type HookFn = () => boolean;

function AddHookFunction(name: string, target: any, fnc: HookFn): void {
  if (!Reflect.hasMetadata(name, target)) {
    Reflect.defineMetadata(name, [], target);
  }
  Reflect.getMetadata(name, target).push(fnc);
}

export function Submit(target: any, propertyKey: string, descriptor: PropertyDescriptor) {
  AddHookFunction(SUBMIT, target, descriptor.value);
}

export function BeforeSubmit(target: any, propertyKey: string, descriptor: PropertyDescriptor) {
  AddHookFunction(BEFORE_SUBMIT, target, descriptor.value);
}

export function AfterSubmit(target: any, propertyKey: string, descriptor: PropertyDescriptor) {
  AddHookFunction(AFTER_SUBMIT, target, descriptor.value);
}

export function BeforeValidate(target: any, propertyKey: string, descriptor: PropertyDescriptor) {
  AddHookFunction(BEFORE_VALIDATE, target, descriptor.value);
}

export function AfterValidate(target: any, propertyKey: string, descriptor: PropertyDescriptor) {
  AddHookFunction(AFTER_VALIDATE, target, descriptor.value);
}

export function CallHookFunctions(
  this: GenericFormGroup<any>,
  hookName: string,
  target: any,
  optional: boolean = true,
): boolean {

  if (!Reflect.hasMetadata(hookName, target)) {
    if (optional === false) {
      throw new Error(`Hook '${hookName}' is not defined`);
    }
  } else {

    const fncs: HookFn[] = Reflect.getMetadata(hookName, target);

    for (const fnc of fncs) {
      if (fnc.apply(this) === false) {
        return false;
      }
    }

  }

  return true;
}
