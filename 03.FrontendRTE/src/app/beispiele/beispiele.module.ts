/* (c) https://github.com/MontiCore/monticore */
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material';
import { ButtonDirectivesModule, FormDirectivesModule, PanelDirectivesModule } from '@upe/ngx-bootstrap-directives';
import { ContextMenuModule, ContextMenuService } from 'ngx-contextmenu';
import { PipesModule } from '@shared/pipes';
import { routing } from './beispiele.routing';
import { GuiDslComponentsModule } from '@shared/components/guidsl/guidsl-components.module';
import { InputModule } from '@shared/components/guidsl/input/input.module';
import { BeispieleDatatablesGenComponent } from './beispiele-datatables-gen.component';
import { BeispieleInputGenComponent } from './beispiele-input-gen.component';
import { BeispieleLayoutGenComponent } from './beispiele-layout-gen.component';
import { BeispieleNavigationGenComponent } from './beispiele-navigation-gen.component';
import { BeispieleHorizontalbarchartsGenComponent } from "@targetgui/beispiele-horizontalbarcharts-gen.component/beispiele-horizontalbarcharts-gen.component";
import { BeispielePiechartsGenComponent } from "@targetgui/beispiele-piecharts-gen.component/beispiele-piecharts-gen.component";
import { BeispieleTimelinechartsGenComponent } from "./beispiele-timelinecharts-gen.component";
import { BeispieleBarchartsGenComponent } from "./beispiele-barcharts-gen.component";
import { BeispieleChartsGenComponent } from "@targetgui/beispiele-charts-gen.component/beispiele-charts-gen.component";
import { BeispieleLinechartsGenComponent } from "./beispiele-linecharts-gen.component";
import { BeispieleDatatablesInfoGenComponent } from "@targetgui/beispiele-datatables-info-gen.component/beispiele-datatables-info-gen.component";
import { BeispieleRowdatatablesGenComponent } from "./beispiele-rowdatatables-gen.component";

@NgModule({
  declarations: [
    BeispieleDatatablesGenComponent,
    BeispieleChartsGenComponent,
    BeispieleInputGenComponent,
    BeispieleLayoutGenComponent,
    BeispieleNavigationGenComponent,
    BeispieleHorizontalbarchartsGenComponent,
    BeispielePiechartsGenComponent,
    BeispieleTimelinechartsGenComponent,
    BeispieleBarchartsGenComponent,
    BeispieleLinechartsGenComponent,
    BeispieleRowdatatablesGenComponent,
    BeispieleDatatablesInfoGenComponent
  ],
  imports: [
    MatIconModule,
    CommonModule,
    ReactiveFormsModule,
    FlexLayoutModule,
    ButtonDirectivesModule,
    FormDirectivesModule,
    InputModule,
    GuiDslComponentsModule,
    InputModule,
    PanelDirectivesModule,
    routing,
    PipesModule,
    ContextMenuModule,
  ],
  providers: [
    ContextMenuService,
  ],
  entryComponents: []
})
export class BeispieleModule {
}
