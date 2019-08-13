/* (c) https://github.com/MontiCore/monticore */

import { flag } from '../utils/flag';

export function NaN() {
  this.assert(
    isNaN(flag(this, 'object'))
    , 'expected #{this} to be NaN'
    , 'expected #{this} not to be NaN',
  );
}
