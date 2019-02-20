import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatDialogModule, MatToolbarModule } from '@angular/material';
import { GeneralDialogComponent } from '@shared/utils/dialog/general-dialog/app-dialog.component';

import { NotificationService } from './notification.service';
import { NotifactionUmbuchenComponent } from '@shared/utils/dialog/umbuchen/notifaction-umbuchen.component';
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
    NotifactionUmbuchenComponent
  ],
  entryComponents: [
    GeneralDialogComponent,
    NotifactionUmbuchenComponent
  ],
  providers: [NotificationService],
})
export class NotificationModule {
}
