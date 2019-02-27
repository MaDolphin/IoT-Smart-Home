/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { FormControl } from '@angular/forms';
import { Assertion } from '../assertion';
import { flag } from '../utils/flag';

export const MONEY_REGEX = /^[+-]?[0-9]{1,3}(?:\.?[0-9]{3})*(?:,[0-9]{0,2})?$/;

export function isMoney(moneyString: string): boolean {
  return MONEY_REGEX.test(moneyString);
}

export function assertMoney(this: Assertion) {
  const control: FormControl = flag(this, 'object') || this._obj;

  flag(this, 'money', true);

  this.assert(!control.value || isMoney(control.value), 'Bitte geben Sie einen korrekten Betrag ein');

}
