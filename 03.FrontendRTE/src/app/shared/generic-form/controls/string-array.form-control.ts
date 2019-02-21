import { Injectable } from '@angular/core';
import { GenericFormControl } from '@shared/generic-form/generic-form';
import { IGenericFormGroup } from "@shared/generic-form/generic-form/generic-form-group.interface";

@Injectable()
export class StringArrayFormControl<G extends IGenericFormGroup = IGenericFormGroup> extends GenericFormControl<string, G> {

  public separator: RegExp = /\s*,\s*/;
  public joiner: string = ', ';

  public getModelValue(): string[] {
    if (!this.value) {
      return [];
    }
    return this.value.split(this.separator);
  }

  public setModelValue(value: string[]) {
    if (!!value && !!value.length) {
      this.setValue(value.join(this.joiner));
    }
  }

}
