/* (c) https://github.com/MontiCore/monticore */

import 'reflect-metadata';
import { validate } from '../../validator';
import { ERROR_MSG, REQUIRED } from '../config';
import { GenericFormControl } from '../generic-form-control';

export function Required(errorMsg?: string) {
  return function (target: any, propertyKey: string) {
    Reflect.defineMetadata(REQUIRED, true, target, propertyKey);
    Reflect.defineMetadata(REQUIRED + ERROR_MSG, errorMsg, target, propertyKey);
  }
}

export function RequiredValidator(this: GenericFormControl<any>) {
  validate(this, Reflect.getMetadata(REQUIRED + ERROR_MSG, this.parent, this.name)).not.empty;
}
