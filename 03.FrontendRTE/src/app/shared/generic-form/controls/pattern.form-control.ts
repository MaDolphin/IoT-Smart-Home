/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Injectable } from '@angular/core';
import { AddValidator } from '../generic-form/decorators/validator';
import { IGenericFormGroup } from '../generic-form/generic-form-group.interface';
import { ValidationError } from '../validator';
import { TextFormControl } from './text.form-control';

@Injectable()
export class PatternFormControl<G extends IGenericFormGroup = IGenericFormGroup> extends TextFormControl<G> {

  public pattern: RegExp;

  @AddValidator
  public matchPatternValidator() {
    if (!(!this.value || this.pattern.test(this.value))) {
      throw new ValidationError(this.pattern + '');
    }
  }

}
