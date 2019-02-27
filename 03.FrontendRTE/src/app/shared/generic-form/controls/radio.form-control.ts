/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Injectable } from '@angular/core';
import { ERROR_MSG, REQUIRED } from '../generic-form/config';
import { extractPrototype } from '../generic-form/decorators/group/utils';
import { ISelectOptions } from '../generic-form/decorators/options';
import { AddValidator } from '../generic-form/decorators/validator';
import { IGenericFormGroup } from '../generic-form/generic-form-group.interface';
import { GenericFormControl } from '../generic-form/index';
import { ValidationError } from '../validator';

@Injectable()
export class RadioFormControl<G extends IGenericFormGroup = IGenericFormGroup> extends GenericFormControl<string, G> {

  private _options: ISelectOptions[] = [];

  public set options(opts: ISelectOptions[]) {
    this._options = opts;
    this.setOptionIfRequired();
  };

  public get options(): ISelectOptions[] {
    return this._options;
  }

  public getModelValue(): any {
    return this.value;
  }

  public setModelValue(modelValue: any) {
    this.setValue(modelValue);
  }

  public addOptions(options: (string | ISelectOptions)[]) {
    for (const opt of options) {
      if (typeof opt === 'string') {
        this.options.push({
          value: opt,
          option: opt,
        });
      } else {
        this.options.push(opt);
      }
    }
    this.setOptionIfRequired();
  }

  public setOptions(options: (string | ISelectOptions)[]) {
    this.options = [];
    this.addOptions(options);
  }

  @AddValidator
  public requiredValidator() {
    if (this.isRequired && (!this.value || !this.options.find((opt: ISelectOptions) => opt.value === this.value))) {
      throw new ValidationError(Reflect.getMetadata(REQUIRED + ERROR_MSG, extractPrototype(this.parent), this.propertyKey) || 'required');
    }
  }

  private setOptionIfRequired() {

    if (Array.isArray(this.options) && this.options.length === 1) {
      let required = false;

      // generated "required" validation check
      for (let func of this.validatorFunctions) {
        try {
          func.call(this);
        } catch (error) {
          if (!this.value) {
            required = true;
            break;
          }
        }
      }
      if (required) {
        this.setModelValue(this.options[0].value);
      }
    }
  }
}
