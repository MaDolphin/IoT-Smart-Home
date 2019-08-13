/* (c) https://github.com/MontiCore/monticore */

import 'reflect-metadata';
import { LABEL } from '../config';

export function Label(lable: string) {
  return function (target, propertyKey) {
    Reflect.defineMetadata(LABEL, lable, target, propertyKey);
  };
}
