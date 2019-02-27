/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { CommonModule } from '@angular/common';
import { ModuleWithProviders, NgModule } from '@angular/core';
import { MatProgressBarModule } from '@angular/material';
import { LoadingBarComponent } from './loading-bar/loading-bar.component';
import { LoadingSpinnerComponent } from './loading-spinner/loading-spinner.component';
import { LoadingService } from './loading.service';

@NgModule({
  imports: [
    CommonModule,
    MatProgressBarModule,
  ],
  declarations: [
    LoadingBarComponent,
    LoadingSpinnerComponent,
  ],
  exports: [
    LoadingBarComponent,
    LoadingSpinnerComponent,
  ]
})
export class LoadingModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule:  LoadingModule,
      providers: [
        LoadingService,
      ],
    };
  }
}
