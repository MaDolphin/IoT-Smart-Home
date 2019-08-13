/* (c) https://github.com/MontiCore/monticore */
import { ValidationError } from "@shared/generic-form/validator";
import { TextFormControl } from "@shared/generic-form/controls";
import { IEinstellungenBenutzerForm } from "./einstellungenbenutzer.form.interface";

// @TODO move to correct folder
export function validate_initials(this: TextFormControl<IEinstellungenBenutzerForm>) {
  if (this.parent.elements && !this.parent.elements
    .every(m => this.value !== m.initials || this.parent.id.value == m.id)) {
    throw new ValidationError('nicht eindeutig\n');
  }
}

export function validate_username(this: TextFormControl<IEinstellungenBenutzerForm>) {
  if (this.parent.elements && !this.parent.elements
    .every(m => this.value !== m.username || this.parent.id.value == m.id)) {
    throw new ValidationError('nicht eindeutig\n');
  }
}

export function validate_email(this: TextFormControl<IEinstellungenBenutzerForm>) {
  if (this.parent.elements && !this.parent.elements
    .every(m => this.value !== m.email || this.parent.id.value == m.id)) {
    throw new ValidationError('nicht eindeutig\n');
  }
}
