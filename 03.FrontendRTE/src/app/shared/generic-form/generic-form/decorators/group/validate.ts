import { AbstractControl, FormArray, FormGroup, ValidationErrors } from '@angular/forms';
import { AFTER_VALIDATE, BEFORE_VALIDATE } from '@shared/generic-form/generic-form/config';
import { GenericFormControl, GenericFormGroup } from '@shared/generic-form/generic-form';
import { CallHookFunctions } from '../hooks';
import { extractPrototype } from './untils';

function validateControl(
  abstractControl: AbstractControl,
  parentErrors: ValidationErrors = {},
): ValidationErrors | ValidationErrors[] | null {

  let errors: ValidationErrors | ValidationErrors[] | null;

  if (abstractControl instanceof FormArray) {

    // init with array to perseve form structure in the array report
    errors = [];

    for (const control of abstractControl.controls) {
      if (Array.isArray(errors)) {
        // tmp store errors
        const tmpError = validateControl(control);
        // to check if not null
        if (tmpError !== null) {
          // TODO : store array index
          // and if not null push to object
          errors.push(tmpError);
        }
      } else {
        throw new Error('That should never happen XD, errors var is not an array');
      }
    }

    errors = (errors as any[]).length === 0 ? null : errors;

  } else if (abstractControl instanceof FormGroup) {

    errors = {};

    for (const [controlName, ac] of (Object as any).entries(abstractControl.controls)) {
      // tmp store errors
      const tmpError = validateControl(ac);
      // to check if not null
      if (tmpError !== null) {
        // and if not null add to errors object
        errors[controlName] = tmpError;
      }
    }

    errors = Object.keys(errors).length === 0 ? null : errors;

  } else if (abstractControl instanceof GenericFormControl) {
    errors = abstractControl.validate();
  }

  return errors;

}

export function onValidate<T extends GenericFormGroup<any>>(this: T): ValidationErrors | ValidationErrors | null {
  this.beforeValidate();

  const errors: ValidationErrors[] | ValidationErrors | null = validateControl(this);

  this.afterValidate();

  return errors;
}

export function afterValidate<T extends GenericFormGroup<any>>(this: T): void {
  return CallHookFunctions.apply(this, [AFTER_VALIDATE, extractPrototype(this)]);
}

export function beforeValidate<T extends GenericFormGroup<any>>(this: T): void {
  return CallHookFunctions.apply(this, [BEFORE_VALIDATE, extractPrototype(this)]);
}
