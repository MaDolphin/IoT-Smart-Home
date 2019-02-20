import { Assertion } from '../assertion';
import { assertEqual } from './equal';

export function checked(this: Assertion) {
  assertEqual.apply(this, [true, 'control should be not checked']);
}
