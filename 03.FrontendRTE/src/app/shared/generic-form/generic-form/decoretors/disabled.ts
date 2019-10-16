/* (c) https://github.com/MontiCore/monticore */

import 'reflect-metadata';
import { DISABLED } from '../config';

export function Disabled(target: any, propertyKey: string) {
  Reflect.defineMetadata(DISABLED, true, target, propertyKey);
}
