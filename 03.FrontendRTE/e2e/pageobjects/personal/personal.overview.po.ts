/* (c) https://github.com/MontiCore/monticore */
import {promise} from 'selenium-webdriver';
import {
  clickOnRowActionButtons,
  getDataTableRowCells,
  getDataTableRows,
  personalOverviewDatatable,
  setDataTableInputFilter
} from '../../utilits/table.e2e-util';

export class PersonalOverviewPo {

  static async setTableFilter(filterText: string): promise.Promise<void> {
    return setDataTableInputFilter('personalOverviewDatatable', filterText);
  }

  static async isTableEmpty(): promise.Promise<boolean> {
    return PersonalOverviewPo.getTableRowCount().then(elem => {
      return elem === 0;
    });
  }

  static async getOfPersonalDatatableRow(rowIndex: number, columnName: string) {
    return await getDataTableRowCells('personalOverviewDatatable', rowIndex, await personalOverviewDatatable.getId(columnName)).getText();
  }

  static async getTableRowCount(): promise.Promise<number> {
    return getDataTableRows('personalOverviewDatatable').count();
  }

  static async getPersonalnummerOfTableRow(rowIndex: number): promise.Promise<string> {
    return PersonalOverviewPo.getOfPersonalDatatableRow(rowIndex, 'Personal­nummer');
  }

  static async getNameOfTableRow(rowIndex: number): promise.Promise<string> {
    return PersonalOverviewPo.getOfPersonalDatatableRow(rowIndex, 'Name');
  }

  static async getBeschZeitRaumOfTableRow(rowIndex: number): promise.Promise<string> {
    return PersonalOverviewPo.getOfPersonalDatatableRow(rowIndex, 'Beschäftigungs­zeitraum');
  }

  static async clickOnOverviewActionOfTableRow(rowIndex: number): promise.Promise<any> {
    return clickOnRowActionButtons('personalOverviewDatatable', rowIndex, 0, 0);
  }

  static async clickOnDeleteActionOfTableRow(rowIndex: number): promise.Promise<void> {
    return clickOnRowActionButtons('personalOverviewDatatable', rowIndex, 0, 2);
  }

  static async clickOnEditActionOfTableRow(rowIndex: number): promise.Promise<any> {
    return clickOnRowActionButtons('personalOverviewDatatable', rowIndex, 0, 1);
  }
}
