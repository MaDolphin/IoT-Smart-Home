/* (c) https://github.com/MontiCore/monticore */

import { Injectable } from '@angular/core';
import { ISelectOptions } from '../generic-form/decoretors/options';
import { IGenericFormGroup } from '../generic-form/generic-form-group.interface';
import { SelectFormControl } from './select.form-control';

@Injectable()
export class SelectWithIdValueFormControl<G extends IGenericFormGroup = IGenericFormGroup>
  extends SelectFormControl<G> {

  public getModelValue() {
    const selectedOption: ISelectOptions | null =
      this.options.find((option: ISelectOptions) => option.value === this.value) || null;
    if (selectedOption === null) {
      return null;
    }
    return selectedOption.option;
  }

  public setModelValue(value: string) {
    if (this.parent.model.hasOwnProperty('id') && this.options.length) {
      this.setValue(this.parent.model.id + '');
    } else {
      console.warn('Form Group has not this member "id"');
    }
  }

}
