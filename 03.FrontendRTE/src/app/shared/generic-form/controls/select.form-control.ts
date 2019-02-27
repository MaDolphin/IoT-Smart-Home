/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Injectable } from '@angular/core';
import { ERROR_MSG, REQUIRED } from '../generic-form/config';
import { extractPrototype } from '../generic-form/decoretors/group/utils';
import { ISelectOptions } from '../generic-form/decoretors/options';
import { AddValidator } from '../generic-form/decoretors/validator';
import { IGenericFormGroup } from '../generic-form/generic-form-group.interface';
import { GenericFormControl } from '../generic-form/index';
import { ValidationError } from '../validator';

@Injectable()
export class SelectFormControl<G extends IGenericFormGroup = IGenericFormGroup> extends GenericFormControl<string, G> {

  private _options: ISelectOptions[] = [];
  private _defaultValue: string;

  public set options(opts: ISelectOptions[]) {
    this._options = opts;
    this.setOptionIfRequired();
  };

  public get options(): ISelectOptions[] {
    return this._options;
  }

  public set defaultValue(value: string) {
    this._defaultValue = value;
    this.setDefaultOption();
  }

  public get defaultValue(): string {
    return this._defaultValue;
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
    this.setDefaultOption();
  }

  public setOptions(options: (string | ISelectOptions)[]) {
    this.options = [];
    this.addOptions(options);
  }

  public resetOptions(options?: (string | ISelectOptions)[]) {
    this.setValue('');
    if (options !== undefined) {
      this.setOptions(options);
    } else {
      this.setOptions(this.options);
    }
  }

  @AddValidator
  public requiredValidator() {
    if (this.isRequired && (!this.value || !this.options.find((opt: ISelectOptions) => opt.value === this.value))) {
      throw new ValidationError(Reflect.getMetadata(REQUIRED + ERROR_MSG, extractPrototype(this.parent), this.propertyKey) || 'required');
    }
  }

  reset(formState: string | null = null, options?: {
    onlySelf?: boolean;
    emitEvent?: boolean;
  }): void {
    super.reset(formState, options);
    this.setOptionIfRequired();
    this.setDefaultOption();
  }

  private setDefaultOption() {
    let selectOption = this.options.find(o => o.option === this._defaultValue);
    if (selectOption !== undefined) {
      this.setModelValue(selectOption.value);
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
