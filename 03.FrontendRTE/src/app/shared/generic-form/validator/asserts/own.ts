import { flag } from '../utils/flag';

export function own() {
  flag(this, 'own', true);
}