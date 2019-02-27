/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Injectable } from '@angular/core';
import { GenericFormControl } from '@shared/generic-form/generic-form';
import { IGenericFormGroup } from "@shared/generic-form/generic-form/generic-form-group.interface";

@Injectable()
export class LabelFormControl<G extends IGenericFormGroup = IGenericFormGroup> extends GenericFormControl<string[], G> {


  public getModelValue(): string[] {
    return this.value;
  }

  public setModelValue(value: string[]) {
    if (value) {
      this.setValue(value);
    }
  }

}
