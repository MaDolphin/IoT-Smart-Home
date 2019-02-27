/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { flag } from '../utils/flag';
import { ValidationError } from '../validation.error';

export function closeTo(expected, delta, msg) {
  if (msg) {
    flag(this, 'message', msg);
  }
  const obj   = flag(this, 'object');
  let flagMsg = flag(this, 'message');
  const ssfi  = flag(this, 'ssfi');

  if (typeof expected !== 'number' || typeof delta !== 'number') {
    flagMsg = flagMsg ? flagMsg + ': ' : '';
    throw new ValidationError(flagMsg + 'the arguments to closeTo or approximately must be numbers');
  }

  this.assert(
    Math.abs(obj - expected) <= delta
    , 'expected #{this} to be close to ' + expected + ' +/- ' + delta
    , 'expected #{this} not to be close to ' + expected + ' +/- ' + delta,
  );
}