/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import 'reflect-metadata';
import { ERROR_MSG, FORCE_TYPE, MAX, MIN } from '../config';

export function Min(min: number, forceText: boolean = false, errorMsg?: string) {
  return function (target: any, propertyKey: string) {
    Reflect.defineMetadata(MIN, min, target, propertyKey);
    Reflect.defineMetadata(MAX + FORCE_TYPE, forceText, target, propertyKey);
    Reflect.defineMetadata(MAX + ERROR_MSG, errorMsg, target, propertyKey);
  };
}