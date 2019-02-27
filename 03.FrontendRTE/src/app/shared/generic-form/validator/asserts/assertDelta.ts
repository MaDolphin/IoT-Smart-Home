/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { flag } from '../utils/flag';

export function assertDelta(delta, msg) {
  if (msg) {
    flag(this, 'message', msg);
  }

  const msgObj    = flag(this, 'deltaMsgObj');
  const initial   = flag(this, 'initialDeltaValue');
  const final     = flag(this, 'finalDeltaValue');
  const behavior  = flag(this, 'deltaBehavior');
  const realDelta = flag(this, 'realDelta');

  let expression;
  if (behavior === 'change') {
    expression = Math.abs(final - initial) === Math.abs(delta);
  } else {
    expression = realDelta === Math.abs(delta);
  }

  this.assert(
    expression
    , 'expected ' + msgObj + ' to ' + behavior + ' by ' + delta
    , 'expected ' + msgObj + ' to not ' + behavior + ' by ' + delta,
  );
}