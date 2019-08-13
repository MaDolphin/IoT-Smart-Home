/* (c) https://github.com/MontiCore/monticore */

import 'reflect-metadata';
import { ADD_IF, DEFAULT_IF, DISABLED_IF, ERROR_MSG, LABEL_IF, OPTIONS_IF, PLACEHOLDER_IF, READONLY_IF, REMOVE_IF, REQUIRED, REQUIRED_IF, } from '../config';
import { GenericFormControl } from '../generic-form-control';
import { GenericFormGroup } from '../generic-form-group';
import { GenericFormDecoratorError } from '../generic-form.error';
import { ValidatorFn } from '../validator-fn';
import { ISelectOptions } from './options';

export function AddIf(fnc: ValidatorFn, ...changeTrigger: string[]) {
  return function (target: any, propertyKey: string) {
    if (Reflect.hasMetadata(ADD_IF, target, propertyKey)) {
      throw new GenericFormDecoratorError(
        'can only be used once',
        'AddIf',
        target.name,
        propertyKey,
      );
    }
    Reflect.defineMetadata(ADD_IF, {fnc, changeTrigger}, target, propertyKey);
  };
}

export function ApplyAddIf<T extends GenericFormGroup<any>>(this: T, addIfObj: IAddIf) {
  const thisControl: GenericFormControl<any> = this[addIfObj.propertyKey];
  if (!(thisControl instanceof GenericFormControl)) {
    throw new Error(`'this.${addIfObj.propertyKey}' is not a generic form control`);
  }
  try {
    addIfObj.reflect.fnc.apply(thisControl, this.getControlsByNames(addIfObj.reflect.changeTrigger));
    this.addControl(addIfObj.controlName, thisControl);
  } catch (e) {
    // TODO : add logging
  }
}

export function ApplyRemoveIf<T extends GenericFormGroup<any>>(this: T, removeIfObj): void {
  const thisControl: GenericFormControl<any> = this[removeIfObj.propertyKey];
  if (!(thisControl instanceof GenericFormControl)) {
    throw new Error(`'this.${removeIfObj.propertyKey}' is not a generic form control`);
  }
  try {
    removeIfObj.reflect.fnc.apply(thisControl, this.getControlsByNames(removeIfObj.reflect.changeTrigger));
    this.removeControl(removeIfObj.controlName);
  } catch (e) {
    // TODO : add logging
  }
}

export function ReadonlyIf(fnc: ValidatorFn, ...changeTrigger: string[]) {
  return function (target: any, propertyKey: string) {
    Reflect.defineMetadata(READONLY_IF, {fnc, changeTrigger}, target, propertyKey);
  };
}

export function RemoveIf(fnc: ValidatorFn, ...changeTrigger: string[]) {
  return function (target: any, propertyKey: string) {
    if (Reflect.hasMetadata(REMOVE_IF, target, propertyKey)) {
      throw new GenericFormDecoratorError(
        'can only be used once',
        'RemoveIf',
        target.name,
        propertyKey,
      );
    }
    Reflect.defineMetadata(REMOVE_IF, {fnc, changeTrigger}, target, propertyKey);
  };
}

export function RequiredIf(fnc: ValidatorFn, erorMessage: string, ...changeTrigger: string[]) {
  return function (target: any, propertyKey: string) {
    Reflect.defineMetadata(REQUIRED + ERROR_MSG, erorMessage, target, propertyKey);
    Reflect.defineMetadata(REQUIRED_IF, {fnc, changeTrigger}, target, propertyKey);
  };
}

export function DisabledIf(fnc: ValidatorFn, ...changeTrigger: string[]) {
  return function (target: any, propertyKey: string) {
    Reflect.defineMetadata(DISABLED_IF, {fnc, changeTrigger}, target, propertyKey);
  };
}

export type StringManipulationFn = (
  control: GenericFormControl<any>,
  ...depControls: Array<GenericFormControl<any>>
) => string;

export type OptionsArrayManipulationFn = (
  control: GenericFormControl<any>,
  ...depControls: Array<GenericFormControl<any>>
) => string[] | ISelectOptions[];

export type StringArrayManipulationFn = (
  control: GenericFormControl<any>,
  ...depControls: Array<GenericFormControl<any>>
) => string[];

export type DefaultValueManipulationFn = (
  control: GenericFormControl<any>,
  ...depControls: Array<GenericFormControl<any>>
) => string | number | boolean;

export interface IOptionsIfOptions {
  skipSelf?: boolean;
  triggerOnce?: boolean;
  skipInitCall?: boolean;
}

// TODO : add option parameter to other 'if' decorators
export function OptionsIf(fnc: OptionsArrayManipulationFn, options: IOptionsIfOptions = {}, ...changeTrigger: string[]) {
  return function (target: any, propertyKey: string) {
    Reflect.defineMetadata(OPTIONS_IF, {fnc, options, changeTrigger}, target, propertyKey);
  };
}

export function PlaceholderIf(fnc: StringManipulationFn, ...changeTrigger: string[]) {
  return function (target: any, propertyKey: string) {
    Reflect.defineMetadata(PLACEHOLDER_IF, {fnc, changeTrigger}, target, propertyKey);
  };
}

export function LabelIf(fnc: StringManipulationFn, ...changeTrigger: string[]) {
  return function (target: any, propertyKey: string) {
    Reflect.defineMetadata(LABEL_IF, {fnc, changeTrigger}, target, propertyKey);
  };
}

export function DefaultIf(fnc: DefaultValueManipulationFn, ...changeTrigger: string[]) {
  return function (target: any, propertyKey: string) {
    Reflect.defineMetadata(DEFAULT_IF, {fnc, changeTrigger}, target, propertyKey);
  };
}

export interface IAddIfReflect {
  changeTrigger: string[];
  fnc: ValidatorFn;
}

export interface IAddIf {
  reflect: IAddIfReflect;
  controlName: string;
  propertyKey: string;
}
