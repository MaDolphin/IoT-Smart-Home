import {Component, HostBinding, Input, OnInit, ViewChild} from '@angular/core';
import {RouterLocalService} from '@shared/architecture/services/router.local.service';
import {ColorsService} from '@shared/utils/colors.service';
import {BaseChartDirective} from 'ng2-charts';
import 'chartjs-plugin-streaming';
import {StringToDatePipe} from '@shared/pipes/string-to-date.pipe';

export type LineDataGroup = {
  label?: string,
  data: {
    x: number | string,
    y: number,
  }[]
  stack?: string
  color?: string
  interval?: number
  intervalLag?: number
}

@Component({
  selector: 'line-chart',
  templateUrl: './line-chart.component.html',
})
export class LineChartComponent implements OnInit {

  public colorSet = [];

  @HostBinding('style.display') display = 'block';
  @ViewChild(BaseChartDirective) private _chart;

  @Input()
  public set colors(colors: string[]) {
    this.colorSet = colors.map((color) => {
      return {
        backgroundColor: color,
        borderColor: color,
      };
    });
  }

  private _realtime: boolean = true;

  @Input()
  public set realtime(realtime: boolean) {
    this._realtime = realtime;
    if (realtime) {
      this.options.scales.xAxes[0].type = 'realtime';
      this.options.scales.xAxes[0].realtime = {
        duration: 20000,
        refresh: 1000,
        delay: 10000,
        ttl: 100000
      }
    } else {
      this.options.scales.xAxes[0].type = 'linear';
      this.options.scales.yAxes[0].type = 'linear';
    }

  }

  public get realtime(): boolean {
    return this._realtime;
  }

  /*
  public onRefresh() {
    this.dataSet.forEach((dataset) => {
      dataset.data.push({
        x: Date.now(),
        y: this.randomScalingFactor()
      });
    });
  }
  /**/

  private randomScalingFactor() {
    return (Math.random() > 0.5 ? 1.0 : -1.0) * Math.round(Math.random() * 100);
  }

  public dataSet: any[] = [];
  public displayValue: any;

  private _data: LineDataGroup[] = [];
  private _delay: number;
  private interval: number | null;

  @Input()
  public set title(title: string | any) {
    this.options.title.display = true;
    this.options.title.position = 'top';
    this.options.title.fontSize = 18;
    this.options.title.lineHeight = 1.6;
    if (typeof title === 'string') {
      this.options.title.text = title;
    } else {
      for (let key of Object.keys(title)) {
        this.options.title[key] = title[key];
      }
    }

    this.reload();
  }


  @Input()
  public set stepSizeY(stepSize: number) {
    this.options.scales.yAxes[0].ticks.stepSize = stepSize;

    this.reload();
  }

  @Input()
  public set stepSizeX(stepSize: number) {
    this.options.scales.xAxes[0].ticks.stepSize = stepSize;

    this.reload();
  }

  @Input()
  public overwriteData: boolean = false;

  constructor(
    protected _colorsService: ColorsService,
    protected _strToDate: StringToDatePipe) {}

  private setXAxisSize(min: number, max: number) {
    /*
    this.options.scales.xAxes[0].ticks.min = min;
    this.options.scales.xAxes[0].ticks.max = max;
    /**/
  }

  public options: any = {
    responsive: true,
    animation: {
      duration: 0
    },
    title: {
      display: false,
      position: 'top',
      text: ''
    },
    legend: {
      position: 'bottom',
      labels: {
        boxWidth: 20,
        usePointStyle: false,
      }
    },
    scales: {
      xAxes: [
        {
          id: 'x-axis-1',
          type: 'realtime',
          display: true,
          position: 'bottom',
          gridLines: {
            display: true,
          },
          ticks: {
            min: 0,
            max: 20,
            stepSize: 1,
          }
        }
      ],
      yAxes: [
        {
          id: 'y-axis-1',
          display: true,
          position: 'left',
          ticks: {
            min: 0,
            max: 600,
            stepSize: 50000,
            callback: function (value, index, values) {
              return new Intl.NumberFormat().format(value);
            },
          }

        }
      ]
    },
    elements: {
      line: {
        fill: false,
        tension: 0.0,
        spanGaps: true
      }
    },
    tooltips: {
      callbacks: {
        label: (tooltipItem, data) => {
          let label = data.datasets[tooltipItem.datasetIndex].label || '';
          let betrag = new Intl.NumberFormat('de-DE', {style: 'currency', currency: 'EUR'}).format(tooltipItem.yLabel);
          return label + ': ' + betrag;
        }
      }
    }
  };

  public hasData(): boolean {
    if (this.dataSet !== null && this.dataSet.length > 0) {
      return true;
    }

    return false;
  }

  public ngOnInit(): void {
    if (this.colorSet.length === 0)
      this.colors = this._colorsService.getColors();
  }

  @Input()
  public widthInPoints: number;

  @Input()
  public set data(data: LineDataGroup[]) {
    if (this.overwriteData) {
      this._data = data;
    } else {
      this.update(data);
    }
    if (this._data.length > 600) {
      this._data = this._data.slice(100);
    }

    if (this._data) {
      this.dataSet = [];
      if (!this.realtime) {
        this.calcXAxisBoundries(this._data);
      }
      this._data.forEach(value => {
        let dataArray: {x: number, y: number}[] = [];

        value.data.forEach(value1 => {
          dataArray.push({
            x: this.xAxisToNumber(value1.x),
            y: value1.y,
          });
        });
        if (value.interval && this.interval === undefined) {
          this.interval = value.interval;
        }
        if (this.interval !== undefined) {
          let lag = value.intervalLag || 0;
          for (let i = 0; i < dataArray.length; ++i) {
            let entry = dataArray[i];
            if (i < dataArray.length - 1
              && entry.x + value.interval < dataArray[i + 1].x - lag) {
              dataArray.splice(i + 1, 0, {x: entry.x + value.interval, y: null});
            }
          }
        }

        this.dataSet.push({
          data: dataArray,
          label: value.label,
        });
      });
      if (this._data.length && this.realtime && !this._delay) {
        let allData: {x: number, y: number}[] = [].concat(...this.dataSet.map(gr => gr.data));
        let min = Math.min(...allData.map(entry => entry.x));
        this._delay = Date.now() - min + 1000;
        this.options.scales.xAxes[0].realtime.delay = this._delay;
      }
      if (this._chart) {
        this._chart.refresh();
      }
    }
  }

  private calcXAxisBoundries(data: LineDataGroup[]) {
    let min = Number.MAX_SAFE_INTEGER;
    let max = Number.MIN_SAFE_INTEGER;


    data.forEach(value => {
      value.data.forEach(value1 => {
        min = Math.min(min, this.xAxisToNumber(value1.x));
        max = Math.max(max, this.xAxisToNumber(value1.x));
      })
    });

    if (this.widthInPoints) {
      this.setXAxisSize(max - this.widthInPoints * this.stepSizeX + 1, max);
    } else {
      this.setXAxisSize(min, max);
      if (data.length) {
        this.widthInPoints = max - min;
      }
    }
    if (this._chart) {
      this._chart.refresh();
    }
  }

  private reload() {
    if (!this.realtime) {
      this.options = JSON.parse(JSON.stringify(this.options));
    }
  }

  // merging data with current
  private update(updateSet: LineDataGroup[]) {
    if (this._data.length) {
      this._data.forEach(gr => {
        let match = updateSet.find(group => group.label === gr.label);
        if (match) {
          gr.data = gr.data.concat(match.data);
        }
      });

      updateSet.forEach(gr => {
        if (this.isEmptyGroup(gr)) {
          this._data.forEach(group => {
            group.data.push({
              x: gr.data[0].x,
              y: null
            });
          });
        } else if (!this._data.some(group => group.label === gr.label)) {
          this._data.push(gr);
        }
      });
      this._data = [...this._data];
    } else if (!updateSet.every(gr => this.isEmptyGroup(gr))) {
      this._data = updateSet;
    }
  }

  private isEmptyGroup(updateGroup: LineDataGroup) {
    return updateGroup.label === null || updateGroup.label === undefined;
  }

  private xAxisToNumber(value: number | string) {
    let x = value;
    if (typeof x === 'string') {
      x = Date.parse(x);
    } else {
      x = +x;
    }
    return x;
  }

}