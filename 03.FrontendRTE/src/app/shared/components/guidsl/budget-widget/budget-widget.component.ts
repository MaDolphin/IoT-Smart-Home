import { Component, Input, OnInit } from '@angular/core';
import { copyByValue } from '@shared/utils/util';
import { asMoney } from '@components/data-table/data-table.transformation';

@Component({
  selector: 'macoco-budget-widget',
  templateUrl: './budget-widget.component.html',
  styleUrls: ['./budget-widget.component.scss'],
})
export class BudgetWidgetComponent implements OnInit {

  private _budget;
  private _isSubBudget: boolean = false;

  public pieChartData;
  public expanded: boolean = false;
  public displayValue: any = asMoney();


  public set isSubBudget(value: boolean) {
    this._isSubBudget = value;
    if (!this._isSubBudget) {
      this.containerClasses.push('budget-container');
    }
  }

  @Input()
  public get isSubBudget(): boolean {
    return this._isSubBudget;
  }

  public set budget(value) {
    this._budget = value;
    let entries = this._budget.entries.map((entry) => {
      let res = copyByValue(entry);
      res.title = res.name;
      return res;
    });

    if (this._budget.total) {
      this.totalValue = this._budget.total;
    }

    if (this._budget.title) {
      this.title = this._budget.title;
    }

    this.pieChartData = {
      entries: entries,
      total: { value: this.totalValue }
    };

    if (this._budget.subbudgets) {
      this.containerClasses.push('hasSubBudgets');
    }
  }

  @Input()
  public get budget() {
    return this._budget;
  }

  public containerClasses: string[] = [];

  public totalValue;

  public title;

  constructor() { }

  ngOnInit() {
  }

}
