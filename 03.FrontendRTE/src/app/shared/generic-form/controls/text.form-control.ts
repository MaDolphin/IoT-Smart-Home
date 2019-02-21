import { Injectable } from '@angular/core';
import { GenericFormControl } from '@shared/generic-form/generic-form';
import { IGenericFormGroup } from "@shared/generic-form/generic-form/generic-form-group.interface";

@Injectable()
export class TextFormControl<G extends IGenericFormGroup = IGenericFormGroup> extends GenericFormControl<string, G> {
}
