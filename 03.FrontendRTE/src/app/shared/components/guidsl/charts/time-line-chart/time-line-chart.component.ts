import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts';
import { ColorsService } from "@shared/utils/colors.service";
import { Options } from "ng5-slider";
import * as moment from 'moment';

export type LineChartDataTransformationFn = (data: any, range?: ILineChartDataRange) => LineChartData;
export type LineChartDataRangeTransformationFn = (range: any) => ILineChartDataRange;

export interface ILineChartDataEntryLine {
  start: Date,
  end: Date
}

export interface ILineChartDataEntry {
  label: string,
  lines: ILineChartDataEntryLine[];
}

export type LineChartData = ILineChartDataEntry[];

interface LineChartCoordinates {
  x: string, // e.g. "2018-01-01T00:00:00.000Z"
  y: string // e.g. "Vertragslaufzeit"
}

interface ING2ChartsEntry {
  fill: boolean,
  data: (LineChartCoordinates | null)[] // between every line must be a null to enable breaks between them
  label: string
}

export interface ILineChartDataRange {
  min: Date;
  max: Date;
}

type ING2Charts = ING2ChartsEntry[];

@Component({
  selector: 'macoco-timeline-chart',
  templateUrl: './time-line-chart.component.html',
  styleUrls: ['./time-line-chart.component.scss'],
})
export class TimeLineChartComponent implements OnInit {

  public dataSet: ING2Charts;
  private _transformFn: LineChartDataTransformationFn;
  private _rangeTransformFn: LineChartDataRangeTransformationFn;
  public labels: string[];

  //region Range
  public _dataRange: ILineChartDataRange;
  public _shownDataRange: ILineChartDataRange;

  private set dataRange(range: ILineChartDataRange) {
    this._dataRange = range;

    this.sliderOptions.ceil = TimeLineChartComponent.monthDiff(range.min, range.max);

    if (!this.shownDataRange) {
      if (this._tempShownDataRange) {
        this.shownDataRange = Object.assign({}, this._tempShownDataRange) as ILineChartDataRange;
        this.sliderValueMin = TimeLineChartComponent.monthDiff(this.dataRange.min, moment(this._tempShownDataRange.min).startOf('month').toDate());
        this.sliderValueMax = TimeLineChartComponent.monthDiff(this.dataRange.min, moment(this._tempShownDataRange.max).endOf('month').toDate());
        this.updateXAxis();
      } else {
        this.shownDataRange = Object.assign({}, this.dataRange) as ILineChartDataRange;
        this.sliderValueMin = 0; // completely to the left
        this.sliderValueMax = this.sliderOptions.ceil; // // completely to the right
      }
    }
  }

  private get dataRange(): ILineChartDataRange {
    return this._dataRange;
  }

  public set shownDataRange(range: ILineChartDataRange) {
    this._shownDataRange = range;
    this.updateXAxis();
  }

  public get shownDataRange() {
    return this._shownDataRange;
  }

  public static correctRange(range: ILineChartDataRange): ILineChartDataRange {
    return {
      min: moment(range.min).startOf('month').toDate(),
      max: moment(range.max).endOf('month').toDate()
    }
  }

  public dateToString(date: Date): string {
    return moment(date).locale('de').format("MMM YYYY");
  }

  private _yearStart: string = String(new Date().getFullYear());
  private _yearEnd: string = String(new Date().getFullYear());

  public get yearStart() {
    return this._yearStart;
  }

  public get yearEnd() {
    return this._yearEnd;
  }

  private static getRange(data: LineChartData): ILineChartDataRange | null {
    let allDates: Date[] = [];
    for (let entry of data) {
      const dateArr = entry.lines.map((line: ILineChartDataEntryLine) => {
        return [line.start, line.end];
      });
      allDates = allDates.concat([].concat(...dateArr));
    }

    // just to be sure
    if (allDates.length > 0) {
      return {
        min: moment(allDates.reduce((a: Date, b: Date) => a < b ? a : b)).startOf('month').toDate(),
        max: moment(allDates.reduce((a: Date, b: Date) => a > b ? a : b)).endOf('month').toDate()
      }
    } else {
      return null;
    }
  }

  //region Slider
  private static monthDiff(d1: Date, d2: Date): number {
    let months;
    months = (d2.getFullYear() - d1.getFullYear()) * 12;
    months -= d1.getMonth() + 1;
    months += d2.getMonth();
    return months <= 0 ? 0 : months + 1;
  }

  public _sliderValueMin: number = 0;
  public _sliderValueMax: number = 0;
  public sliderOptions: Options = {
    floor: 0,
    ceil: 10,
    hideLimitLabels: true,
    translate: (index: number, _) => moment(this.dataRange.min).add(index, 'month').locale('de').format('MMM YYYY') // e.g. 'Okt. 2019'
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
    let range: ILineChartDataRange = {
      min: moment(this.dataRange.min).add(this.sliderValueMin, 'months').startOf('month').toDate(), // relative to min
      max: moment(this.dataRange.min).add(this.sliderValueMax, 'months').endOf('month').toDate()  // relative to min
    };
    this.shownDataRange = range;
    this.queryUpdate();
  }
  //endregion
  //endregion

  @ViewChild(BaseChartDirective)
  private chart: BaseChartDirective;

  //region Color
  public colorSet = [
    { borderColor: '#00549f' },
    { borderColor: '#555555' },
    { borderColor: '#57ab27' },
    { borderColor: '#f6a800' },
    { borderColor: '#006165' },
    { borderColor: '#A11035' },
    { borderColor: '#0098A1' },
    { borderColor: '#612158' },
  ];

  public getColor(index: number) {
    return this.colorSet[index % this.colorSet.length].borderColor;
  }
  //endregion

  public options: any = {
    responsive: true,
    maintainAspectRatio: false,
    animation: {
      duration: 0,
    },
    legend: {
      display: false,
    },
    elements: { point: { radius: 0 } },
    scales: {
      xAxes: [
        // Vertical lines to distinguish months
        {
          position: 'bottom',
          display: true,
          stacked: false,
          type: 'time',
          bounds: 'data',
          time: {
            min: '2018-01-01',
            max: '2018-12-31',
            unit: 'month',
            unitStepSize: 1,
            displayFormats: {
              month: 'MMM',
            },
          },
          ticks: {
            display: false,
          },
          gridLines: {
            offsetGridLines: false,
            display: true,
            drawTicks: false,
          },
        },
        // Dates above chart
        {
          type: 'time',
          display: true,
          position: 'top',
          time: {
            min: '2017-12-15',
            max: '2018-12-14',
            unit: 'month',
            unitStepSize: 1,
            displayFormats: {
              month: 'MMM YY',
            },
          },
          ticks: {
            callback: function (value, index, values) {
              let shrinkFactor = 1;
              if (values.length >= 20) {
                if (values.length >= 20 && values.length < 50) {
                  shrinkFactor = 2;
                } else if (values.length >= 50 && values.length < 70) {
                  shrinkFactor = 4;
                } else if (values.length >= 70) {
                  shrinkFactor = 6;
                }
              }

              if ((index) % shrinkFactor === 0) {
                let m;
                let split = value.split(' ');

                switch (split[0]) {
                  case 'Mar':
                    m = 'Mär';
                    break;
                  case 'May':
                    m = 'Mai';
                    break;
                  case 'Oct':
                    m = 'Okt';
                    break;
                  case 'Dec':
                    m = 'Dez';
                    break;
                  default:
                    m = split[0];
                    break;
                }

                return m + ' ' + split[1];
              } else {
                return '';
              }
            },

          },
          gridLines: {
            offsetGridLines: false,
            tickMarkLength: 1,
            display: false,
          },
        },
      ],
      yAxes: [
        {
          type: 'category',
          ticks: {
            callback: function (value, index, values) {
              if (value.indexOf('break') > -1) {
                let name = value.split('.break');
                return name[0];

                // return value;
              }

              return null;

            },
          },
          gridLines: {
            tickMarkLength: 0,
            drawBorder: false,
            // color: "rgba(255,0,255,1)"
          },
        },
        {
          position: 'right',
          ticks: {
            display: false,
          },
          gridLines: {
            display: false,
            drawTicks: false,
          },
        },
      ],
    },
    plugins: {
      streaming: false,
    }
  };

  constructor(protected _colorsService: ColorsService) {
  }

  private correctDataForRange(data: LineChartData, range: ILineChartDataRange): LineChartData {
    return data.map((entry: ILineChartDataEntry) => {
      entry.lines = entry.lines.map((line: ILineChartDataEntryLine) => {
        return {
          start: line.start < range.min ? range.min : line.start,
          end: line.end > range.max ? range.max : line.end
        } as ILineChartDataEntryLine;
      });
      return entry;
    });
  }

  public ngOnInit(): void {
    this.colors = this._colorsService.getColors();
  }

  public queryUpdate() {
    this.updateEvent.emit(this.shownDataRange);
  }

  public clearData() {
    this.dataSet = [];
  }

  public updateChart() {
    if (this.chart) {
      this.chart.ngOnInit();
    }
  }

  public hasData() {
    return this.dataSet.length > 0;
  }

  private updateXAxis() {
    this.options.scales.xAxes[0].time.min = moment(this.shownDataRange.min).startOf('month').format('YYYY-MM-DD');
    this.options.scales.xAxes[0].time.max = moment(this.shownDataRange.max).endOf('month').format('YYYY-MM-DD');

    this.options.scales.xAxes[1].time.min = moment(this.shownDataRange.min).subtract(1, 'month').format("YYYY-MM") + "-15";
    this.options.scales.xAxes[1].time.max = moment(this.shownDataRange.max).format("YYYY-MM") + "-14";
  }

  @Output('update') updateEvent: EventEmitter<ILineChartDataRange> = new EventEmitter();

  //region Inputs
  @Input()
  public set dataTransformation(transformFn: LineChartDataTransformationFn) {
    this._transformFn = transformFn;
  }

  @Input()
  public set rangeTransformation(transformFn: LineChartDataRangeTransformationFn) {
    this._rangeTransformFn = transformFn;
  }

  @Input()
  public set yearStart(val) {
    this._yearStart = val;
    this.updateXAxis();

    this.updateChart();
  }

  @Input()
  public set colors(colors: string[]) {
    let tmpSet = [];
    for (let color of colors) {
      tmpSet.push({
        borderColor: color,
      });
    }

    this.colorSet = tmpSet;
  }

  @Input()
  public set yearEnd(val) {
    this._yearEnd = val;
    this.updateXAxis();

    this.updateChart();
  }

  @Input()
  public set dateRange(range: any) {
    if (range) {
      if (!!this._rangeTransformFn) {
        this.dataRange = TimeLineChartComponent.correctRange(this._rangeTransformFn(range));
      } else {
        this.dataRange = TimeLineChartComponent.correctRange(range);
      }
    }
  }

  @Input()
  public set data(data: any) {
    this.clearData();

    if (data === undefined) {
      return;
    }

    // format arbitrary data into a format used by this component to display the data
    let transformedData: LineChartData = this._transformFn(data, this.shownDataRange);

    if (this.shownDataRange) {
      transformedData = this.correctDataForRange(transformedData, this.shownDataRange);
    }

    // compute maximum range from given data if no date range was specified explicitly
    if (!this.dataRange) {
      let range: ILineChartDataRange | null = TimeLineChartComponent.getRange(transformedData);
      if (range) {
        this.dataRange = range;
      }
    }

    // set labels
    const labels: string[] = transformedData.map((entry: ILineChartDataEntry) => entry.label);
    this.labels = [...labels]; // cloning
    labels.unshift('empty.front');
    labels.push('empty.end');
    this.options.scales.yAxes[0].labels = labels;

    this.dataSet = transformedData.map((entry: ILineChartDataEntry) => {
      let dataArr: (LineChartCoordinates | null)[] = [];
      for (let line of entry.lines) {
        dataArr.push({
          x: line.start.toISOString(),
          y: entry.label
        });
        dataArr.push({
          x: line.end.toISOString(),
          y: entry.label
        });
        dataArr.push(null); // to enable breaks between lines
      }

      return {
        fill: false,
        data: dataArr,
        label: entry.label
      } as ING2ChartsEntry;
    });

    setTimeout( () => {
      this.updateChart();
    }, 100);
  }

  private _tempShownDataRange: ILineChartDataRange;

  @Input()
  public set shownDateRange(range: ILineChartDataRange) {
    if (this.dataRange) {
      this.sliderValueMin = TimeLineChartComponent.monthDiff(this.dataRange.min, moment(range.min).startOf('month').toDate());
      this.sliderValueMax = TimeLineChartComponent.monthDiff(this.dataRange.min, moment(range.max).endOf('month').toDate());
      this.shownDataRange = TimeLineChartComponent.correctRange(range);
    } else {
      this._tempShownDataRange = TimeLineChartComponent.correctRange(range);
    }
  }
  //endregion

}
