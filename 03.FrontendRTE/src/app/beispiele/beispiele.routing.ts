/* (c) https://github.com/MontiCore/monticore */
import { RouterModule, Routes } from '@angular/router';
import { BeispieleDatentabellenGenComponent } from './beispiele-datentabellen-gen.component';
import { BeispieleChartsGenComponent } from './beispiele-charts-gen.component';
import { BeispieleInputGenComponent } from './beispiele-input-gen.component';
import { BeispieleLayoutGenComponent } from './beispiele-layout-gen.component';
import { BeispieleNavigationGenComponent } from './beispiele-navigation-gen.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'datentabellen',
    pathMatch: 'full',
  },
  {
    path: 'datentabellen',
    component: BeispieleDatentabellenGenComponent,
  },
  {
    path: 'charts',
    component: BeispieleChartsGenComponent,
  },
  {
    path: 'input',
    component: BeispieleInputGenComponent,
  },
  {
    path: 'layout',
    component: BeispieleLayoutGenComponent,
  },
  {
    path: 'navigation',
    component: BeispieleNavigationGenComponent,
  }
];

export const routing = RouterModule.forChild(routes);
