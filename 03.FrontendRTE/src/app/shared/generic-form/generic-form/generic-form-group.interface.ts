/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { FormGroup, ValidationErrors } from '@angular/forms';

export interface IGenericFormGroup<T = any> extends FormGroup {

  value: any;

  model: T;

  init(): IGenericFormGroup<T>;

  beforeSubmit(): boolean;

  submit(): boolean;

  afterSubmit(): boolean;

  beforeValidate(): void;

  validate(): ValidationErrors[] | ValidationErrors | null;

  afterValidate(): void;
}
