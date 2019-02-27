/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { flag } from '../utils/flag';

export function assertNull() {
  this.assert(
    null === flag(this, 'object')
    , 'expected #{this} to be null'
    , 'expected #{this} not to be null',
  );
}