/* (c) https://github.com/MontiCore/monticore */
import {Component, HostBinding, Input, OnInit, ViewChild} from '@angular/core';
import {ColorsService} from '@shared/utils/colors.service';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import 'chartjs-plugin-streaming';
import {StringToDatePipe} from '@shared/pipes/string-to-date.pipe';

export type GaugeDataGroup = {
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
  selector: 'gauge-chart',
  templateUrl: './gauge-chart.component.html',
})
export class GaugeChartComponent {
  
  single: any[];
  view: any[] = [500, 400];
  legend: boolean = true;
  legendPosition: string = 'below';

  colorScheme = {
    domain: ['#5AA454', '#E44D25', '#CFC0BB', '#7aa3e5', '#a8385d', '#aae3f5']
  };

  constructor() {
    var sampleData = [
      {
        "name": "Germany",
        "value": 8940000
      },
      {
        "name": "USA",
        "value": 5000000
      },
      {
        "name": "France",
        "value": 7200000
      },
      {
        "name": "UK",
        "value": 5200000
      },
      {
        "name": "Italy",
        "value": 7700000
      },
      {
        "name": "Spain",
        "value": 4300000
      }
    ];

    Object.assign(this, { sampleData });
  }

  onSelect(data): void {
    console.log('Item clicked', JSON.parse(JSON.stringify(data)));
  }

  onActivate(data): void {
    console.log('Activate', JSON.parse(JSON.stringify(data)));
  }

  onDeactivate(data): void {
    console.log('Deactivate', JSON.parse(JSON.stringify(data)));
  }

  hasData() {
    return true;
  }
}
