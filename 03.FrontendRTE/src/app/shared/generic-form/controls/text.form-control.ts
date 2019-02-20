import { Injectable } from '@angular/core';
import { GenericFormControl } from '@shared/generic-form/generic-form';
import { IGenericFormGroup } from '@shared/generic-form/generic-form-group.interface';
import { extractPrototype } from '@shared/generic-form/generic-form/decorators/group/untils';

@Injectable()
export class TextFormControl<G extends IGenericFormGroup = IGenericFormGroup> extends GenericFormControl<string, G> {
  /*
    @AddValidator
    public requiredValidator() {

      if (this.isRequired && (!this.value)) {
        throw new ValidationError(Reflect.getMetadata(REQUIRED + ERROR_MSG, extractPrototype(this.parent), this.propertyKey) || 'required');
      }

    }
       */
}
