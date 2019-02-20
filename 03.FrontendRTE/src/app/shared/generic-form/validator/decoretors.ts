import { addChainableMethod } from './utils/addChainableMethod';
import { addMethod } from './utils/addMethod';
import { addProperty } from './utils/addProperty';

export function AssertionProperty(fnc) {
  return function (target: any, propertyKey: string) {
    addProperty(target, propertyKey, fnc);
  };
}

export function AssertionMethod(fnc) {
  return function (target: any, propertyKey: string, descriptor) {
    addMethod(descriptor, propertyKey, fnc);
  };
}

export function AssertionChainableMethod(fnc, chainingBehavior?) {
  return function (target: any, propertyKey: string) {
    addChainableMethod(target, propertyKey, fnc, chainingBehavior);
  };
}

export function AssertionChain(target: any, propertyKey: string) {
  addProperty(target, propertyKey);
}
