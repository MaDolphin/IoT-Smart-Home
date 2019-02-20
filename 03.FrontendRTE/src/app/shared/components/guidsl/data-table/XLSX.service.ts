import { Injectable } from '@angular/core';
import { saveAs } from 'file-saver';
import * as XLSX from 'xlsx';
import { WorkBook } from 'xlsx';
import { DateToStringPipe } from '@shared/pipes/date-to-string.pipe';
import { ISO_DATE_FORMAT } from '@shared/pipes/string-to-date.pipe';

@Injectable()
export class XLSXService {

  constructor() {
  }

  public toXLSX(data: any[]): WorkBook {
    // generate worksheet
    const ws = XLSX.utils.json_to_sheet(data);
    // generate workbook and add the worksheet
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Sheet1');

    return wb;
  }

  public toCSV(data: any[]): string {
    const wb: WorkBook = this.toXLSX(data);
    return XLSX.utils.sheet_to_csv(wb.Sheets[Object.keys(wb.Sheets)[0]], {FS: ';'});

  }

  public saveToXLSX(data: any[]): void {
    const wb = this.toXLSX(data);

    // save to file
    const wbout = XLSX.write(wb, {bookType: 'xlsx', type: 'binary'});
    this.saveAs(wbout);
  }

  public saveToCSV(data: any[], name: string): void {
    const csv = this.toCSV(data);
    let filename: string = name + '_' + new DateToStringPipe().transform(new Date(), ISO_DATE_FORMAT) + '.csv';
    console.log('Export ' + name + ' to file: ' + filename);
    saveAs(new Blob(['\ufeff', csv], {type: 'text/csv'}), filename);
  }

  private saveAs(str: string, filetype: string = 'xlsx', filename: string = 'export'): void {
    saveAs(this.createBlob(this.createArrayBuffer(str)), `${filename}.${filetype}`);
  }

  private createBlob(buffer: ArrayBuffer): Blob {
    return new Blob([buffer]);
  }

  private createArrayBuffer(str: string): ArrayBuffer {
    const buf  = new ArrayBuffer(str.length);
    const view = new Uint8Array(buf);
    for (let i = 0; i !== str.length; ++i) {
      // tslint:disable:no-bitwise
      view[i] = str.charCodeAt(i) & 0xFF;
    }

    return buf;
  }

}
