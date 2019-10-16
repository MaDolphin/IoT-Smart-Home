/* (c) https://github.com/MontiCore/monticore */

import 'reflect-metadata';
import { ADD_VALIDATOR, VALIDATOR_CONTROL_FUNCTIONS } from '../config';
import { ValidatorFn } from '../validator-fn';

export function Validator(fnc: ValidatorFn, ...changeTrigger: string[]) {
  return function (target: any, propertyKey: string) {
    if (!Reflect.hasMetadata(VALIDATOR_CONTROL_FUNCTIONS, target, propertyKey)) {
      Reflect.defineMetadata(VALIDATOR_CONTROL_FUNCTIONS, [], target, propertyKey);
    }
    Reflect.getMetadata(VALIDATOR_CONTROL_FUNCTIONS, target, propertyKey).push({ fnc, changeTrigger });
  };
}

export function AddValidator(target: any, propertyKey: string, descriptor: PropertyDescriptor) {
  if (!Reflect.hasMetadata(ADD_VALIDATOR, target)) {
    Reflect.defineMetadata(ADD_VALIDATOR, [], target);
  }
  Reflect.getMetadata(ADD_VALIDATOR, target).unshift(descriptor.value);
}
