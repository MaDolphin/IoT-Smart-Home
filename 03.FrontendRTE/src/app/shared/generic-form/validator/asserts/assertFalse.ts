import { flag } from '../utils/flag';

export function assertFalse() {
  this.assert(
    false === flag(this, 'object')
    , 'expected #{this} to be false'
    , 'expected #{this} to be true'
    , flag(this, 'negate') ? true : false,
  );
}