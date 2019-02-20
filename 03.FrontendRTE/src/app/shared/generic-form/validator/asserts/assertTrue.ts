import { flag } from '../utils/flag';

export function assertTrue() {
  this.assert(
    true === flag(this, 'object')
    , 'expected #{this} to be true'
    , 'expected #{this} to be false',
    flag(this, 'negate') ? false : true,
  );
}