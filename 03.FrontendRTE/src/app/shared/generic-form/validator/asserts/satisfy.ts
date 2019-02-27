/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { flag } from '../utils/flag';
import { objDisplay } from '../utils/objDisplay';

export function satisfy(matcher, msg) {
  if (msg) {
    flag(this, 'message', msg);
  }
  const obj    = flag(this, 'object');
  const result = matcher(obj);
  this.assert(
    result
    , 'expected #{this} to satisfy ' + objDisplay(matcher)
    , 'expected #{this} to not satisfy' + objDisplay(matcher)
    , flag(this, 'negate') ? false : true
    , result,
  );
}