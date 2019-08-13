/* (c) https://github.com/MontiCore/monticore */

import 'reflect-metadata';
import { READONLY } from '../config';

export function Readonly(target: any, propertyKey: string) {
  Reflect.defineMetadata(READONLY, true, target, propertyKey);
}
