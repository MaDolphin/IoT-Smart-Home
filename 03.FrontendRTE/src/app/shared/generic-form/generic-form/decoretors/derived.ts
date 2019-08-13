/* (c) https://github.com/MontiCore/monticore */

import { DERIVED } from '../config';

export function Derived(target, propertyKey) {
  Reflect.defineMetadata(DERIVED, true, target, propertyKey);
}
