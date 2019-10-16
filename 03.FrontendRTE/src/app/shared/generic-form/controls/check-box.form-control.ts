/* (c) https://github.com/MontiCore/monticore */

import { Injectable } from '@angular/core';
import { GenericFormControl } from '@shared/generic-form/generic-form';
import { IGenericFormGroup } from "@shared/generic-form/generic-form/generic-form-group.interface";

@Injectable()
export class CheckBoxFormControl<G extends IGenericFormGroup = IGenericFormGroup> extends GenericFormControl<boolean, G> {

  public getModelValue(): boolean {
    return this.value;
  }

  public setModelValue(modelValue: boolean) {
    this.setValue(modelValue);
  }

}
