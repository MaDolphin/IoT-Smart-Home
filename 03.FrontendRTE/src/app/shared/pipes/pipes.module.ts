/* (c) https://github.com/MontiCore/monticore */

import { CommonModule, DecimalPipe, PercentPipe } from '@angular/common';
import { ModuleWithProviders, NgModule } from '@angular/core';
import { AutoCompleteDatePipe } from './auto-complete-date.pipe';
import { AutoCompleteFromArrayPipe } from './auto-complete-from-array.pipe';
import { ClonePipe } from './clone.pipe';
import { CurrencyPipe } from './currency.pipe';
import { DateToStringPipe } from './date-to-string.pipe';
import { DurationInMonthsAndDays } from './duration-months&days.pipe';
import { EnumKeysPipe } from './enum-key.pipe';
import { GetRelativeValueToPipe } from './get-relativ-value-to.pipe';
import { JoinPipe } from './join.pipe';
import { PrettyJsonPipe } from './json-prettyjson.pipe';
import { ModelSerializePipe } from './model-serialize.pipe';
import { MomentPipe } from './moment.pipe';
import { MoneyToNumberPipe } from './money-to-number.pipe';
import { NumberToMoneyPipe } from './number-to-money.pipe';
import { PieChartPriorityPipe } from './piechart-priority.pipe';
import { PropertyFilterPipe } from './property-filter.pipe';
import { RemoveNullsPipe } from './remove-nulls.pipe';
import { RoundPipe } from './round.pipe';
import { SearchFilter } from './search-filter.pipe';
import { StringArrayFilter } from './string-array-filter.pipe';
import { StringToDatePipe } from './string-to-date.pipe';
import { SumPipe } from './sum.pipe';
import { UnixDateToStringPipe } from './unix-date-to-string.pipe';
import { ValueWithoutOverheadPipe } from './valueWithoutOverhead.pipe';
import { ToPercentPipe } from './topercent.pipe';
import { ValueWithoutRatePipe } from './valueWithoutRate.pipe';
import { ValueWithOverheadPipe } from './valueWithOverhead.pipe';
import { ValueWithRatePipe } from './valueWithRate.pipe';
import { NumberToStringPipe } from './number-to-string.pipe';
import { StatusToColorPipe } from '@shared/pipes/status-to-color.pipe';
import { SafeHTMLPipe } from '@shared/pipes/safe-html.pipe';
import { HourPipe } from '@shared/pipes/hour.pipe';
import { TitleTranslationPipe } from '@shared/pipes/title-translation.pipe';

const pipes: any[] = [
  PieChartPriorityPipe,
  PropertyFilterPipe,
  ModelSerializePipe,
  EnumKeysPipe,
  SearchFilter,
  CurrencyPipe,
  DurationInMonthsAndDays,
  StringArrayFilter,
  DateToStringPipe,
  StringToDatePipe,
  GetRelativeValueToPipe,
  JoinPipe,
  NumberToMoneyPipe,
  NumberToStringPipe,
  DateToStringPipe,
  ClonePipe,
  MoneyToNumberPipe,
  AutoCompleteFromArrayPipe,
  AutoCompleteDatePipe,
  MomentPipe,
  SumPipe,
  RemoveNullsPipe,
  RoundPipe,
  ValueWithRatePipe,
  ValueWithoutRatePipe,
  ValueWithOverheadPipe,
  ValueWithoutOverheadPipe,
  ToPercentPipe,
  UnixDateToStringPipe,
  PrettyJsonPipe,
  StatusToColorPipe,
  SafeHTMLPipe,
  HourPipe,
  TitleTranslationPipe,
];

@NgModule({
  declarations: pipes,
  exports: pipes,
  imports: [
    CommonModule,
  ],
  providers: [
    DecimalPipe,
    RoundPipe,
    PercentPipe,
    NumberToStringPipe,
    PieChartPriorityPipe,
    ToPercentPipe,
    SumPipe,
    StringToDatePipe,
    HourPipe
  ]
})
export class PipesModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule: PipesModule,
      providers: pipes
    };
  }

  static forArchitecture(): ModuleWithProviders {
    return {
      ngModule: PipesModule,
      providers: [
        DateToStringPipe,
        StringToDatePipe,
        MoneyToNumberPipe,
        NumberToMoneyPipe,
        ValueWithRatePipe,
        ValueWithoutRatePipe,
        ValueWithOverheadPipe,
        ValueWithoutOverheadPipe,
        RemoveNullsPipe,
        ClonePipe,
        DurationInMonthsAndDays,
      ]
    }
  }
}
