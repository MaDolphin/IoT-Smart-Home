import { Component, Input, OnInit } from '@angular/core';

interface BarDataGroup {
  label: string,
  data: number[]
  stack?: string
  backgroundColor?: string
}

@Component({
  selector: 'app-horizontal-bar-chart',
  templateUrl: './horizontal-bar-chart.component.html',
})
export class HorizontalBarChartComponent implements OnInit {

  @Input()
  public labels = [];

  public chartHeight = '100px';

  public colorSet = [];

  private defaultColor = ['#8dc060', '#B65256', '#CD8B87', '#555555', '#87FF87', '#980000'];

  // @Input() public horizontal: boolean = true;

  @Input()
  public set colors(colors: string[]) {
    this.colorSet = colors.map((color) => {
      return { backgroundColor: color };
    });
  }

  public dataSet: BarDataGroup[] = [];
  public displayValue: any;

  @Input()
  public set stacked(stacked: boolean) {
    this.options.scales.xAxes[0].stacked = stacked;
    this.options.scales.yAxes[0].stacked = stacked;
  }

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
  }

  @Input()
  public set legend(legend: boolean | any) {
    this.options.legend.display = true;
    this.options.legend.position = 'bottom';
    if (typeof legend === 'boolean') {
      this.options.legend.display = legend;
    } else {
      for (let key of Object.keys(legend)) {
        this.options.legend[key] = legend[key];
      }
    }
  }

  @Input()
  public set displayXAxis(display: boolean) {
    this.options.scales.xAxes[0].display = display;
  }

  public options: any = {
    responsive: true,
    maintainAspectRatio: false,
    animation: {
      duration: 1000,
    },
    title: {},
    legend: {},
    scales: {
      xAxes: [
        {
          categoryPercentage: 0.4,
          barPercentage: 1.0,
          ticks: {
            beginAtZero: true,
          },
          gridLines: {
            drawBorder: false,
            display: false
          }
        }
      ],
      yAxes: [
        {
          categoryPercentage: 0.7,
          barPercentage: 1.0,
          ticks: {
            beginAtZero: true,
          },
          gridLines: {
            drawBorder: false,
            display: false
          }
        }
      ]
    },
    tooltips: {
      callbacks: {
        label: (tooltipItem, data) => {
          let label = data.datasets[tooltipItem.datasetIndex].label || '';
          let value = +tooltipItem.xLabel;
          if (this.displayValue !== undefined) {
            value = this.displayValue(value);
          }

          return label + ': ' + value;
        }
      }
    }
  };

  public hasData(): boolean {
    if (this.dataSet != null && this.dataSet.length > 0) {
      return true;
    }

    return false;
  }

  public ngOnInit(): void {
    if (!this.colors || !this.colors.length) {
      this.colors = this.defaultColor;
    }
  }

  @Input()
  public set data(data: any) {
    if (data) {
      this.dataSet = data.values;
      this.displayValue = data.displayValue;
    }
  }

}