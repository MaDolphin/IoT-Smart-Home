/* (c) https://github.com/MontiCore/monticore */

import { ModuleWithProviders, NgModule } from '@angular/core';
import { MCContainerComponent } from '@shared/components/guidsl/mc-node/mc-container/mc-container.component';
import { PieChartComponent } from '@shared/components/guidsl/charts/pie-chart/pie-chart.component';
import { LabelComponent } from '@shared/components/guidsl/label/label.component';
import { TimeLineChartComponent } from '@shared/components/guidsl/charts/time-line-chart/time-line-chart.component';
import { ButtonComponent } from '@shared/components/guidsl/button/button.component';
import { BarChartComponent } from '@shared/components/guidsl/charts/bar-chart/bar-chart.component';
import { NavigationComponent } from '@shared/components/guidsl/navigation/navigation.component';
import { PercentageChartComponent } from '@shared/components/guidsl/charts/percentage-chart/percentage-chart.component';
import { MatAutocompleteModule, MatCardModule, MatDatepickerModule, MatFormFieldModule, MatSelectModule, MatTabsModule } from '@angular/material';
import { PipesModule } from '@shared/pipes';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FormDirectivesModule, InputGroupDirectivesModule } from '@upe/ngx-bootstrap-directives';
import { ChartsModule } from 'ng2-charts';
import { DataTableModule } from '@shared/components/guidsl/data-table/data-table.module';
import { DataTableComponent } from '@shared/components/guidsl/data-table/data-table.component';
import { InputModule } from '@shared/components/guidsl/input/input.module';
import { RouterModule } from '@angular/router';
import { FlexLayoutModule } from '@angular/flex-layout';
import { BalanceBoxComponent } from '@shared/components/guidsl/balance-box/balance-box.component';
import { HorizontalBarChartComponent } from '@shared/components/guidsl/charts/horizonatl-bar-chart/horizontal-bar-chart.component';
import { BudgetWidgetComponent } from '@shared/components/guidsl/budget-widget/budget-widget.component';
import { TextMaskModule } from 'angular2-text-mask';
import { LineChartComponent } from './charts/line-chart/line-chart.component';
import { Ng5SliderModule } from 'ng5-slider';


@NgModule({
  declarations: [
    MCContainerComponent,
    PieChartComponent,
    LabelComponent,
    TimeLineChartComponent,
    ButtonComponent,
    NavigationComponent,
    BarChartComponent,
    PercentageChartComponent,
    BalanceBoxComponent,
    HorizontalBarChartComponent,
    LineChartComponent,
    BudgetWidgetComponent,
  ],
  imports: [
    DataTableModule,
    PipesModule.forArchitecture(),
    ChartsModule,
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    FormDirectivesModule,
    InputGroupDirectivesModule,
    InputModule,
    FormsModule,
    FlexLayoutModule,
    RouterModule,
    MatAutocompleteModule,
    MatCardModule,
    MatDatepickerModule,
    MatSelectModule,
    MatTabsModule,
    TextMaskModule,
    Ng5SliderModule
  ],
  exports: [
    MCContainerComponent,
    DataTableComponent,
    PieChartComponent,
    LabelComponent,
    TimeLineChartComponent,
    ButtonComponent,
    NavigationComponent,
    BarChartComponent,
    PercentageChartComponent,
    BalanceBoxComponent,
    HorizontalBarChartComponent,
    LineChartComponent,
    BudgetWidgetComponent
  ],
  providers: [
  ],
  entryComponents: [
    TimeLineChartComponent,
    LabelComponent,
    ButtonComponent,
    NavigationComponent,
    BarChartComponent,
    PercentageChartComponent
  ]
})
export class GuiDslComponentsModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule:  GuiDslComponentsModule,
    };
  }
}
