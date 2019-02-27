/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Assertion } from '../assertion';
import { pattern } from './pattern';

export function email(this: Assertion) {
  pattern.apply(
    this,
    [
      /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
      'an valid email should not be entered',
    ],
  );
}