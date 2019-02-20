import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { Logger } from '@upe/logger';
import { BaseChartDirective } from 'ng2-charts';
import { RouterLocalService } from '@shared/architecture/services/router.local.service';
import { ColorsService } from '@shared/utils/colors.service';

interface BarGroup {
  name: string,
  laufzeiten: Array<{
    konto: string,
    begin: string,
    end: string
  }>
}

@Component({
  selector: 'app-timeline-chart',
  templateUrl: './time-line-chart.component.html',
  styleUrls: ['./time-line-chart.component.scss'],
})
export class TimeLineChartComponent implements OnInit {

  public chartHeight = '100px';

  public dataSet;

  public personalSet = [];

  public kontenSet = [];

  private _yearStart: string = String(new Date().getFullYear());
  private _yearEnd: string = String(new Date().getFullYear());
  public _dateRange = [this._yearStart];


  public get yearStart() {
    return this._yearStart;
  }

  private logger: Logger = new Logger({ name: 'TimeLineChartComponent' });

  @Input()
  public set yearStart(val) {
    this._yearStart = val;
    this.updateXAxis();

    this.updateChart();
  }

  @ViewChild(BaseChartDirective)
  private chart: BaseChartDirective;

  public get yearEnd() {
    return this._yearEnd;
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

  @Output('update') updateEvent: EventEmitter<string> = new EventEmitter();

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

  public getColor (i) {
    return this.colorSet[i % this.colorSet.length].borderColor;
  }

  @Input()
  public hideYearSelect: boolean = false;

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
  };

  constructor(
    protected _routerLocalService: RouterLocalService,
    protected _colorsService: ColorsService
  ) {
  }

  public ngOnInit(): void {
    this.clearData();


    this.colors = this._colorsService.getColors();

  }

  public queryUpdate() {
    let param = { start: this.yearStart, end: this.yearEnd };
    this.updateEvent.emit(JSON.stringify(param));
  }



  @Input()
  public set dateRange(value: FinanzierungsJahreDTO) {
    let currentYear = (new Date()).getFullYear();

    if (value) {
      this._dateRange = [];
      if (value.abschlussjahr < currentYear) {
        this._yearStart = String(value.abschlussjahr);
        this._yearEnd = String(value.abschlussjahr);
        this.queryUpdate();
      }

      for (let i = value.startjahr; i <= value.abschlussjahr; i++) {
        this._dateRange.push(String(i));
      }
    }

    this.updateXAxis();
  }

  public clearData() {
    this.dataSet = [];
  }


  @Input()
  public set data(data: VertragLaufzeitEntryDTO[]) {
    if (data === undefined) {
      this.clearData();
      return;
    }
    this.setData(data);
  }

  public setData(data: VertragLaufzeitEntryDTO[]) {

    this.clearData();

    let kostenStellen = [];

    let vertrag = [];

    data.forEach( (entry: VertragLaufzeitEntryDTO) => {
      entry.kostenstellen.forEach( (kostenStelle) => {
        kostenStellen.push(kostenStelle);
      });

      vertrag.push(entry.vertrag);

    });

    this.kontenSet = this.extractKonten(kostenStellen, vertrag);

    this.personalSet = this.extractPersonal(null);

    this.options.scales.yAxes[0].labels = this.setYAxis(this.personalSet, this.kontenSet);

    this.dataSet = this.parseData(kostenStellen, vertrag);

  }

  public updateChart() {
    if (this.chart) {
      this.chart.ngOnInit();
    }
  }

  public hasData() {
    if (this.dataSet.length > 0)
      return true;

    return false;
  }

  private updateXAxis() {
    this.options.scales.xAxes[0].time.min = this.yearStart + '-01-01';
    this.options.scales.xAxes[0].time.max = this.yearEnd + '-12-31';

    this.options.scales.xAxes[1].time.min = (Number(this.yearStart) - 1) + '-12-15';
    this.options.scales.xAxes[1].time.max = this.yearEnd + '-12-14';
  }

  private parseData(kostenStellen: LaufzeitEntryDTO[], vertragDTO: LaufzeitEntryDTO[]) {

    let tmpSet = [];

    // Create Tuple (start and end point of the line) and combine it with
    // y axis attribute: personalName.konto

    this.personalSet.forEach(person => {
      vertragDTO.forEach( (vertrag) => {
        let vPoints = [
          {
            x: vertrag.startDatum,
            y: person + '.' + vertrag.name,
          }, {
            x: vertrag.endDatum,
            y: person + '.' + vertrag.name,
          },
        ];

        if (!tmpSet[vertrag.name]) {
          tmpSet[vertrag.name] = [];
        }

        tmpSet[vertrag.name].push(vPoints);
      });



      kostenStellen.forEach(ks => {
        let points = [
          {
            x: ks.startDatum,
            y: person + '.' + ks.name,
          }, {
            x: ks.endDatum,
            y: person + '.' + ks.name,
          },
        ];

        if (!tmpSet[ks.name]) {
          tmpSet[ks.name] = [];
        }

        tmpSet[ks.name].push(points);
      });
    });

    // Group similar konto into one group
    let resultSet = [];

    for (let set in tmpSet) {
      let data = {
        fill: false,
        data: [],
        label: set,
      };

      for (let points of tmpSet[set]) {
        data.data.push(points[0]);
        data.data.push(points[1]);
        data.data.push(NaN); // Enables breaks between lines
      }
      resultSet.push(data);
    }

    return resultSet;

  }

  private setYAxis(personal, konten) {

    let axisLabels = [];

    if (personal.length < 2) {
      axisLabels.push('empty.front');
    }

    for (let person of personal) {

      if (personal.length > 1) {
        axisLabels.push(person + '.' + 'break');
      }

      for (let konto of konten) {
        let label = person + '.' + konto;
        axisLabels.push(label);
      }
    }

    axisLabels.push('empty.end');
    return axisLabels;
  }

  private extractPersonal(data: VertragLaufzeitEntryDTO) {
    /*
     var tmpSet = [];

     for (let barGroup of data) {
     if (tmpSet.indexOf(barGroup.name) == -1)
     tmpSet.push(barGroup.name);
     }
     */
    return ['MA01'];
  }

  private extractKonten(kostenstellen: LaufzeitEntryDTO[], vertragDTO: LaufzeitEntryDTO[]) {

    let vertrag;

    vertragDTO.forEach( (data) => {
      vertrag = data.name;
    });

    let tmpSet = kostenstellen.map(a => a.name).reduce((acc, cur) => {
      if (acc.indexOf(cur) < 0) {
        acc.push(cur);
      }

      return acc;
    }, []);

    tmpSet.unshift(vertrag);


    return tmpSet;
  }

}
