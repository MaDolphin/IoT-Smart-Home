import { Control, Group, Validator } from "@shared/generic-form/generic-form/decoretors";
import { EinstellungenBenutzerTabelleEntryDTO } from "@targetdtos/einstellungenbenutzertabelleentry.dto";
import { TextFormControl } from "@shared/generic-form/controls";
import { validate_email, validate_initials, validate_username } from "./einstellungenbenutzer.form.validators";
import { EinstellungenBenutzerFormTOP } from "@targetgui/einstellungen-benutzer-gen.component/einstellungenbenutzer-top.form";

@Group()
export class EinstellungenBenutzerForm extends EinstellungenBenutzerFormTOP {

  public elements: EinstellungenBenutzerTabelleEntryDTO[] = [];

  @Control('email')
  @Validator(validate_email)
  public _email: TextFormControl = undefined;

  @Control('initials')
  @Validator(validate_initials)
  public _initials: TextFormControl = undefined;

  @Control('username')
  @Validator(validate_username)
  public _username: TextFormControl = undefined;

  public initializeValues() {
    this._istAktiv.setModelValue(true);
    this._istAktiv.label = '';
  }

  public reset() {
    super.reset();
    this.initializeValues();
  }

}