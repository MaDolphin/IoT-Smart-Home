/* (c) https://github.com/MontiCore/monticore */

import { Component, Input, OnInit } from '@angular/core';
import { Logger } from '@upe/logger';
import { BehaviorSubject } from 'rxjs';

import { GetRelativeValueToPipe } from '@shared/pipes/get-relativ-value-to.pipe';
import { asMoney } from '@shared/components/guidsl/data-table/data-table.trasformation';
import { CurrencyPipe } from '@shared/pipes/currency.pipe';

@Component({
  selector: 'macoco-pie-chart',
  templateUrl: './pie-chart.component.html',
  styleUrls: ['./pie-chart.component.scss']
})
export class PieChartComponent implements OnInit {

  // TODO: this should ensure that component loads when ready, but this does not work yet
  private _entries = new BehaviorSubject<number[]>([]);

  private _titles: string[] = ['', '', ''];

  @Input()
  legendPosition: string = 'right';

  @Input()
  public displayValue: any = asMoney();

  set entries(value: number[]) {
    this._entries.next(value);

    if (this.entries.length > this.colors.length || this.entries.length > this.titles.length) {
      this.logger.error(
        `MAF0x00BF: Array of the data entries should have no longer length,
         than length of colors or titles`);
      throw new Error(
        `MAF0x00BF: Array of the data entries should have no longer length,
         than length of colors or titles`);
    }

    this._data = [];
    for (let i = 0; i < this.entries.length; ++i) {
      let entryValue;
      if (this.displayValue !== undefined) {
        entryValue = this.displayValue(this.entries[i]);
      } else {
        entryValue = this.entries[i];
      }
      let pieChartEntry = {
        value: entryValue,
        title: this.titles[i],
        color: this.colors[i]
      };
      this._data.push(pieChartEntry);
    }
  }

  get entries(): number[] {
    return this._entries.getValue();
  };

  @Input()
  legendTitle: string = '';

  @Input()
  hasWarning: boolean = true;

  colors: string[] = ['#00549f', '#555555', '#57ab27', '#f6a800', '#006165', '#A11035', '#0098A1', '#612158'];

  set titles(value: string[]) {
    this._titles = value;
  };

  get titles(): string[] {
    return this._titles;
  };

  total: number = 0;

  options: any;

  _data: { value: string, title: string, color: string }[];
  actualTotal: number = 0;

  percentage: string[] = [];
  overflowing: boolean[] = [];
  warningSet: string[] = [
    'Die Ausgaben übersteigen das Budget!',
    'Die Planung übersteigt das Budget!',
  ];

  private logger: Logger = new Logger({ name: 'PieChartComponent' });


  public set warnings(warnings: string[] | string) {
    if (warnings instanceof Array) {
      this.warningSet = warnings;
    } else {
      this.warningSet = [warnings];
    }
  }

  public get warning(): string {
    let title: string = '';
    if (this.hasWarning) {
      for (let i = 0; i < this.overflowing.length; ++i) {
        if (this.overflowing[i]) {
          if (this.warningSet[i]) {
            title = this.warningSet[i];
          } else {
            title = this.warningSet[this.warningSet.length - 1];
          }
          break;
        }
      }
    }
    return title;
  }

  constructor(
    private percentagePipe: GetRelativeValueToPipe,
    private cur: CurrencyPipe
  ) {
  }

  public ngOnInit(): void {

    this._entries.subscribe((values) => {

      this.percentage = [];
      this.overflowing = [];

      // percentage text for small charts
      let valTotal = 0;
      this.actualTotal = 0;
      let vals = values.slice();
      // if percentage should be floored
      let floor = true;
      let balancedPercentage = [];

      for (let i = 0; i < vals.length; ++i) {
        // negative values
        if (vals[i] < 0) {
          values[i] = 0;
        }
        let perc = this.percentagePipe.transform(vals[i], this.total);

        valTotal += vals[i];
        this.actualTotal += values[i];
        if (valTotal > this.total && !!this.total) {
          this.percentage[i] = '>100 %';
          this.overflowing.push(true);
          for (let j = i + 1; j < vals.length; ++j) {
            this.percentage[j] = '';
            this.overflowing.push(true);
          }
          break;
        } else {
          this.percentage[i] = perc + ' %';
          balancedPercentage.push(perc);
          this.overflowing.push(false);
        }
      }
      if (this.overflowing.every(o => !o)
        && balancedPercentage.reduce((a, b) => { return a + b }, 0) > 100) {
        this.balancePercentage(vals, this.total, balancedPercentage);
        balancedPercentage.forEach((perc, i) => {
          this.percentage[i] = perc + ' %';
        })
      }

      if (!this.hasWarning) {
        this.overflowing = [];
      }

      // If there are originally negative values for chart
      if (this.actualTotal > valTotal) {
        this.actualTotal = this.total;
      }

      // Options for the pie chart
      this.options = {
        responsiveAnimationDuration: 50,
        responsive: true,
        cutoutPercentage: 70,
        legend: { display: false },
        tooltips: {
          enabled: false,
          custom: function (tooltipModel) {
            // Tooltip Element
            let tooltipEl = document.getElementById('chartjs-tooltip');

            // Create element on first render
            if (!tooltipEl) {
              tooltipEl = document.createElement('div');
              tooltipEl.id = 'chartjs-tooltip';
              document.body.appendChild(tooltipEl);
            }

            // Hide if no tooltip
            if (tooltipModel.opacity === 0) {
              tooltipEl.style.display = 'none';
              return;
            }

            // Set caret Position
            tooltipEl.classList.remove('above', 'below', 'no-transform');
            if (tooltipModel.yAlign) {
              tooltipEl.classList.add(tooltipModel.yAlign);
            } else {
              tooltipEl.classList.add('no-transform');
            }

            // Set Text
            if (tooltipModel.body) {
              let innerHtml = [
                (tooltipModel.body[0].lines || []).join('\n')
              ];
              for (let i = 0; i < innerHtml.length; ++i) {
                let index = innerHtml[i].lastIndexOf(' ');
                let text = innerHtml[i].substring(0, index + 1);
                let percentage = innerHtml[i].substring(index + 1);
                innerHtml[i] = text + (Math.round((+percentage) * 100) / 100) + ' %';
              }
              tooltipEl.innerHTML = innerHtml.join('\n').replace(/,/g, ', ');
            }

            // `this` will be the overall tooltip
            let position = this._chart.canvas.getBoundingClientRect();
            let fontSize = 13;
            let height = 20;

            // Display, position, and set styles
            tooltipEl.style.opacity = '0.8';
            tooltipEl.style.display = 'block';
            tooltipEl.style.position = 'absolute';
            tooltipEl.style.left = position.left + tooltipModel.caretX + 'px';
            tooltipEl.style.top = position.top + tooltipModel.caretY + 'px';
            tooltipEl.style.height = height + 'px';
            tooltipEl.style.lineHeight = height - fontSize + 'px';
            tooltipEl.style.fontSize = fontSize + 'px';
            tooltipEl.style.fontStyle = tooltipModel._fontStyle;
            tooltipEl.style.padding = tooltipModel.yPadding + 'px ' + tooltipModel.xPadding + 'px';
            tooltipEl.style.backgroundColor = '#000';
            tooltipEl.style.color = '#fff';
            tooltipEl.style.borderRadius = '6px';
            tooltipEl.onmouseover = () => { if (tooltipEl) tooltipEl.style.display = 'block'; };
            tooltipEl.onmouseleave = () => { if (tooltipEl) tooltipEl.style.display = 'none'; };
          }
        }
      };
    });
  }

  /**
   * Changes percentage values of an array to match 100 percent sum
   * @param value The array of values, for which percentages are calculated
   * @param to The total value, which relates to 100 percent
   * @param res The array of percentages to balance
   */
  private balancePercentage(arr: number[], to: number, res: number[]): null {
    let index = [];
    arr.forEach((value, i) => {
      let val = Math.round(value / to * 1000).toString().split('.')[0];
      let decimals = parseInt(val.slice(-2));
      index.push([i, decimals]);
    });
    let sorts = index.sort((a, b) => a[1] - b[1]).map(el => el[0]);

    for (let i of sorts) {
      if (res.reduce((a, b) => { return a + b }, 0) > 100) {
        res[i] -= 1;
      } else {
        return
      }
    }
  }

  // TODO: set up interface for basic dom elements to have the same kind of function as this
  /**
   * Function to set pie chart data, intended to be used with asynchronous data,
   * which comes from backend
   * @param data Data, which contains pie chart values and corresponding names
   * for those values
   * @param total Value to consider as a total for the model instance
   */
  @Input()
  public set pieChartData(
    data: {
      entries: { value: number, title: string, color: string }[],
      total?: { value: number, title?: string },
      displayValue?: any
    }) {
    if (data === undefined) {
      return;
    }

    let entries = [];
    let colors = [];
    this.titles.length = 0;

    for (let d of data.entries) {
      this.titles.push(d.title);
      entries.push(d.value);
      if (d.color) {
        colors.push(d.color);
      }
    }

    if (data.total) {
      this.total = data.total.value;
      if (data.total.title) {
        this.legendTitle = data.total.title;
      }
    } else {
      this.hasWarning = false;
    }

    if (data.displayValue !== undefined) {
      this.displayValue = data.displayValue;
    }

    if (colors.length) {
      this.colors = colors;
    }
    this.entries = entries;
  }

  public setData(data: any) {
    // this.setPieData(data[0].entries, data[0].total);
  }
}
