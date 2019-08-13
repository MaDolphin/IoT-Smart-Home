/* (c) https://github.com/MontiCore/monticore */

import 'reflect-metadata';
import { ALLOW_CHARACTERS } from '../config';

export enum CharacterTypes {
  LETTERS,
  NUMBERS,
  SPECIAL_CHARACTERS,
  ALL,
}

export function AllowCharacters(...allow: CharacterTypes[]) {
  return function (target: any, propertyKey: string) {
    Reflect.defineMetadata(ALLOW_CHARACTERS, allow, target, propertyKey);
  };
}
