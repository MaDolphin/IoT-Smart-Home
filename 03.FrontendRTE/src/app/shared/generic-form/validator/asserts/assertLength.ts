/* (c) https://github.com/MontiCore/monticore */

import { flag } from '../utils/flag';

export function assertLength(n, msg) {
  if (msg) {
    flag(this, 'message', msg);
  }
  const obj     = flag(this, 'object');
  const flagMsg = flag(this, 'message');
  const ssfi    = flag(this, 'ssfi');
  // TODO : catch if not has property length
  let len     = obj.length;

  if (len === undefined)
    len = obj.value.length;

  this.assert(
    len === n
    , 'expected #{this} to have a length of #{exp} but got #{act}'
    , 'expected #{this} to not have a length of #{act}'
    , n
    , len,
  );
}
