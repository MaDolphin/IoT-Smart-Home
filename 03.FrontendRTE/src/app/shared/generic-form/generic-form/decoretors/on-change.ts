/* (c) https://github.com/MontiCore/monticore */

import 'reflect-metadata';
import { ON_CHANGE, ON_VALID_CHANGE } from '../config';
import { ValidatorFn } from '../validator-fn';

function AddOnChangeFunction(controlName: string, target: any, fnc: ValidatorFn, onInit: boolean) {
  if (!Reflect.hasMetadata(ON_CHANGE, target)) {
    Reflect.defineMetadata(ON_CHANGE, {}, target);
  }
  const onChangeFunctions = Reflect.getMetadata(ON_CHANGE, target);
  if (!onChangeFunctions.hasOwnProperty(controlName)) {
    onChangeFunctions[controlName] = [];
  }
  onChangeFunctions[controlName].push({fnc, onInit});
}

function AddOnValidChangeFunction(controlName: string, target: any, fnc: ValidatorFn, onInit: boolean) {
  if (!Reflect.hasMetadata(ON_VALID_CHANGE, target)) {
    Reflect.defineMetadata(ON_VALID_CHANGE, {}, target);
  }
  const onValidChangeFunctions = Reflect.getMetadata(ON_VALID_CHANGE, target);
  if (!onValidChangeFunctions.hasOwnProperty(controlName)) {
    onValidChangeFunctions[controlName] = [];
  }
  onValidChangeFunctions[controlName].push({fnc, onInit});
}

export function OnChange(controlName: string, onInit: boolean = false) {
  return function (target: any, propertyKey: string, descriptor: PropertyDescriptor) {
    AddOnChangeFunction(controlName, target, descriptor.value, onInit);
  };
}

export function OnValidChange(controlName: string, onInit: boolean = false) {
  return function (target: any, propertyKey: string, descriptor: PropertyDescriptor) {
    AddOnValidChangeFunction(controlName, target, descriptor.value, onInit);
  };
}
