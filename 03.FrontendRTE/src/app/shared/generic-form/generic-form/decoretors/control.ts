/* (c) https://github.com/MontiCore/monticore */

import 'reflect-metadata';
import { CONTROL_NAME } from '../config';

export function Control(name?: string) {
  return function (target: any, propertyKey: string) {
    Reflect.defineMetadata(CONTROL_NAME, name || propertyKey, target, propertyKey);
  };
}
