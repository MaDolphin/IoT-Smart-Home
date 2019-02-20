import { ModuleWithProviders, NgModule } from '@angular/core';
import { HttpModule } from '@angular/http';
import { MatDialogModule } from '@angular/material';
import { RouterModule } from '@angular/router';
import { LoadingModule } from '@shared/layout/loading/loading.module';
import { NotificationService } from '@shared/notification/notification.service';
import { PipesModule } from '@shared/pipes/pipes.module';
import { DownloadFileService } from '@shared/utils/download-file.service';
import { RoleManagementFormularFactory } from './forms/factories/role-management.formular.factory';
import { UserRegistrationFormularFactory } from './forms/factories/user-registration.formular.factory';
import { EntgeltService } from './services/entgelt.service';
import { JsonApiService } from './services/json-api.service';
import { LoggerService } from './services/logger.service';
import { Projectkinds } from './services/models/projectkinds';
import { RoleService } from './services/models/role.model.service';
import { RoleAssignmentModelService } from './services/models/roleassignment.model.service';
import { UserModelService } from './services/models/user.model.service';
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
        RoleAssignmentViewModelFactory,
        UserViewModelFactory,
        UserRegistrationFormularFactory,
        RoleManagementFormularFactory,
        RoleAssignmentModelService,
        UserModelService,
        JsonApiService,
        StorageService,
        RouterLocalService,
        RoleService,
        Projectkinds,
        DownloadFileService,
        LoggerService,
        CommandRestService,
        EntgeltService,
      ]
    };
  }
}
