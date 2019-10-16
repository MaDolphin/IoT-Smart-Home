/* (c) https://github.com/MontiCore/monticore */

import 'reflect-metadata';
import { DEFAULT } from '../config';

export function Default(value: any) {
  return function (target: any, propertyKey: string) {
    Reflect.defineMetadata(DEFAULT, value, target, propertyKey);
  };
}
