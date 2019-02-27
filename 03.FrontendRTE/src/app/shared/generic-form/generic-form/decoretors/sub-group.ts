/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import 'reflect-metadata';
import { SUB_GROUP } from '../config';

export function SubGroup(name?: string) {
  return function (target: any, propertyKey: string) {
    Reflect.defineMetadata(SUB_GROUP, name || propertyKey, target, propertyKey);
  };
}
