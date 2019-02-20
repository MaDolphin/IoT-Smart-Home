import { deepEqual } from '../utils/deep-eql';
import { flag } from '../utils/flag';

export function assertEql(obj, msg) {
  if (msg) {
    flag(this, 'message', msg);
  }
  this.assert(
    deepEqual(obj, flag(this, 'object'))
    , 'expected #{this} to deeply equal #{exp}'
    , 'expected #{this} to not deeply equal #{exp}'
    , obj
    , this._obj
    , true,
  );
}