/* (c) https://github.com/MontiCore/monticore */

import { Injectable } from '@angular/core';
import { IGenericFormGroup } from '../generic-form/generic-form-group.interface';
import { GenericFormControl } from '../generic-form/index';

@Injectable()
export class AutoCompleteFormControl<G extends IGenericFormGroup = IGenericFormGroup> extends GenericFormControl<string, G> {

  public options: string[] = [];

  public addOptions(options: (string)[]) {
    for (const opt of options) {
        this.options.push(opt);
    }
  }

  public setOptions(options: (string)[]) {
    this.options = [];
    this.addOptions(options);
  }

  public allowNewInput: boolean = false;
  public filterByInput: boolean = true;

  public get filteredOptions() {
    if (this.options) {

      if (this.filterByInput && this.value) {
        if (this.options.includes(this.value) || this.allowNewInput) {
          return this.options;
        }
        return this.options.filter((item) => new RegExp(`^${this.value}`, 'gi').test(item));
      }

      return this.options;

    } else {
      return [];
    }
  }

/*
  @AddValidator
  public requiredValidator() {
    if (this.isRequired && (!this.value || !this.options.find((opt: ISelectOptions) => opt.value === this.value))) {
      throw new ValidationError(Reflect.getMetadata(REQUIRED + ERROR_MSG, extractPrototype(this.parent), this.propertyKey) || 'required');
    }
  }
*/
}
