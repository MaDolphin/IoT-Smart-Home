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
import { BeispieleImagesGenComponent } from './beispiele-images-gen.component';
import { BeispieleLayoutGenComponent } from './beispiele-layout-gen.component';
import { BeispieleHorizontalbarchartsGenComponent } from "@targetgui/beispiele-horizontalbarcharts-gen.component/beispiele-horizontalbarcharts-gen.component";
import { BeispielePiechartsGenComponent } from "@targetgui/beispiele-piecharts-gen.component/beispiele-piecharts-gen.component";
import { BeispieleTimelinechartsGenComponent } from "./beispiele-timelinecharts-gen.component";
import { BeispieleBarchartsGenComponent } from "./beispiele-barcharts-gen.component";
import { BeispieleChartsGenComponent } from "@targetgui/beispiele-charts-gen.component/beispiele-charts-gen.component";
import { BeispieleLinechartsGenComponent } from "./beispiele-linecharts-gen.component";
import { BeispieleDatatablesInfoGenComponent } from "@targetgui/beispiele-datatables-info-gen.component/beispiele-datatables-info-gen.component";
import { BeispieleRowdatatablesGenComponent } from "./beispiele-rowdatatables-gen.component";
import { BeispieleInputGenComponent } from "@targetgui/beispiele-input-gen.component/beispiele-input-gen.component";
import { BeispieleButtonsGenComponent } from "./beispiele-buttons-gen.component";
import { BeispieleTextinputsGenComponent } from "./beispiele-textinputs-gen.component";
import { BeispieleAutocompletesAndDropdownsGenComponent } from './beispiele-autocompletes-and-dropdowns-gen.component';
import { BeispieleLabelsGenComponent } from "./beispiele-labels-gen.component";
import { BeispieleLayout1GenComponent } from "@targetgui/beispiele-layout-1-gen.component/beispiele-layout-1-gen.component";
import { BeispieleLayout2GenComponent } from "@targetgui/beispiele-layout-2-gen.component/beispiele-layout-2-gen.component";
import { BeispieleLayout3GenComponent } from "@targetgui/beispiele-layout-3-gen.component/beispiele-layout-3-gen.component";
import { BeispieleLayout4GenComponent } from "@targetgui/beispiele-layout-4-gen.component/beispiele-layout-4-gen.component";
import { BeispieleLayout5GenComponent } from "@targetgui/beispiele-layout-5-gen.component/beispiele-layout-5-gen.component";
import { BeispieleInputOtherGenComponent } from "@targetgui/beispiele-input-other-gen.component/beispiele-input-other-gen.component";

@NgModule({
  declarations: [
    BeispieleDatatablesGenComponent,
    BeispieleChartsGenComponent,
    BeispieleInputGenComponent,
    BeispieleImagesGenComponent,
    BeispieleLayoutGenComponent,
    BeispieleHorizontalbarchartsGenComponent,
    BeispielePiechartsGenComponent,
    BeispieleTimelinechartsGenComponent,
    BeispieleBarchartsGenComponent,
    BeispieleLinechartsGenComponent,
    BeispieleRowdatatablesGenComponent,
    BeispieleDatatablesInfoGenComponent,
    BeispieleButtonsGenComponent,
    BeispieleTextinputsGenComponent,
    BeispieleAutocompletesAndDropdownsGenComponent,
    BeispieleLabelsGenComponent,
    BeispieleLayout1GenComponent,
    BeispieleLayout2GenComponent,
    BeispieleLayout3GenComponent,
    BeispieleLayout4GenComponent,
    BeispieleLayout5GenComponent,
    BeispieleInputOtherGenComponent
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
