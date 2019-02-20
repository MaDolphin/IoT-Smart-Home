import { ModuleWithProviders, NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import 'chart.js';
import { DirectivesModule } from './directives/directives.module';
import { HeaderService } from './layout/header/header.service';
import { SettingsService } from '@shared/layout';
import { NotificationModule } from './notification/notification.module';
import { PipesModule } from './pipes';
import { UtilsModule } from './utils/utils.module';
import { InputModule } from '@shared/components/guidsl/input/input.module';
import { GuiDslComponentsModule } from '@shared/components/guidsl/guidsl-components.module';
import { HttpClientModule } from '@angular/common/http';
import { ComponentInjectorService } from '@shared/architecture/services/component-creator.service';

@NgModule(
  {
    imports: [
      PipesModule.forArchitecture()
    ],
    declarations: [],
    exports:      [
      NotificationModule,
      UtilsModule,
      InputModule,
      GuiDslComponentsModule,
      HttpClientModule,
      DirectivesModule,
      PipesModule,
      FlexLayoutModule
    ],
    providers: [
      ComponentInjectorService
    ],
  }
)
export class GeneralModule {

  static forRoot(): ModuleWithProviders {
    return {
      ngModule:  GeneralModule,
      providers: [
        SettingsService,
        HeaderService,
      ]
    };
  }

}
