/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule, MatCardModule, MatExpansionModule, MatIconModule, MatTabsModule } from '@angular/material';
import { ButtonDirectivesModule, FormDirectivesModule, PanelDirectivesModule } from '@upe/ngx-bootstrap-directives';
import { ChartsModule } from 'ng2-charts';
import { ContextMenuModule, ContextMenuService } from 'ngx-contextmenu';
import { LoadingModule } from '@shared/layout/loading/loading.module';
import { DownloadFileService } from '@shared/utils/download-file.service';
import { UtilsModule } from '@shared/utils/utils.module';
import { routing } from './guidsl.routing';
import { GuiDslComponentsModule } from '@shared/components/guidsl/guidsl-components.module';
import { InputModule } from '@shared/components/guidsl/input/input.module';
import { PipesModule } from '@shared/pipes';

@NgModule({
  declarations: [
  ],
  imports: [
    CommonModule,
    GuiDslComponentsModule,
    InputModule,
    routing,
    MatTabsModule,
    UtilsModule,
    FormsModule,
    ChartsModule,
    ContextMenuModule,
    MatButtonModule,
    ReactiveFormsModule,
    MatIconModule,
    MatButtonModule,
    LoadingModule,
    MatCardModule,
    FlexLayoutModule,
    MatExpansionModule,
    PanelDirectivesModule,
    ButtonDirectivesModule,
    FormDirectivesModule,
    PipesModule,
  ],
  providers: [
    DownloadFileService,
    ContextMenuService,
  ],
})
export class GuidslModule {

}
