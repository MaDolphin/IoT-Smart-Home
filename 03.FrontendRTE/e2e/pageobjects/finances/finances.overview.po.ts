import {getElementById} from '@utilities/helper.e2e-util';
import {MoneyToNumberPipe} from '@shared/pipes/money-to-number.pipe';
import {promise} from 'selenium-webdriver';
import {
  calculateSumOfRowCells,
  financesDatatable,
} from '@utilities/table.e2e-util';

let moneyToNumber = new MoneyToNumberPipe();

export class FinancesOverviewPo {

  static async calculateTotalAmount(): promise.Promise<number> {
    return calculateSumOfRowCells('financesDatatable', await financesDatatable.getId('Gesamtbudget'), 1);
  }

  static async calculateSpentAmount(): promise.Promise<number> {
    return calculateSumOfRowCells('financesDatatable', await financesDatatable.getId('Ausgaben'), 1);
  }

  static async calculatePlanAmount(): promise.Promise<number> {
    return calculateSumOfRowCells('financesDatatable', await financesDatatable.getId('Verplant'), 1);
  }

  static async calculateSaldoAmount(): promise.Promise<number> {
    return calculateSumOfRowCells('financesDatatable', await financesDatatable.getId('Saldo'), 1);
  }

  static async getTotalAmountOfHeader(): promise.Promise<number> {
    return FinancesOverviewPo.getAmountOfHeaderById('total');
  }

  static async getSpentAmountOfHeader(): promise.Promise<number> {
    return FinancesOverviewPo.getAmountOfHeaderById('financial');
  }

  static async getPlanAmountOfHeader(): promise.Promise<number> {
    return FinancesOverviewPo.getAmountOfHeaderById('planned');
  }

  static async getSaldoAmountOfHeader(): promise.Promise<number> {
    return FinancesOverviewPo.getAmountOfHeaderById('residual');
  }

  static async getAmountOfHeaderById(id: string): promise.Promise<number> {
    return getElementById(id).getText().then((text) => {
      return moneyToNumber.transform(text);
    });
  }

}