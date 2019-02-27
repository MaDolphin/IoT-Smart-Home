/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import 'reflect-metadata';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { OPTIONS, OPTIONS_BEHAVIOR_SUBJECT } from '../config';

export interface ISelectOptions {
  value: string;
  option: string;
}

export function Options(...options: (string | ISelectOptions)[]) {
  return function (target, propertyKey) {
    Reflect.defineMetadata(OPTIONS, options, target, propertyKey);
  };
}

export function OptionsBehaviorSubject(options: BehaviorSubject<(string | ISelectOptions)[]> | string) {
  return function (target, propertyKey) {
    Reflect.defineMetadata(OPTIONS_BEHAVIOR_SUBJECT, options, target, propertyKey);
  };
}
