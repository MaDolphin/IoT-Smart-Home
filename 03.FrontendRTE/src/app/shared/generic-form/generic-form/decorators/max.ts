import 'reflect-metadata';
import { ERROR_MSG, FORCE_TYPE, MAX } from '../config';

export function Max(max: number, forceText: boolean = false, errorMsg?: string) {
  return function (target: any, propertyKey: string) {
    Reflect.defineMetadata(MAX, max, target, propertyKey);
    Reflect.defineMetadata(MAX + FORCE_TYPE, forceText, target, propertyKey);
    Reflect.defineMetadata(MAX + ERROR_MSG, errorMsg, target, propertyKey);
  };
}