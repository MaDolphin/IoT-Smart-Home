/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { FormControl } from '@angular/forms';
import { default as moment } from 'moment/src/moment';
import { Assertion } from '../assertion';
import { flag } from '../utils/flag';

export const DATE_REGEX = /^(?:(?:(?:0?[13578]|1[02])(\/|-|\.)31)\1|(?:(?:0?[1,3-9]|1[0-2])(\/|-|\.)(?:29|30)\2))(?:(?:1[6-9]|[2-9]\d)?\d{2})$|^(?:0?2(\/|-|\.)29\3(?:(?:(?:1[6-9]|[2-9]\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:(?:0?[1-9])|(?:1[0-2]))(\/|-|\.)(?:0?[1-9]|1\d|2[0-8])\4(?:(?:1[6-9]|[2-9]\d)?\d{2})$/;

export const NORMAL_DATE_REGEX = /^[0-9]{2}\.[0-9]{2}\.[0-9]{4}$/;

export let DEFAULT_DATE_FORMAT = 'DD.MM.YYYY';

export let LANG = 'de';

export function setDefaultDateLang(lang) {
  LANG = lang;
}

export function SetDefaultDateFormat(format) {
  DEFAULT_DATE_FORMAT = format;
}

// TODO: this function should probably be replaced with a decorator, accepting
// format of string and error message as arguments
export function isInNormalDateFormat(dateString: string): boolean {
  return NORMAL_DATE_REGEX.test(dateString);
}

export function isDate(dateString: string | Date): boolean {
  return moment(dateString, DEFAULT_DATE_FORMAT, LANG, true).isValid();
}

export function toDate(dateString: string): Date | null {
  const momentDate = moment(dateString, DEFAULT_DATE_FORMAT, LANG, true);
  return momentDate.isValid() ? momentDate.toDate() : null;
}

export function toDateObject(dateString: string): Date | null {
  const momentDate = moment(dateString);
  return momentDate.isValid() ? momentDate.toDate() : null;
}


export function toDateString(date: Date): string {
  return moment(date).format(DEFAULT_DATE_FORMAT);
}

export function assertDate(this: Assertion) {
  const control: FormControl = flag(this, 'object') || this._obj;

  flag(this, 'date', true);

  this.assert(
    !control.value || isInNormalDateFormat(control.value) || control.value instanceof Date,
    'Bitte geben Sie ein gültiges Datum ein');
  this.assert(
    !control.value || isDate(control.value) || control.value instanceof Date,
    'Bitte geben Sie ein gültiges Datum ein');
}
