import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatDialogModule, MatToolbarModule } from '@angular/material';
import { GeneralDialogComponent } from '@shared/utils/dialog/general-dialog/general-dialog.component';

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
    GeneralDialogComponent,
  ],
  entryComponents: [
    GeneralDialogComponent,
  ],
  providers: [NotificationService],
})
export class NotificationModule {
}
