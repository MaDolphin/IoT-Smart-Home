/* (c) https://github.com/MontiCore/monticore */
import { RouterModule, Routes } from '@angular/router';
import { BeispieleDatentabellenGenComponent } from './beispiele-datentabellen-gen.component';
import { BeispieleInputGenComponent } from './beispiele-input-gen.component';
import { BeispieleLayoutGenComponent } from './beispiele-layout-gen.component';
import { BeispieleNavigationGenComponent } from './beispiele-navigation-gen.component';
import { BeispieleHorizontalbarchartsGenComponent } from "@targetgui/beispiele-horizontalbarcharts-gen.component/beispiele-horizontalbarcharts-gen.component";
import { BeispieleBarchartsGenComponent } from "./beispiele-barcharts-gen.component";
import { BeispielePiechartsGenComponent } from "@targetgui/beispiele-piecharts-gen.component/beispiele-piecharts-gen.component";
import { BeispieleTimelinechartsGenComponent } from "./beispiele-timelinecharts-gen.component";
import { BeispieleChartsGenComponent } from "@targetgui/beispiele-charts-gen.component/beispiele-charts-gen.component";
import { BeispieleLinechartsGenComponent } from "./beispiele-linecharts-gen.component";

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
    path: 'linecharts',
    component: BeispieleLinechartsGenComponent,
  },
  {
    path: 'horizontalbarcharts',
    component: BeispieleHorizontalbarchartsGenComponent
  },
  {
    path: 'barcharts/:id',
    component: BeispieleBarchartsGenComponent
  },
  {
    path: 'piecharts/:id',
    component: BeispielePiechartsGenComponent
  },
  {
    path: 'timelinecharts/:id',
    component: BeispieleTimelinechartsGenComponent
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
