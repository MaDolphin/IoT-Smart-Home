/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { ModuleWithProviders, NgModule } from '@angular/core';

@NgModule({
  declarations: [
    // Pipes.
    // Directives.
    // Components.
  ],
  exports:      [
    // Pipes.
    // Directives.
    // Components.
  ],
})
// Consider registering providers using a forRoot() method
// when the module exports components, directives or pipes that require sharing the same providers instances.
// Consider registering providers also using a forChild() method
// when they requires new providers instances or different providers in child modules.
export class NgxFormsModule {

  /**
   * Use in AppModule: new instance of SumService.
   */
  public static forRoot(): ModuleWithProviders {
    return {
      ngModule:  NgxFormsModule,
      providers: [],
    };
  }

  /**
   * Use in features modules with lazy loading: new instance of SumService.
   */
  public static forChild(): ModuleWithProviders {
    return {
      ngModule:  NgxFormsModule,
      providers: [],
    };
  }

}
