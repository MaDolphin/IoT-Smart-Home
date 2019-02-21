import { ModuleWithProviders, NgModule } from '@angular/core';
import { HttpModule } from '@angular/http';
import { MatDialogModule } from '@angular/material';
import { RouterModule } from '@angular/router';
import { LoadingModule } from '@shared/layout/loading/loading.module';
import { NotificationService } from '@shared/notification/notification.service';
import { PipesModule } from '@shared/pipes/pipes.module';
import { DownloadFileService } from '@shared/utils/download-file.service';
import { JsonApiService } from './services/json-api.service';
import { LoggerService } from './services/logger.service';
import { RouterLocalService } from './services/router.local.service';
import { StorageService } from './services/storage.service';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';

@NgModule({
  imports: [
    LoadingModule,
    HttpModule,
    PipesModule.forArchitecture(),
    RouterModule,
    MatDialogModule,
  ],
  providers: [
    NotificationService,
  ]
})
export class ArchitectureModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule: ArchitectureModule,
      providers: [
        JsonApiService,
        StorageService,
        RouterLocalService,
        DownloadFileService,
        LoggerService,
        CommandRestService,
      ]
    };
  }
}
