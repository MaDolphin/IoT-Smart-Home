import { Injectable } from '@angular/core';
import { AddValidator } from '@shared/generic-form/generic-form/decorators/validator';
import { GenericFormControl } from '@shared/generic-form/generic-form';
import { IGenericFormGroup } from '@shared/generic-form/generic-form/generic-form-group.interface';
import { validate } from '../validator';
import { toNumber } from '../validator/asserts/number';

@Injectable()
export class NumberFormControl<G extends IGenericFormGroup = IGenericFormGroup> extends GenericFormControl<number, G> {

  public getModelValue(): any {
    return toNumber(this.value);
  }

  public setModelValue(modelValue: number) {
    this.setValue(modelValue);
  }

  @AddValidator
  public isNumberValidator() {
    validate(this).is.number;
  }

}
