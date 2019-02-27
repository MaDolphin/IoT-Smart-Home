/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import 'reflect-metadata';
import { PLACEHOLDER } from '../config';

export function Placeholder(placeholder: string) {
  return function (target, propertyKey) {
    Reflect.defineMetadata(PLACEHOLDER, placeholder, target, propertyKey);
  };
}
