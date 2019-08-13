/* (c) https://github.com/MontiCore/monticore */

import { flag } from '../utils/flag';

export function assertUndefined() {
  this.assert(
    undefined === flag(this, 'object')
    , 'expected #{this} to be undefined'
    , 'expected #{this} not to be undefined',
  );
}
