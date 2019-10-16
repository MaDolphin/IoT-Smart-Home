/* (c) https://github.com/MontiCore/monticore */

import { flag } from '../utils/flag';

export function own() {
  flag(this, 'own', true);
}
