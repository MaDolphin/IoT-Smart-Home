/* (c) https://github.com/MontiCore/monticore */

import {RouterModule, Routes} from '@angular/router';
import {LineChartSampleComponent} from './line-chart-sample.component';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'linechart',
  },
  {
    path: 'linechart',
    component: LineChartSampleComponent
  }
];

export const routing = RouterModule.forChild(routes);
