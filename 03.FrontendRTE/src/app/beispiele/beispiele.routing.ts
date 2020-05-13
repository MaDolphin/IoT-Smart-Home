import { RouterModule, Routes } from '@angular/router';
import { BeispieleDatatablesGenComponent } from './beispiele-datatables-gen.component';
import { BeispieleImagesGenComponent } from './beispiele-images-gen.component';
import { BeispieleLayoutGenComponent } from './beispiele-layout-gen.component';
import { BeispieleHorizontalbarchartsGenComponent } from "@targetgui/beispiele-horizontalbarcharts-gen.component/beispiele-horizontalbarcharts-gen.component";
import { BeispieleBarchartsGenComponent } from "./beispiele-barcharts-gen.component";
import { BeispielePiechartsGenComponent } from "@targetgui/beispiele-piecharts-gen.component/beispiele-piecharts-gen.component";
import { BeispieleTimelinechartsGenComponent } from "./beispiele-timelinecharts-gen.component";
import { BeispieleChartsGenComponent } from "@targetgui/beispiele-charts-gen.component/beispiele-charts-gen.component";
import { BeispieleLinechartsGenComponent } from "./beispiele-linecharts-gen.component";
import { BeispieleDensitychartsGenComponent } from "./beispiele-densitycharts-gen.component";
import { BeispieleDatatablesInfoGenComponent } from "@targetgui/beispiele-datatables-info-gen.component/beispiele-datatables-info-gen.component";
import { BeispieleRowdatatablesGenComponent } from "./beispiele-rowdatatables-gen.component";
import { BeispieleInputGenComponent } from "@targetgui/beispiele-input-gen.component/beispiele-input-gen.component";
import { BeispieleButtonsGenComponent } from "./beispiele-buttons-gen.component";
import { BeispieleTextinputsGenComponent } from "./beispiele-textinputs-gen.component";
import { BeispieleAutocompletesAndDropdownsGenComponent } from "./beispiele-autocompletes-and-dropdowns-gen.component";
import { BeispieleLabelsGenComponent } from "./beispiele-labels-gen.component";
import { BeispieleLayout1GenComponent } from "@targetgui/beispiele-layout-1-gen.component/beispiele-layout-1-gen.component";
import { BeispieleLayout2GenComponent } from "@targetgui/beispiele-layout-2-gen.component/beispiele-layout-2-gen.component";
import { BeispieleLayout3GenComponent } from "@targetgui/beispiele-layout-3-gen.component/beispiele-layout-3-gen.component";
import { BeispieleLayout4GenComponent } from "@targetgui/beispiele-layout-4-gen.component/beispiele-layout-4-gen.component";
import { BeispieleLayout5GenComponent } from "@targetgui/beispiele-layout-5-gen.component/beispiele-layout-5-gen.component";
import { BeispieleInputOtherGenComponent } from "@targetgui/beispiele-input-other-gen.component/beispiele-input-other-gen.component";

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'datentabellen',
    pathMatch: 'full',
  },
  {
    path: 'datatablesinfo',
    component: BeispieleDatatablesInfoGenComponent,
  },
  {
    path: 'datatables',
    component: BeispieleDatatablesGenComponent,
  },
  {
    path: 'rowdatatables',
    component: BeispieleRowdatatablesGenComponent,
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
    path: 'densitycharts',
    component: BeispieleDensitychartsGenComponent,
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
    component: BeispieleInputGenComponent
  },
  {
    path: 'buttons',
    component: BeispieleButtonsGenComponent
  },
  {
    path: 'other',
    component: BeispieleInputOtherGenComponent
  },
  {
    path: 'images',
    component: BeispieleImagesGenComponent,
  },
  {
    path: 'textinputs',
    component: BeispieleTextinputsGenComponent
  },
  {
    path: 'autocompletes',
    component: BeispieleAutocompletesAndDropdownsGenComponent
  },
  {
    path: 'labels',
    component: BeispieleLabelsGenComponent
  },
  {
    path: 'layout',
    component: BeispieleLayoutGenComponent,
  },
  {
    path: 'layout1',
    component: BeispieleLayout1GenComponent,
  },
  {
    path: 'layout2',
    component: BeispieleLayout2GenComponent,
  },
  {
    path: 'layout3',
    component: BeispieleLayout3GenComponent,
  },
  {
    path: 'layout4',
    component: BeispieleLayout4GenComponent,
  },
  {
    path: 'layout5',
    component: BeispieleLayout5GenComponent,
  }
];

export const routing = RouterModule.forChild(routes);
