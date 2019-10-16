/* (c) https://github.com/MontiCore/monticore */
import { GenericFormGroup } from '@shared/generic-form/ngx-forms';
import { EinstellungenBenutzerTabelleEntryDTO } from '@einstellungenbenutzertabelleentry-dto/einstellungenbenutzertabelleentry.dto';
import { CheckBoxFormControl, TextFormControl } from '@shared/generic-form/controls';

export interface IEinstellungenBenutzerForm extends GenericFormGroup<EinstellungenBenutzerTabelleEntryDTO> {
  id: TextFormControl;

  elements: EinstellungenBenutzerTabelleEntryDTO[];

  _istAktiv: CheckBoxFormControl;

  _username: TextFormControl;

  _initials: TextFormControl;

  _email: TextFormControl;
}
