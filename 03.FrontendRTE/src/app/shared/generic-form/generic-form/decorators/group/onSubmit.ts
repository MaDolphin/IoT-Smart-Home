import { AbstractControl, FormGroup } from '@angular/forms';
import { AFTER_SUBMIT, BEFORE_SUBMIT, MULTI_SUB_GROUPS_PROPERTY, SUB_GROUPS_PROPERTY, SUBMIT } from '@shared/generic-form/generic-form/config';
import { GenericFormGroup } from '@shared/generic-form/generic-form';
import { CallHookFunctions } from '../hooks';
import { extractPrototype } from './utils';

export function beforeSubmit<T extends GenericFormGroup<any>>(this: T): boolean {

  let updateTreeValueAndValidity = (control: AbstractControl) => {
    if (control !== undefined && control !== null) {
      control.updateValueAndValidity();
      if (control instanceof FormGroup) {
        for (let key of Object.keys(control.value)) {
          const ctrl = control.controls[key];
          updateTreeValueAndValidity(ctrl);
        }
      }
    }
  };
  updateTreeValueAndValidity(this);

  return this.validate() === null && CallHookFunctions.apply(this, [BEFORE_SUBMIT, extractPrototype(this)]);
}

export function onSubmit<T extends GenericFormGroup<any>>(this: T): boolean {
  let result: boolean = this.beforeSubmit();

  if (this[SUB_GROUPS_PROPERTY]) {
    for (let subGroupPropertyKey of this[SUB_GROUPS_PROPERTY]) {

      if (!(this[subGroupPropertyKey] instanceof GenericFormGroup)) {
        throw new Error(`Object is not instance of GenericFormGroup, instead '${typeof this[subGroupPropertyKey]}'`);
      }

      this[subGroupPropertyKey].submit();
    }
  }

  if (this[MULTI_SUB_GROUPS_PROPERTY]) {
    for (let multiSubGroupPropertyKey of this[MULTI_SUB_GROUPS_PROPERTY]) {
      this[multiSubGroupPropertyKey].forEach((multiSubGroup: GenericFormGroup<any>) => {

        if (!(multiSubGroup instanceof GenericFormGroup)) {
          throw new Error(`Object is not instance of GenericFormGroup, instead '${typeof multiSubGroup}'`);
        }

        multiSubGroup.submit();

      });
    }
  }

  result = result && CallHookFunctions.apply(this, [SUBMIT, extractPrototype(this)]);

  result = result && this.afterSubmit();

  return result;
}

export function afterSubmit<T extends GenericFormGroup<any>>(this: T): boolean {
  return CallHookFunctions.apply(this, [AFTER_SUBMIT, extractPrototype(this)]);
}