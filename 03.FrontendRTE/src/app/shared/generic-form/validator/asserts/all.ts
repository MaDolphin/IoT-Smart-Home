import { flag } from '../utils/flag';

export function all() {
  flag(this, 'all', true);
  flag(this, 'any', false);
}