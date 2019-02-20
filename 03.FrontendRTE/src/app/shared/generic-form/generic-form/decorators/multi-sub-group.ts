import { DESIGN_TYPE, MULTI_SUB_GROUP } from '../config';

export function MultiSubGroup(classType: any, name?: string) {
  return function (target: any, propertyKey: string) {
    Reflect.defineMetadata(MULTI_SUB_GROUP, name || propertyKey, target, propertyKey);
    Reflect.defineMetadata(DESIGN_TYPE, classType, target, propertyKey);
  };
}
