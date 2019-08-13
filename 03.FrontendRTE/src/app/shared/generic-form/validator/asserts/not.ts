/* (c) https://github.com/MontiCore/monticore */

import { flag } from '../utils/flag';

export function not() {
  flag(this, 'negate', true);
}
