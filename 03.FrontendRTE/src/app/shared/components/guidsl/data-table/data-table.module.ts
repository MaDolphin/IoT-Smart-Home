/* (c) https://github.com/MontiCore/monticore */

import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FlexLayoutModule} from '@angular/flex-layout';

import {
  MatButtonModule, MatButtonToggleModule,
  MatCheckboxModule,
  MatDialogModule,
  MatIconModule,
  MatInputModule, MatListModule,
  MatMenuModule,
  MatPaginatorModule, MatRadioModule,
  MatSortModule,
  MatTableModule,
  MatSlideToggleModule,
} from '@angular/material';
import {NgxDatatableModule} from '@swimlane/ngx-datatable';
import {TextMaskModule} from 'angular2-text-mask';
import {ContextMenuModule} from 'ngx-contextmenu';
import {ApplyPipePipe} from './apply-pipe.pipe';
import {DataTableComponent} from './data-table.component';
import {XLSXService} from './XLSX.service';
import {InputModule} from "@shared/components/guidsl/input/input.module";
import {PipesModule} from '@shared/pipes';

@NgModule({
  declarations: [
    DataTableComponent,
    ApplyPipePipe,
  ],
  imports: [
    CommonModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatInputModule,
    MatButtonModule,
    TextMaskModule,
    MatMenuModule,
    MatListModule,
    MatIconModule,
    MatCheckboxModule,
    MatRadioModule,
    MatDialogModule,
    NgxDatatableModule,
    ContextMenuModule,
    FlexLayoutModule,
    InputModule,
    PipesModule,
    MatButtonToggleModule,
    MatSlideToggleModule
  ],
  exports: [
    DataTableComponent,
    NgxDatatableModule,
  ],
  providers: [
    XLSXService,
  ],
})
export class DataTableModule {
}
