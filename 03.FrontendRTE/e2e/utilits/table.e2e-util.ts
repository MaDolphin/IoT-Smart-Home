import {browser, by, ElementArrayFinder, ElementFinder} from 'protractor';
import {promise} from 'selenium-webdriver';
import {getElement, moneyToNumber} from './helper.e2e-util';

class E2eDatatable {
  table: Map<string, number> = new Map<string, number>();
  tablename: string;

  constructor(tableName: string) {
    this.tablename = tableName;
  }

  async getId(key: string): promise.Promise<number> {
    if (this.table.size === 0) {
      await this.fillTable();
    }
    let ret = this.table.get(key);
    return ret === undefined ? -1 : ret;
  }

  async fillTable(): promise.Promise<void> {
    let cells: ElementFinder[] = await getDataTableHeaderCells(this.tablename);
    for (let i = 0; i < cells.length; i++) {
      this.table.set(await cells[i].getText(), i);
    }
  }
}

export const financesDatatable = new E2eDatatable('financesDatatable');
export const bookingDatatable = new E2eDatatable('genericBookingsDatatable');
export const retrievalDatatable = new E2eDatatable('genericBookingsDatatable');
export const billsDatatable = new E2eDatatable('genericBookingsDatatable');
export const billsWithFlatrateDatatable = new E2eDatatable('genericBookingsDatatable');
export const allocationDatatable = new E2eDatatable('genericBookingsDatatable');
export const jobAlllocationDatatable = new E2eDatatable('genericBookingsDatatable');
export const personalOverviewDatatable = new E2eDatatable('personalOverviewDatatable');

export function getDataTableByName(name: string): ElementFinder {
  return getElement(by.css('macoco-data-table[name=' + name + ']'));
}

export function getDataTable(tabName: string): ElementFinder {
  return getElement(by.css('[name=' + tabName + ']'));
}

export function getDataTableRows(tableName: string): ElementArrayFinder {
  return getDataTableByName(tableName).all(by.css('datatable-body-row'));
}

export function getTableRows(tabName: string): ElementArrayFinder {
  return getDataTable(tabName).all(by.css('[name=tablerows'));
}

export function getDataTableRowCells(tableName: string, rowIndex: number, cellIndex: number): ElementFinder {
  const tableRows = getDataTableRows(tableName);
  const rowCells = tableRows.get(rowIndex).all(by.css('datatable-body-cell'));
  return rowCells.get(cellIndex);
}

export async function getDataTableRowCellText(table: E2eDatatable, rowIndex: number, cellName: string): promise.Promise<string> {
  return table.getId(cellName).then((id) => {
    return getDataTableRowCells(table.tablename, rowIndex, id).getText()
  })

}

export function getTableRowCells(tabName: string, colName: string, rowIndex: number, cellIndex: number): ElementFinder {
  const tableRows = getTableRows(tabName);
  const rowCells = tableRows.get(rowIndex).all(by.css('[name=' + colName));
  return rowCells.get(cellIndex);
}

export function getDataTableHeaderCell(tableName: string, index: number) {
  const dataTable = getDataTableByName(tableName);
  return dataTable.all(by.css('datatable-header-cell')).get(index);
}

export function getDataTableHeaderCells(tableName: string): ElementArrayFinder {
  const dataTable = getDataTableByName(tableName);
  return dataTable.all(by.css('datatable-header-cell'));
}

export async function setDataTableInputFilter(tableName: string, inputValue: string) {
  return getDataTableByName(tableName).element(by.css('.filter')).element(by.css('input')).getWebElement().then((input) => {
    return input.clear().then(() => {
      input.sendKeys(inputValue)
    })
  })
}

export async function calculateSumOfRowCells(tableName: string, cellIndex: number, excludedElements: number = 0): promise.Promise<number> {
  const rows = getDataTableRows(tableName);
  let totalBudget = 0;
  await rows.count().then((elem) => {
    for (let i = 0; i < elem - excludedElements; i++) {
      getDataTableRowCells(tableName, i, cellIndex).getText().then((text) => {
        totalBudget = totalBudget + moneyToNumber.transform(text);
        totalBudget = Math.round(totalBudget * 100) / 100;
      });
    }
  });
  return totalBudget;
}

export async function sumOfAggregateSums(tableName: string, cellIndex: number): promise.Promise<number> {
  const rows = getDataTableRows(tableName);
  let total = 0;
  await rows.count().then((elem) => {
    for (let i = 0; i < elem; i++) {
      getDataTableRowCells(tableName, i, cellIndex).getText().then((text) => {
        total = total + moneyToNumber.transform(text);
      });
    }
  });
  return total;
}

export async function dragAndDropDatatableCellHeader(drag: ElementFinder, drop: ElementFinder): promise.Promise<void> {
  return browser.actions().mouseMove(drag).mouseDown().mouseMove(drop).perform().then(() => {
    return browser.actions().mouseUp().perform();
  })
}

export async function clickOnRowActionButtons(tableName: string, rowIndex: number, cellIndex: number, buttonNumber: number) {
  return getDataTableRowCells(tableName, rowIndex, cellIndex).getWebElement().then((cell) => {
    return cell.findElements(by.css('button'))
  }).then((buttons) => {
    return buttons[buttonNumber].click();
  })
}
