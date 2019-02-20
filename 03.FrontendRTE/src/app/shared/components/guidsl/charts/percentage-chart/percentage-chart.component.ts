import { Component, Input, OnInit } from '@angular/core';
import { Logger } from '@upe/logger';

interface BarPercentageData {
  label: string,
  value: number
}

@Component({
  selector: 'app-percentage-bar-chart',
  templateUrl: './percentage-chart.component.html',
  styleUrls: ['./percentage-chart.component.scss']
})
export class PercentageChartComponent implements OnInit {

  private logger: Logger = new Logger({ name: 'PercentageChartComponent' });

  @Input()
  public colors = ['#57ab27', '#555555', '#cc071e'];

  public data: BarPercentageData[] = [
    {
      label: 'Verfügbar',
      value: 63
    },
    {
      label: 'Verplant',
      value: 10,
    },
    {
      label: 'Belastung',
      value: 25
    }
  ];



  constructor() { }

  public ngOnInit(): void {

  }

  public setData(data: any) {
    this.data = data;
  }

}
