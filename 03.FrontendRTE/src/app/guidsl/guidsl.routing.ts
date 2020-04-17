/* (c) https://github.com/MontiCore/monticore */

import { RouterModule, Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'dashboard',
  },
];

export const routing = RouterModule.forChild(routes);
