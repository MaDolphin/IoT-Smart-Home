import {getElementById, moneyToNumber} from '../../utilits/helper.e2e-util';
import {promise} from 'selenium-webdriver';

export class DashboardPo {

  static async getTotalAmountOfFinancesContainer(): promise.Promise<number> {
    return this.getAmountOfFinancesContainerById('total');
  }

  static async getSpentAmountOfFinancesContainer(): promise.Promise<number> {
    return this.getAmountOfFinancesContainerById('pieChartValue0');
  }

  static async getPlanAmountOfFinancesContainer(): promise.Promise<number> {
    return this.getAmountOfFinancesContainerById('pieChartValue1');
  }

  static async getSaldoAmountOfFinancesContainer(): promise.Promise<number> {
    return this.getAmountOfFinancesContainerById('pieChartValue2');
  }

  static async getAmountOfFinancesContainerById(id: string): promise.Promise<number> {
    let result = 0;
    await getElementById(id).getText().then((text) => {
      // ToDo: moneyToNumber pipe isn't able to convert texts with a currency sign to a number
      result = result + moneyToNumber.transform(text.substring(0, text.lastIndexOf(' €')));
    });
    return result;
  }

}