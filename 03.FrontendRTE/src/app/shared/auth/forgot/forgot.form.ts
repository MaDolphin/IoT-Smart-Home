/* (c) https://github.com/MontiCore/monticore */

import { Injectable } from '@angular/core';
import { Control, Group } from '@shared/generic-form/generic-form/decoretors';
import { GenericFormGroup } from '@shared/generic-form/generic-form';
import { SelectFormControl } from '@shared/generic-form/controls/select.form-control';

@Injectable()
@Group()
export class ForgotPasswordForm extends GenericFormGroup<ForgotPasswordForm> {
  @Control()
  public datenbank: SelectFormControl = undefined;

}
