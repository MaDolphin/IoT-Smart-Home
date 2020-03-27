/* (c) https://github.com/MontiCore/monticore */
import { AfterViewInit, Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts';
import * as Chart from 'chart.js'
import { ColorsService } from "@shared/utils/colors.service";
import { Options } from "ng5-slider";

export type BarChartYAxisData = { label: BarChartYAxisDataLabel, value: BarChartYAxisDataValue, stack?: string };
export type BarChartXAxisDataValue = any;
export type BarChartYAxisDataValue = number;
export type BarChartXAxisDataLabel = string;
export type BarChartYAxisDataLabel = string;

export type YAxisType = 'STUNDE' | 'PROZENT';

export type SortFn = (n1: BarChartXAxisDataValue, n2: BarChartXAxisDataValue) => number;

export type BarChartDataTransformationFn = (data: any, range?: IBarChartDataRange) => BarChartData;
export type BarChartRangeTransformationFn = (range: IBarChartDataRange) => BarChartXAxisDataValue[];
export type BarChartXAxisDataValueStringifyFn = (value: BarChartXAxisDataValue) => string
export type BarChartDataRangeTransformationFn = (range: any) => IBarChartDataRange;


export type Color = string;

export interface IBarChartDataEntry {
  xAxis: BarChartXAxisDataValue;
  yAxis: BarChartYAxisData[];
}

export type BarChartData = IBarChartDataEntry[];

export interface IBarChartDataRange {
  min: BarChartXAxisDataValue;
  max: BarChartXAxisDataValue;
}

// interface solely used for the external charts library
interface BarDataGroup {
  label: string,
  data: number[]
  stack?: string
  fillColor?: string
  backgroundColor?: any
  borderColor?: any
  hoverBackgroundColor?: any
  hoverBorderColor?: any
}

@Component({
  selector: 'macoco-bar-chart',
  templateUrl: './bar-chart.component.html',
  styleUrls: ['./bar-chart.component.scss']
})
export class BarChartComponent implements OnInit, AfterViewInit {

  @Output('update')
  public onUpdate = new EventEmitter<IBarChartDataRange>();

  @ViewChild(BaseChartDirective)
  private chart: BaseChartDirective;

  // transform methods
  private _sortFn: (n1: BarChartXAxisDataValue, n2: BarChartXAxisDataValue) => number = (n1: any, n2: any) => n1 - n2;
  private _rangeTransformFn: BarChartDataRangeTransformationFn;
  private _transformFn: BarChartDataTransformationFn;
  public _getAllXAxisDataValuesFn: BarChartRangeTransformationFn;
  public _barChartXAxisDataValueStringifyFn: BarChartXAxisDataValueStringifyFn = (value: any) => '' + value;

  public _customLegend;

  public colorSet = [];

  // range variables
  public _dataRange: IBarChartDataRange;
  public _shownDataRange: IBarChartDataRange;

  public xAxisLabels: BarChartXAxisDataLabel[] = [];

  // bar chart data formatted in a way that is usable by the charts external charts library
  public dataset: BarDataGroup[] = [];

  // raw bar chart data
  private _data: BarChartData;

  public allXAxisDataValuesInRange: BarChartXAxisDataValue[];

  //region Slider
  public _sliderValueMin: number = 0;
  public _sliderValueMax: number = 0;
  public sliderOptions: Options = {
    floor: 0,
    ceil: 0,
    hideLimitLabels: true,
    translate: (index: number, _) => this._barChartXAxisDataValueStringifyFn(this.allXAxisDataValuesInRange[index])
  };

  public get sliderValueMin() {
    return this._sliderValueMin;
  }

  public set sliderValueMin(index: number) {
    this._sliderValueMin = index;
  }

  public get sliderValueMax() {
    return this._sliderValueMax;
  }

  public set sliderValueMax(index: number) {
    this._sliderValueMax = index;
  }

  public onSliderChanged(event) {
    this._shownDataRange.min = this.allXAxisDataValuesInRange[event.value];
    this._shownDataRange.max = this.allXAxisDataValuesInRange[event.highValue];
    this.fetchData();
  }
  //endregion

  private getAllXAxisDataValuesInRange(): BarChartXAxisDataValue[] {
    return this._getAllXAxisDataValuesFn(this._dataRange);
  }

  public getAllXAxisDataValuesInData(): BarChartXAxisDataValue[] {
    return this._data
      .map((entry: IBarChartDataEntry) => {
        return entry.xAxis;
      });
  }

  private getAllYAxisDataLabels(): Set<BarChartYAxisDataLabel> {
    let labels: Set<BarChartYAxisDataLabel> = new Set();
    this._data.forEach((entry: IBarChartDataEntry) => {
      entry.yAxis.forEach((data: BarChartYAxisData) => {
        labels.add(data.label)
      });
    });
    return labels;
  }

  private maxLimits = {
    type: 'max',
    max: {
      percentage: 100,
      hour: 10
    },
    suggestedMax: {
      percentage: 100,
      hour: 10
    }
  };

  private tickSize = {
    percentage: 25,
    hour: 5,
  };

  private addEmptyBars(stackData) {
    for (const stack of this.getAllStacks(this._data)) {
      stackData.get(stack).push(NaN);
    }
  }

  private getAllStacks(data: BarChartData): string[] {
    const stacks = new Set();
    data.forEach((dataEntry: IBarChartDataEntry) => {
      const yAxis = dataEntry.yAxis;
      for (const bar of yAxis) {
        if (bar.stack) {
          stacks.add(bar.stack);
        }
      }
    });
    return Array.from(stacks.values());
  }

  private setDataSetWithoutStacks() {
    this.getAllYAxisDataLabels().forEach((label: BarChartYAxisDataLabel) => {
      let values: BarChartYAxisDataValue[] = this._data
        .map((entry: IBarChartDataEntry) => {
          return entry.yAxis.length > 0 ? entry.yAxis.find((yData: BarChartYAxisData) => yData.label === label).value : 0;
        });

      const barDataGroup: BarDataGroup = {
        label: label,
        data: values,
      };

      this.dataset.push(barDataGroup)
    });
  }

  private setDataSetWithStacks(allStackLabels: string[]) {
    this.colors = [];
    this.options.scales.xAxes[0].barPercentage = 0.8;

    let index = 0;

    // Create bar chart data groups from specified data set
    this.getAllYAxisDataLabels().forEach((label: BarChartYAxisDataLabel) => {
      const stackData = new Map<string, BarChartYAxisDataValue[]>();

      // insert empty arrays for each stack into the map
      for (const stack of allStackLabels) {
        stackData.set(stack, []);
      }

      this._data.forEach((entry: IBarChartDataEntry) => {
        if (entry.yAxis.length > 0) {
          let yDatas = entry.yAxis.filter((yData: BarChartYAxisData) => yData.label === label);

          for (const stack of allStackLabels) {
            // tslint:disable-next-line:no-shadowed-variable
            let yData = yDatas.find((yData: BarChartYAxisData) => yData.stack === stack);
            if (yData) {
              stackData.get(stack).push(yData.value);
            } else {
              stackData.get(stack).push(NaN);
            }
          }
        } else {
          this.addEmptyBars(stackData);
        }
      });

      for (const stack of allStackLabels) {
        const barDataGroup: BarDataGroup = {
          label: label,
          data: stackData.get(stack),
          stack: stack,
          backgroundColor : this._colorsService.getColors()[index],
        };

        this.dataset.push(barDataGroup)
      }
      index++;
    });

  }

  private updateEvent: EventEmitter<IBarChartDataRange> = new EventEmitter();

  //region Inputs
  //region Optional
  @Input()
  public set dataTransformation(transformFn: BarChartDataTransformationFn) {
    this._transformFn = transformFn;
  }

  // TODO MF: get rid of this!
  @Input()
  public set dateRange(_: any) {}

  @Input()
  public set shownDataRange(range: IBarChartDataRange) {
    this._shownDataRange = range;
  }

  @Input()
  public set dataRange(range: IBarChartDataRange) {
    if (range) {
      if (!!this._rangeTransformFn) {
        this._dataRange = this._rangeTransformFn(range);
      } else {
        this._dataRange = range;
      }
      if (!this._shownDataRange) {
        this._shownDataRange = Object.assign({}, this._dataRange) as IBarChartDataRange;
      }
    }
  }

  @Input()
  public set colors(colors: Color[]) {
    if (colors.length === 0) {
      this.colorSet = [{}];
    }

    let i = 0;
    for ( ; i < colors.length; i++) {
      if (i < this.colorSet.length)
        this.colorSet[i].backgroundColor = colors[i];
      else
        break;
    }

    for ( ; i < colors.length; i++) {
      this.colorSet.push({
        backgroundColor: colors[i]
      });
    }
  }

  @Input()
  public set tickSizePercentage(size: number) {
    this.tickSize.percentage = size;
  }

  @Input()
  public set useSuggestedMax(suggestedMax: boolean) {
    if (suggestedMax) {
      this.maxLimits.type = 'suggestedMax';
    } else {
      this.maxLimits.type = 'max';
    }
  }

  @Input()
  public set tickSizeHour(size: number) {
    this.tickSize.hour = size;
  }

  @Input()
  public set suggestedMaxPercentage(max: number) {
    this.maxLimits.type = 'suggestedMax';
    this.maxLimits.suggestedMax.percentage = max;
  }

  @Input()
  public set maxPercentage(max: number) {
    this.maxLimits.type = 'max';
    this.maxLimits.max.percentage = max;
  }

  @Input()
  public set suggestedMaxHour(max: number) {
    this.maxLimits.type = 'suggestedMax';
    this.maxLimits.suggestedMax.hour = max;
  }

  @Input()
  public set maxHour(max: number) {
    this.maxLimits.type = 'max';
    this.maxLimits.max.hour = max;
  }

  @Input()
  public set stacked(stacked: boolean) {
    this.options.scales.xAxes[0].stacked = stacked;
    this.options.scales.yAxes[0].stacked = stacked;
  }

  @Input()
  public set title(title: string) {
    this.options.title.display = true;
    this.options.title.text = title;
    this.options.title.position = 'top';
    this.options.title.fontSize = 18;
    this.options.title.lineHeight = 1.6;
  }

  @Input()
  public set customLegend(legend) {
    this._customLegend = legend;
    this.options.legend.display = false;
  }

  @Input()
  public set sortFn(fn: SortFn) {
    this._sortFn = fn;
  }

  public get sortFn(): SortFn {
    return this._sortFn;
  }

  private _yAxisType: YAxisType = 'PROZENT';

  @Input()
  public set yAxisType(type: YAxisType) {
    this._yAxisType = type
  }

  public get yAxisType(): YAxisType {
    return this._yAxisType;
  }
  //endregion

  //region Mandatory
  @Input()
  public set rangeTransformation(transformFn: BarChartDataRangeTransformationFn) {
    this._rangeTransformFn = transformFn;
  }

  @Input()
  public set getAllXAxisDataValuesFn(fn: BarChartRangeTransformationFn) {
    this._getAllXAxisDataValuesFn = fn;
  }

  @Input()
  public set barChartXAxisDataValueStringifyFn(fn: BarChartXAxisDataValueStringifyFn) {
    this._barChartXAxisDataValueStringifyFn = fn;
  }

  public get barChartXAxisDataValueStringifyFn(): BarChartXAxisDataValueStringifyFn {
    return this._barChartXAxisDataValueStringifyFn;
  }

  @Input()
  public set data(data: any) {
    if (data === undefined || !data) {
      return;
    }

    if (this._dataRange) {
      if (this._sortFn(this._shownDataRange.min, this._dataRange.min) < 0
        || this._sortFn(this._shownDataRange.min, this._dataRange.max) > 0
        || this._sortFn(this._shownDataRange.max, this._dataRange.min) < 0
        || this._sortFn(this._shownDataRange.max, this._dataRange.max) > 0) {
        this._shownDataRange = Object.assign({}, this._dataRange) as IBarChartDataRange;
        this.updateXAxis();
        this.fetchData();
      }
    }

    this._data = this._transformFn(data, this._shownDataRange);

    // set data range manually iff no data range was provided via input
    if (!this._dataRange) {
      const allXAxisValuesSorted: BarChartXAxisDataValue[] = this.getAllXAxisDataValuesInData()
        .sort((n1: BarChartXAxisDataValue, n2: BarChartXAxisDataValue) => this.sortFn(n1, n2));
      this.dataRange = {
        min: allXAxisValuesSorted[0],
        max: allXAxisValuesSorted[allXAxisValuesSorted.length - 1]
      };
    }

    // Update axes
    this.updateXAxis();
    this.updateYAxis(this._yAxisType);

    // Reset existing data set
    this.dataset = [];

    const allStackLabels: string[] = this.getAllStacks(this._data);
    if (allStackLabels.length > 0) {
      this.setDataSetWithStacks(allStackLabels);
    } else {
      this.setDataSetWithoutStacks();
    }

    // do this only once when the first chunk of data arrived
    if (!this.allXAxisDataValuesInRange) {
      this.allXAxisDataValuesInRange = this.getAllXAxisDataValuesInRange();
      this.sliderOptions.ceil = this.allXAxisDataValuesInRange.length - 1;
      this._sliderValueMax = this.allXAxisDataValuesInRange.findIndex((value: BarChartXAxisDataValue) => this._sortFn(value, this._shownDataRange.max) === 0);
      this._sliderValueMin = this.allXAxisDataValuesInRange.findIndex((value: BarChartXAxisDataValue) => this._sortFn(value, this._shownDataRange.min) === 0);
    }

    setTimeout( () => {
      this.updateChart();
    }, 100);
  }
  //endregion
  //endregion

  public options: any = {
    // animation: false,
    padding: "10",
    responsive: true,
    maintainAspectRatio: false,
    animation: {
      duration: 1000,
    },
    title: {},
    legend: {
      display: true,
      position: 'bottom'
    },
    elements: { point: { radius: 0 } },
    scales: {
      xAxes: [
        {
          position: 'bottom',
          display: true,
          categoryPercentage: 0.4,
          barPercentage: 1.0,
          gridLines: {
            offsetGridLines: false,
            display: false,
          }
        },
      ],
      yAxes: [
        {
          categoryPercentage: 1.0,
          barPercentage: 1.0,
          ticks: {
            max: 100,
            stepSize: 1,
            beginAtZero: true,
          },
          gridLines: {
            drawBorder: true,
          },
        },
      ]
    },
    tooltips: {
      mode: 'x',
      position: 'custom',
      callbacks: {
        label: (tooltipItem, data) => {
          if (isNaN(tooltipItem.yLabel)) {
            return ''
          }
          let label = data.datasets[tooltipItem.datasetIndex].label || '';
          if (label) {
            label += ': ';
          }
          label += tooltipItem.yLabel;
          return label;
        }
      }
    }
  };

  public hasData(): boolean {
    return this.dataset != null && this.dataset.length > 0;
  }

  //region Axis
  public updateXAxis() {
    if (this._data) {
      this.xAxisLabels = this._data.map((group: IBarChartDataEntry) => this.barChartXAxisDataValueStringifyFn(group.xAxis));
    }
  }

  public updateYAxis(type: YAxisType) {
    let tick = this.options.scales.yAxes[0].ticks;

    let append;
    let remove;

    let axisType;

    if (this.maxLimits.type === 'suggestedMax') {
      append = 'suggestedMax';
      remove = 'max';
    } else {
      append = 'max';
      remove = 'suggestedMax';
    }

    if (type === 'STUNDE') {
      axisType = 'hour';
    } else {
      axisType = 'percentage';
    }

    tick[append] = this.maxLimits[append][axisType];
    tick.stepSize = this.tickSize[axisType];
    delete tick[remove];
  }
  //endregion

  constructor(protected _colorsService: ColorsService) {
  }

  public ngOnInit(): void {
    this.updateXAxis();

    this.colors = this._colorsService.getColors();

    Chart.Tooltip.positioners.custom = this.customTooltipPosition;

    this.updateEvent.subscribe((range: IBarChartDataRange) => this.onUpdate.emit(range));
  }

  public ngAfterViewInit() {
    this.fetchData();
  }

  public updateChart() {
    if (this.chart) {
      this.chart.ngOnInit();
    }
  }

  public fetchData() {
    this.updateEvent.emit(this._shownDataRange);
  }

  private customTooltipPosition(_, position) {
    return {x: position.x, y: position.y};
  }

}
