import { registerLocaleData } from '@angular/common';
import localeDe from '@angular/common/locales/de';
import localeDeExtra from '@angular/common/locales/extra/de';
import { LOCALE_ID, NgModule } from '@angular/core';
import { MAT_DATE_FORMATS, MAT_DATE_LOCALE, MatGridListModule } from '@angular/material';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ButtonDirectivesModule, PanelDirectivesModule } from '@upe/ngx-bootstrap-directives';
import { ChartsModule } from 'ng2-charts';
import { ContextMenuModule } from 'ngx-contextmenu';
import { PERFECT_SCROLLBAR_CONFIG, PerfectScrollbarConfigInterface, PerfectScrollbarModule, } from 'ngx-perfect-scrollbar';
import { AppComponent } from './app.component';
import { routing } from './app.routing';
import { ArchitectureModule } from '@shared/architecture/architecture.module';
import { AuthModule } from '@shared/auth/auth.module';
import { LayoutModule } from '@shared/layout';
import { LoadingModule } from '@shared/layout/loading/loading.module';
import { MontiGemModule } from '@shared/montigem.module';
import { PipesModule } from '@shared/pipes';
import { UtilsModule } from '@shared/utils/utils.module';
import { GuiDslComponentsModule } from '@shared/components/guidsl/guidsl-components.module';
import { InputModule } from '@shared/components/guidsl/input/input.module';
import { DashboardComponent } from "@targetgui/dashboard.component/dashboard.component";


registerLocaleData(localeDe, 'de-DE', localeDeExtra);
registerLocaleData(localeDe, 'de', localeDeExtra);

const DEFAULT_PERFECT_SCROLLBAR_CONFIG: PerfectScrollbarConfigInterface = {
  suppressScrollX: true,
};

export const MY_DATE_FORMATS = {
  parse:   {
    dateInput: 'LL',
  },
  display: {
    dateInput:          'LL',
    monthYearLabel:     'MMM YYYY',
    dateA11yLabel:      'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
  ],
  imports:      [
    BrowserAnimationsModule,
    BrowserModule,
    routing,
    MontiGemModule.forRoot(),
    PipesModule.forRoot(),
    AuthModule.forRoot(),
    PerfectScrollbarModule,
    ArchitectureModule.forRoot(),
    GuiDslComponentsModule.forRoot(),
    InputModule.forRoot(),
    UtilsModule.forRoot(),
    LoadingModule.forRoot(),
    LayoutModule.forRoot(),
    ContextMenuModule.forRoot(),

    MatGridListModule,
    ChartsModule,

    PanelDirectivesModule,
    ButtonDirectivesModule,


    // MatMomentDateModule,
  ],
  // entryComponents: [AppComponent],
  bootstrap:    [AppComponent],
  providers:    [
    { provide: MAT_DATE_LOCALE, useValue: 'de-DE' },
    { provide: MAT_DATE_FORMATS, useValue: MY_DATE_FORMATS },
    {
      provide:  PERFECT_SCROLLBAR_CONFIG,
      useValue: DEFAULT_PERFECT_SCROLLBAR_CONFIG,
    },
    { provide: LOCALE_ID, useValue: 'de-DE' },
  ],
})
export class AppModule {

}
