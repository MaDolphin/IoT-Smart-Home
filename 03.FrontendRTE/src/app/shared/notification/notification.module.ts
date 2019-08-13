/* (c) https://github.com/MontiCore/monticore */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatDialogModule, MatToolbarModule } from '@angular/material';
import { MontiGemDialogComponent } from '@shared/utils/dialog/general-dialog/montigem-dialog.component';

import { NotificationService } from './notification.service';
import { InputModule } from '@shared/components/guidsl/input/input.module';
import { FlexLayoutModule } from '@angular/flex-layout';

@NgModule({
  imports: [
    MatDialogModule,
    CommonModule,
    MatToolbarModule,
    InputModule,
    FlexLayoutModule,
  ],
  exports: [],
  declarations: [
    MontiGemDialogComponent,
  ],
  entryComponents: [
    MontiGemDialogComponent,
  ],
  providers: [NotificationService],
})
export class NotificationModule {
}
