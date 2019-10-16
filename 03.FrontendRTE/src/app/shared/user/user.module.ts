/* (c) https://github.com/MontiCore/monticore */

import { CommonModule } from '@angular/common';
import { ModuleWithProviders, NgModule } from '@angular/core';
import { LoginInfoComponent } from '@shared/user/login-info';

@NgModule(
  {
    imports: [CommonModule],
    declarations: [LoginInfoComponent],
    exports: [LoginInfoComponent]
  }
)
export class UserModule {

  static forRoot(): ModuleWithProviders {
    return {
      ngModule: UserModule,
      providers: []
    };
  }
}
