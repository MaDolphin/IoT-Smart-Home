/* (c) https://github.com/MontiCore/monticore */

import { animate, state, style, transition, trigger } from '@angular/animations';

export const animations = [
  trigger('loadingState', [
    state('inactive', style({
      display: 'none',
    })),
    state('active', style({
      display: 'block',
    })),
    transition('inactive => active', animate('1000ms ease-in')),
    transition('active => inactive', animate('1000ms ease-out')),
  ]),
];
