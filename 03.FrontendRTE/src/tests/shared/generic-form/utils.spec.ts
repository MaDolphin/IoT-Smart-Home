/* (c) https://github.com/MontiCore/monticore */

import { DebugElement } from '@angular/core';
import { ComponentFixture } from '@angular/core/testing';
import { AbstractControl, FormArray, FormControl, FormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { expect } from 'chai';
import { GenericFormControl, GenericFormGroup } from '../../../app/shared/generic-form/generic-form';

export function sendModelToForm(model: any, fixture: ComponentFixture<any>, baseId?: string): void {

  const keys           = Object.keys(model);
  const inputs         = keys.filter((key) => typeof model[key] === 'string' || typeof model[key] === 'number');
  const subGroups      = keys.filter((key) => typeof model[key] === 'object' && !Array.isArray(model[key]));
  const multiSubGroups = keys.filter((key) => Array.isArray(model[key]));

  for (const inputSelector of inputs) {

    const selector            = `${baseId ? `#${baseId} ` : ''}input[formControlName=${inputSelector}]`;
    const input: DebugElement = fixture.debugElement.query(By.css(selector));

    expect(input, `could not find input with selector '${selector}'`).to.be.not.null;

    input.nativeElement.value = model[inputSelector];
    input.nativeElement.dispatchEvent(new Event('input'));

  }

  for (const subGroup of subGroups) {
    console.warn('the component can not be updated');
    sendModelToForm(model[subGroup], fixture, subGroup);
  }

  for (let i = 0; i < multiSubGroups.length; i++) {
    console.warn('the component can not be updated');
    const multiSubGroup = multiSubGroups[i];
    sendModelToForm(model[multiSubGroup][i], fixture, `${multiSubGroup}-${i}`);
  }

}

// TODO : refactor
export function setModelToForm(model: any, form: FormGroup) {

  const keys               = Object.keys(model);
  const controlNames       = keys.filter((key) => typeof model[key] === 'string' || typeof model[key] === 'number');
  const subGroupNames      = keys.filter((key) => typeof model[key] === 'object' && !Array.isArray(model[key]));
  const multiSubGroupNames = keys.filter((key) => Array.isArray(model[key]));

  for (const controlName of controlNames) {

    expect(form.controls).has.ownProperty(controlName);
    const control = form.controls[controlName];
    expect(control).to.be.instanceof(GenericFormControl);
    if (control instanceof GenericFormControl) {
      control.setModelValue(model[controlName]);
    } else {
      throw new Error('that should not happen XD');
    }

  }

  for (const subGroupName of subGroupNames) {
    expect(form.controls).has.ownProperty(subGroupName);
    expect(form.controls[subGroupName]).to.be.instanceOf(FormGroup);
    const subFormGroup = form.controls[subGroupName];
    if (subFormGroup instanceof FormGroup) {
      setModelToForm(model[subGroupName], subFormGroup);
    } else {
      throw new Error('that should not happen XD');
    }
  }

  // TODO : simpler recursion like validators
  for (const multiSubGroupName of multiSubGroupNames) {
    expect(form.controls).has.ownProperty(multiSubGroupName);
    expect(form.controls[multiSubGroupName]).to.be.instanceOf(FormArray);
    const multiFormSubGroup: FormArray = form.controls[multiSubGroupName] as any;
    for (let i = 0; i < multiFormSubGroup.controls.length; i++) {
      const subFormGroup = multiFormSubGroup.controls[i];
      expect(subFormGroup).to.be.instanceOf(FormGroup);
      if (subFormGroup instanceof FormGroup) {
        setModelToForm(model[multiSubGroupName][i], subFormGroup);
      } else {
        throw new Error('that should not happen XD');
      }
    }
  }

}

export function checkControl(abstractControl: AbstractControl, key: string = 'root') {

  if (abstractControl instanceof FormControl) {

    expect(
      abstractControl.valid,
      `form control '${key}' should be valid, errors: '${JSON.stringify(abstractControl.errors)}', value: '${JSON.stringify(
        abstractControl.value)}'`,
    ).to.be.true;

  } else if (abstractControl instanceof FormGroup) {

    for (const [controlName, control] of (Object as any).entries(abstractControl.controls)) {
      checkControl(control, `${key}.${controlName}`);
    }
    expect(abstractControl.valid, `form group '${key}' should be valid if all controls are valid!!!`).to.be.true;

  } else if (abstractControl instanceof FormArray) {

    for (let i = 0; i < abstractControl.controls.length; i++) {
      checkControl(abstractControl.controls[i], `${key}.${i}`);
    }
    expect(abstractControl.valid, `from array '${key}' should be valid if all controls are valid!!!`).to.be.true;
  }

}

export function compareObject(a: any, b: any, parent: string = 'root'): void {

  const keys      = Object.keys(a);
  const valueKeys = keys.filter((key) => typeof a[key] === 'string' || typeof a[key] === 'number');
  const arrayKeys = keys.filter((key) => Array.isArray(a[key]));
  const objKeys   = keys.filter((key) => typeof a[key] === 'object' && !Array.isArray(a[key]));

  for (const valueKey of valueKeys) {

    expect(b, `the property '${parent}.${valueKey}' is missing`).has.ownProperty(valueKey);
    expect(b[valueKey], `the value form '${parent}.${valueKey}' is not equal`).to.eq(a[valueKey]);

  }

  for (const arrayKey of arrayKeys) {
    expect(b, `the property '${parent}.${arrayKey}' is missing`).has.ownProperty(arrayKey);
    expect(b[arrayKey], `the array from '${parent}.${arrayKey}' has only '${b[arrayKey]}' elements`).to.have
      .length(a[arrayKey].length);
    for (let i = 0; i < a[arrayKey].length; i++) {
      compareObject(a[arrayKey][i], b[arrayKey][i], `${parent}.${arrayKey}.${i}`);
    }
  }

  for (const objKey of objKeys) {
    expect(b, `the property '${parent}.${objKey}' is missing`).has.ownProperty(objKey);
    compareObject(a[objKey], b[objKey], `${parent}.${objKey}`);
  }


}

export function checkModel(model: any, form: GenericFormGroup<any>) {

  checkControl(form);

  compareObject(model, form.model);

}

export function checkValues(model: any, form: GenericFormGroup<any>) {

  checkControl(form);

  compareObject(model, form.value);

}

export function submitAndCheckModel(
  model: any,
  submit: () => void,
  fixture: ComponentFixture<any>,
  valid: boolean = true,
): void {

  fixture.detectChanges();

  submit();

  const component = fixture.componentInstance;

  if (valid) {

    checkControl(component.form);

    expect(component.form.valid, `Form should be valid if all controls are valid`).to.be.true;

  } else {

    expect(component.form.invalid, 'Form should be invalid after submit').to.be.true;

  }

  compareObject(model, component.form.model);

}
