import { ReflectiveInjector } from '@angular/core';
import { AbstractControl, FormArray } from '@angular/forms';
import { GenericFormGroup } from '@shared/generic-form/generic-form';
import { extractPrototype } from './utils';

// TODO : dont allow custom multi sub group names
// FIXME : this functionality is not implemented !!!!!!!

export function createMultiSubGroup(this: GenericFormGroup<any>, name: string, subGroupModel?: any) {
  const prototype = extractPrototype(this);

  const propertyKey       = name; // FIXME : will not work with custom multi sub group names
  const multiSubGroupName = propertyKey;

  if (!Reflect.hasMetadata('design:type', prototype, propertyKey)) {
    throw new Error('Could not create new multi sub group without class type specification')
  }

  // get the constructor for this control
  const subGroupConstructor = Reflect.getMetadata('design:type', prototype, propertyKey);

  if (subGroupConstructor === undefined) {
    throw new Error('Could not create new multi sub group. The constructor ist undefined');
  }

  let injector;

  try {
    injector = ReflectiveInjector.resolveAndCreate([subGroupConstructor], this.injector);
  } catch (e) {
    throw new Error(`could not resolve multi sub form group '${name}': ` + e);
  }

  // create sub group instance
  const subGroup = injector.get(subGroupConstructor);

  if (!(subGroup instanceof GenericFormGroup)) {
    throw new Error(`Multi SubGroup instance '${propertyKey}' is not instance of AbstractControl`);
  }

  subGroup.onAddControl.subscribe((c) => this.onAddControl.next(c));
  subGroup.onRemoveControl.subscribe((c) => this.onRemoveControl.next(c));

  if (this[propertyKey].length > this.model[multiSubGroupName].length) {
    throw new Error('Something want very badly wrong. There are more multi sub group instances then models');
  }

  // set sub group name to the multi sub group name + the index
  // TODO : whats happened with the index/name, if I add 5 groups and remove 2 and add 4
  subGroup.name = `${multiSubGroupName}-${this[propertyKey].length}`;

  // if subGroupModel is not passed
  if (!subGroupModel) {
    // create a new model
    subGroupModel = {};
  }

  if (!this.model[multiSubGroupName].find((sgm) => sgm === subGroupModel)) {
    // add new model to array
    this.model[multiSubGroupName].push(subGroupModel);
  }

  // add sub group to multi form group array
  this[propertyKey].push(subGroup);

  const formArray: AbstractControl = this.controls[multiSubGroupName];

  if (formArray instanceof FormArray) {
    subGroup.setParent(formArray);
    formArray.push(subGroup);
  } else {
    throw new Error(`The form array for the multi sub group '${multiSubGroupName}' is not init`);
  }

  // init sub group with model
  subGroup.init(subGroupModel);
  this.onAddControl.next(subGroup);

}

export function removeMultiSubGroup(this: GenericFormGroup<any>, name: string, index: number) {
  this[name].splice(index, 1);
  const formArray = this.controls[name];
  if (formArray instanceof FormArray) {
    const control = formArray.at(index);
    formArray.removeAt(index);
    if (!(control instanceof GenericFormGroup)) {
      throw new Error('That should never happen XD');
    }
    // cleanup subscriptions
    control.onRemoveControl.complete();
    control.onRemoveControl.unsubscribe();
    control.onAddControl.complete();
    control.onAddControl.unsubscribe();
    this.onRemoveControl.next(control);
  } else {
    throw new Error('That should never happen XD');
  }
  this.model[name].splice(index, 1);
}

export function countMultiSubGroup(this: GenericFormGroup<any>, name: string): number {
  return this[name].length;
}
