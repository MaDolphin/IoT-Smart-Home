/* (c) https://github.com/MontiCore/monticore */

import { CommonModule } from '@angular/common';
import { ModuleWithProviders, NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule } from '@angular/forms';
import { MatButtonModule, MatIconModule, MatListModule, MatMenuModule, MatSidenavModule, MatToolbarModule, } from '@angular/material';
import { RouterModule } from '@angular/router';
import { FormDirectivesModule } from '@upe/ngx-bootstrap-directives';
import { BsDropdownModule, TooltipModule } from 'ngx-bootstrap';
import { PerfectScrollbarModule } from 'ngx-perfect-scrollbar';
import { PipesModule } from '@shared/pipes';
import { HeaderComponent } from './header/header.component';
import { LoadingModule } from './loading/loading.module';
import { MainLayoutComponent } from './main/main-layout.component';
import { MainLayoutService } from './main/main-layout.service';
import { BigBreadcrumbsComponent } from './navigation/big-breadcrumbs.component';
import { NavigationComponent } from './navigation/navigation.component';
import { BreadcrumbComponent } from './breadcrumb/breadcrumb.component';


@NgModule(
  {
    imports: [
      CommonModule,
      PipesModule,
      FormsModule,
      RouterModule,
      LoadingModule,
      TooltipModule,
      BsDropdownModule,
      FlexLayoutModule,
      MatIconModule,
      MatToolbarModule,
      MatButtonModule,
      MatSidenavModule,
      MatMenuModule,
      MatListModule,
      PerfectScrollbarModule,
      FormDirectivesModule,
    ],
    declarations: [
      MainLayoutComponent,
      NavigationComponent,
      BigBreadcrumbsComponent,
      HeaderComponent,
      BreadcrumbComponent,
    ],
    exports: [
      LoadingModule,
      BigBreadcrumbsComponent,
    ],
    providers: [],
  },
)
export class LayoutModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule:  LayoutModule,
      providers: [
        MainLayoutService,
      ],
    };
  }
}
