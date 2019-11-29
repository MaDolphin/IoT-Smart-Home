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
import { BeispieleDatentabellenGenComponent } from './beispiele-datentabellen-gen.component';
import { BeispieleChartsGenComponent } from './beispiele-charts-gen.component';
import { BeispieleInputGenComponent } from './beispiele-input-gen.component';
import { BeispieleLayoutGenComponent } from './beispiele-layout-gen.component';
import { BeispieleNavigationGenComponent } from './beispiele-navigation-gen.component';

@NgModule({
  declarations: [
    BeispieleDatentabellenGenComponent,
    BeispieleChartsGenComponent,
    BeispieleInputGenComponent,
    BeispieleLayoutGenComponent,
    BeispieleNavigationGenComponent
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
