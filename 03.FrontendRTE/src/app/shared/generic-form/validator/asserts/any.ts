/* (c) https://github.com/MontiCore/monticore */

import { flag } from '../utils/flag';

export function any() {
  flag(this, 'any', true);
  flag(this, 'all', false);
}
