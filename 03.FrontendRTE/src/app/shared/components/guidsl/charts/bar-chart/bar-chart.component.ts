import { AfterViewInit, Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts';
import * as Chart from 'chart.js'
import { ColorsService } from "@shared/utils/colors.service";
import { Options } from "ng5-slider";

export type BarChartXAxisData = { label: BarChartXAxisDataLabel, value: BarChartXAxisDataValue };
export type BarChartYAxisData = { label: BarChartYAxisDataLabel, value: BarChartYAxisDataValue };
export type BarChartXAxisDataValue = any;
export type BarChartYAxisDataValue = number;
export type BarChartXAxisDataLabel = string;
export type BarChartYAxisDataLabel = string;

export type YAxisType = 'STUNDE' | 'PROZENT';

export type SortFn = (n1: BarChartXAxisDataValue, n2: BarChartXAxisDataValue) => number;

export type BarChartDataTransformationFn = (data: any, range?: IBarChartDataRange) => BarChartData;

export type Color = string;

export interface IBarChartDataEntry {
  xAxisValue: BarChartXAxisData;
  yAxisValues: BarChartYAxisData[];
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
  backgroundColor?: string
}

@Component({
  selector: 'macoco-bar-chart',
  templateUrl: './bar-chart.component.html',
  styleUrls: ['./bar-chart.component.scss']
})
export class BarChartComponent implements OnInit, AfterViewInit {

  @ViewChild(BaseChartDirective)
  private chart: BaseChartDirective;

  private _sortFn: (n1: BarChartXAxisDataValue, n2: BarChartXAxisDataValue) => number = (n1: any, n2: any) => n1 - n2;

  public xAxisLabels: BarChartXAxisDataLabel[] = [];

  // bar chart data formatted in a way that is usable by the charts external charts library
  public dataset: BarDataGroup[] = [];

  // raw bar chart data
  private _data: BarChartData;

  public allXAxisDataInRange: BarChartXAxisData[];

  //region Slider
  public _sliderValueMin: number = 0;
  public _sliderValueMax: number = 0;
  public sliderOptions: Options = {
    floor: 0,
    ceil: 0,
    hideLimitLabels: true,
    translate: (index: number, _) => this.allXAxisDataInRange[index].label
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

  public onSliderChanged() {
    this.shownDataRange.min = this.allXAxisDataInRange[this.sliderValueMin].value;
    this.shownDataRange.max = this.allXAxisDataInRange[this.sliderValueMax].value;
    this.fetchData();
  }
  //endregion

  public _customLegend;

  public colorSet = [];

  public _dataRange: IBarChartDataRange;
  public shownDataRange: IBarChartDataRange;

  private getAllXAxisDataInRange(): BarChartXAxisData[] {
    return this._data
      .map((entry: IBarChartDataEntry) => entry.xAxisValue)
      .filter((data: BarChartXAxisData) => this.sortFn(data.value, this._dataRange.min) >= 0 && this.sortFn(data.value, this._dataRange.max) <= 0)
  }

  public getAllXAxisDataValues(): BarChartXAxisDataValue[] {
    return this._data
      .map((entry: IBarChartDataEntry) => {
        return entry.xAxisValue.value;
      });
  }

  private getAllYAxisDataLabels(): Set<BarChartYAxisDataLabel> {
    let labels: Set<BarChartYAxisDataLabel> = new Set();
    this._data.forEach((entry: IBarChartDataEntry) => {
      entry.yAxisValues.forEach((data: BarChartYAxisData) => {
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

  private _transformFn: BarChartDataTransformationFn;

  @Output('update') updateEvent: EventEmitter<IBarChartDataRange> = new EventEmitter();

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
  public set dataRange(range: IBarChartDataRange) {
    this._dataRange = range;
    this.shownDataRange = Object.assign({}, this._dataRange) as IBarChartDataRange;
    console.log("Set data range in dataRange setter");
  }

  @Input()
  public set colors(colors: Color[]) {
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
  public set data(data: any) {
    if (data === undefined || !data) {
      return;
    }

    this._data = this._transformFn(data, this.shownDataRange);

    // set data range manually iff no data range was provided via input
    if (!this._dataRange) {
      const allXAxisValuesSorted: BarChartXAxisDataValue[] = this.getAllXAxisDataValues()
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

    // Create bar chart data groups from specified data set
    this.getAllYAxisDataLabels().forEach((label: BarChartYAxisDataLabel) => {
      let values: BarChartYAxisDataValue[] = this._data
        .map((entry: IBarChartDataEntry) => {
          return entry.yAxisValues.find((yData: BarChartYAxisData) => yData.label === label).value;
        });

      const barDataGroup: BarDataGroup = {
        label: label,
        data: values,
      };

      this.dataset.push(barDataGroup)
    });

    // do this only once when the first chunk of data arrived
    if (!this.allXAxisDataInRange) {
      this.allXAxisDataInRange = this.getAllXAxisDataInRange();
      this.shownDataRange.min = this.allXAxisDataInRange[0].value;
      this.shownDataRange.max = this.allXAxisDataInRange[this.allXAxisDataInRange.length - 1].value;
      this.sliderOptions.ceil = this.allXAxisDataInRange.length - 1;
      this._sliderValueMax = this.allXAxisDataInRange.length - 1;
    }
  }
  //endregion
  //endregion

  public options: any = {
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
            drawBorder: false,
          },
        },
      ]
    },
    tooltips: {
      mode: 'x',
      position: 'custom'
    }
  };

  public reloadChart() {
    if (this.chart !== undefined) {
      this.chart.chart.destroy();
      this.chart.chart = 0;

      this.chart.datasets = this.dataset;
      this.chart.labels = this.xAxisLabels;
      this.chart.ngOnInit();
    }
  }

  public hasData(): boolean {
    return this.dataset != null && this.dataset.length > 0;
  }

  //region Axis
  public updateXAxis() {
    if (this._data) {
      this.xAxisLabels = this._data.map((group: IBarChartDataEntry) => group.xAxisValue.label);
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
    this.updateEvent.emit(this.shownDataRange);
  }

  private customTooltipPosition(_, position) {
    return {x: position.x, y: position.y};
  }

}
