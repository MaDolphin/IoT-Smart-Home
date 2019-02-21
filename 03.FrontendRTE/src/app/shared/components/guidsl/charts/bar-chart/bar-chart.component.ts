import { AfterViewInit, Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { Logger } from '@upe/logger';
import { BaseChartDirective } from 'ng2-charts';
import { RouterLocalService } from '@shared/architecture/services/router.local.service';
import * as Chart from 'chart.js'
import { ColorsService } from '@shared/utils/colors.service';


interface BarDataGroup {
  label: string,
  data: number[]
  stack?: string
  backgroundColor?: string
}

@Component({
  selector: 'app-bar-chart',
  templateUrl: './bar-chart.component.html',
  styleUrls: ['./bar-chart.component.scss']
})
export class BarChartComponent implements OnInit, AfterViewInit {

  public MONTHS = ['Jan', 'Feb', 'Mär', 'Apr', 'Mai', 'Jun', 'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Dez'];

  private _yearStart = '2018';
  private _yearEnd = '2018';
  private _xAxis = [];

  public chartHeight = '100px';

  public _customLegend;

  @Input()
  public set tickSizePercentage(size: number) {
    this.tickSize.percentage = size;
  }

  public colorSet = [];
  public _dateRange = [2018, 2019];

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

  private defaultColor = ['#00549f', '#555555', '#57ab27', '#554496', '#87FF87', '#980000'];
  private _displayYear: number = (new Date()).getFullYear();
  private logger: Logger = new Logger({name: 'BarChartComponent'});

  public get displayYear() {
    return this._displayYear;
  }

  @ViewChild(BaseChartDirective)
  private chart: BaseChartDirective;

  public set displayYear(year: number) {
    this._displayYear = year;

    // TODO only after init
    // this.chart.ngOnInit();
  }

  @Output('update') updateEvent: EventEmitter<string> = new EventEmitter();

  @Input()
  public set colors(colors: string[]) {
    /*
    this.colorSet = colors.map((color) => {
      return { backgroundColor: color };
    });
*/


    let i = 0;
    for (; i < colors.length; i++) {
      if (i < this.colorSet.length)
        this.colorSet[i].backgroundColor = colors[i];
      else
        break;
    }

    for (; i < colors.length; i++) {
      this.colorSet.push(
        {
          backgroundColor: colors[i]
        });

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

  public dataSet: any = []; // BarDataGroup[];

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
  public hideYearSelect: boolean = false;

  @Input()
  public cmd;

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
    elements: {point: {radius: 0}},
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

      this.chart.datasets = this.dataSet;
      this.chart.labels = this.xAxis;
      this.chart.ngOnInit();
    }
  }

  public get yearStart() {
    return this._yearStart;
  }


  public set yearEnd(val) {

    this._yearEnd = val;
    this.updateXAxis();
  }

  public set yearStart(val) {
    this._yearStart = val;
    this.updateXAxis();
  }

  public get yearEnd() {
    return this._yearEnd;
  }

  public get xAxis() {
    return this._xAxis;
  }

  public set xAxis(array) {
    this._xAxis = array;
  }

  public hasData(): boolean {
    if (this.dataSet != null && this.dataSet.length > 0)
      return true;

    return false;
  }


  public updateChart() {
    if (this.chart) {
      this.chart.ngOnInit();
    }
  }

  public updateXAxis() {
    let m: any = [];

    for (let year = Number(this.yearStart); year <= Number(this.yearEnd); year++) {
      m = m.concat(this.MONTHS.map((month) => {
        return month + ' ' + year.toString().slice(-2);
      }));
    }
    this.xAxis = m;
  }

  public updateYAxis(type: string) {
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

  constructor(protected _routerLocalService: RouterLocalService, protected _colorsService: ColorsService) {
  }

  public ngOnInit(): void {


    this.updateXAxis();

    let currentYear = (new Date()).getFullYear();

    this.colors = this._colorsService.getColors();
    this._dateRange = [currentYear, currentYear];

    Chart.Tooltip.positioners.custom = this.customTooltipPosition;


  }

  public ngAfterViewInit() {
    this.yearStart = String((new Date()).getFullYear());
    this.yearEnd = String((new Date()).getFullYear());

    this.fetchData();
  }

  @Input()
  public set data(data: any) {
    this.setData(data);
  }

  public setData(data: any) {
    this.updateXAxis();

    let obj;
    if (data != null)
      obj = data;
    else
      return;

    this.dataSet = [];
  }

  @Input()
  public set dateRange(value: { startjahr: number, abschlussjahr: number }) {

    let currentYear = (new Date()).getFullYear();

    if (value) {
      this._dateRange = [];
      if (value.abschlussjahr < currentYear) {
        this._yearStart = String(value.abschlussjahr);
        this._yearEnd = String(value.abschlussjahr);
        this.fetchData();
      }
      for (let i = value.startjahr; i <= value.abschlussjahr; i++) {
        this._dateRange.push(i);
      }
    }
  }


  public fetchData() {
    let param = {start: this.yearStart, end: this.yearEnd};
    this.updateEvent.emit(JSON.stringify(param));
  }

  private createEmptyDataSet(label: string): BarDataGroup {
    let data = [];

    for (let year = Number(this.yearStart); year <= Number(this.yearEnd); year++) {
      for (let m = 0; m < this.MONTHS.length; m++) {
        data.push(NaN);
      }
    }

    return {
      label: label,
      data: data
    };
  }

  private parseLegendFinanzierungsInDetails(dataSet) {
    return dataSet.reduce((acc, cur) => {
      if (acc.findIndex((ks) => {
        return ks.label === cur.label;
      }) === -1) {
        acc.push({
          label: cur.label,
          color: this.colorSet[acc.length].backgroundColor
        });
      }

      return acc;
    }, []);
  }

  private parseColorsFinanzierungsInDetails(dataSet): string[] {
    let mapping = {};

    dataSet.forEach(ks => {
      if (!mapping.hasOwnProperty(ks.label)) {
        let index = Object.keys(mapping).length;
        mapping[ks.label] = this.colorSet[index].backgroundColor
      }
    });

    let res = dataSet.map(ks => {
      return mapping[ks.label];
    });

    return res;
  }

  private customTooltipPosition(elements, position) {
    return {x: position.x, y: position.y};
  }
}
