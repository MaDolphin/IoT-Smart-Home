import { AfterViewInit, Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts';
import * as Chart from 'chart.js'
import { ColorsService } from "@shared/utils/colors.service";
import { BeispieleBarChartDTO } from "@targetdtos/beispielebarchart.dto"; // TODO: Remove since not generic
import { BeispieleBarChartEntryDTO } from "@targetdtos/beispielebarchartentry.dto"; // TODO: Remove since not generic
import { DatumsbereichDTO } from "@targetdtos/datumsbereich.dto";


interface BarDataGroup {
  label: string,
  data: number[]
  stack?: string
  backgroundColor?: string
}

@Component({
  selector: 'montigem-bar-chart',
  templateUrl: './bar-chart.component.html',
  styleUrls: ['./bar-chart.component.scss']
})
export class BarChartComponent implements OnInit, AfterViewInit {

  @ViewChild(BaseChartDirective)
  private chart: BaseChartDirective;

  public MONTHS = ['Jan', 'Feb', 'Mär', 'Apr', 'Mai', 'Jun', 'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Dez'];

  private _yearStart = '2019';
  private _yearEnd = '2019';

  private _xAxis = [];

  public dataSet: any = []; // BarDataGroup[];

  public _customLegend;

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

  //region Display Year
  private _displayYear: number = (new Date()).getFullYear();

  public get displayYear() {
    return this._displayYear;
  }

  public set displayYear(year: number) {
    this._displayYear = year;
  }
  //endregion

  @Output('update') updateEvent: EventEmitter<string> = new EventEmitter();

  //region Inputs
  @Input()
  public set data(data: any) {
    this.setData(data);
  }

  @Input()
  public set dateRange(value: DatumsbereichDTO) {
    let currentYear = (new Date()).getFullYear();

    if (value) {
      this._dateRange = [];
      if (value.abschlussjahr < currentYear) {
        this._yearStart = String(value.abschlussjahr);
        this._yearEnd = String(value.abschlussjahr);
        this.fetchData();
      } else if (value.startjahr > currentYear && value.abschlussjahr > currentYear) {
        this._yearStart = String(value.startjahr);
        this._yearEnd = String(value.abschlussjahr);
        this.fetchData();
      }

      for (let i = value.startjahr; i <= value.abschlussjahr; i++) {
        this._dateRange.push(i);
      }
    }
  }

  @Input()
  public set colors(colors: string[]) {
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
  public hideYearSelect: boolean = false;

  @Input()
  public cmd;
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

      this.chart.datasets = this.dataSet;
      this.chart.labels = this.xAxis;
      this.chart.ngOnInit();
    }
  }

  //region Year
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
  //endregion

  public setData(data: any) {
    this.updateXAxis();

    let obj;
    if (data != null)
      obj = data;
    else
      return;

    this.dataSet = [];

    if (obj instanceof BeispieleBarChartDTO) {
      this.dataSet = this.parseDataFinanzierungZusammenstellung(data);
    } else {
      console.log("Could not parse data");
      this.dataSet = [];
    }

  }

  public hasData(): boolean {
    if (this.dataSet != null && this.dataSet.length > 0) {
      return true;
    }
    return false;
  }

  //region Axis
  public get xAxis() {
    return this._xAxis;
  }

  public set xAxis(array) {
    this._xAxis = array;
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
  //endregion

  constructor(protected _colorsService: ColorsService) {
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

  public updateChart() {
    if (this.chart) {
      this.chart.ngOnInit();
    }
  }

  public fetchData() {
    let param = { start: this.yearStart, end: this.yearEnd };
    this.updateEvent.emit(JSON.stringify(param));
  }

  //region Data manipulation - TODO MF: make this more generic
  private parseDataFinanzierungZusammenstellung(dataSetx: BeispieleBarChartDTO) {
    let dataSet: BeispieleBarChartEntryDTO[] = dataSetx.entries;
    let resultSet: BarDataGroup[] = [];

    if (dataSet.length === 0 )
      return resultSet;

    // Calc Y Axis (Percentage or Hour display)
    this.updateYAxis(dataSet[0].umfang.zahlenTyp);

    // Planumfang
    let planUmfangSet = this.createEmptyDataSet('Planumfang');

    dataSet.forEach((dto) => {
      let index = (dto.jahr - Number(this.yearStart)) * 12 + (dto.monat - 1);
      planUmfangSet.data[index] = parseFloat((+dto.planUmfang.wert).toFixed(2)) / 100;
    });

    resultSet.push(planUmfangSet);

    // Umfänge
    resultSet.push(this.createEmptyDataSet('Beschäftigungsumfang'));

    dataSet.forEach((dto) => {
      let index = (dto.jahr - Number(this.yearStart)) * 12 + (dto.monat - 1);
      let setIndex = 1;
      let value = parseFloat((+dto.umfang.wert).toFixed(2)) / 100;
      if (value <= 0)
        return;

      while (setIndex < resultSet.length && !isNaN(resultSet[setIndex].data[index])) {
        setIndex++;
      }

      if (setIndex >= resultSet.length)
        resultSet.push(this.createEmptyDataSet('Monat mit Finanzierungsänderung'));

      resultSet[setIndex].data[index] = value;
    });

    return resultSet;
  }
  //endregion

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

  private customTooltipPosition(elements, position) {
    return {x: position.x, y: position.y};
  }

}
