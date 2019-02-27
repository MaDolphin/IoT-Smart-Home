/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { XLSXService } from '@components/data-table/XLSX.service';
import * as XLSX from 'xlsx';
import * as SaveAs from 'file-saver';

describe('Data Table', () => {

  describe('XLSX Service', () => {

    let xlsx, data;

    beforeEach(() => {
      spyOn(SaveAs, 'saveAs').and.callFake(function () {
        return {
          then: function (callBack) {
            callBack(true);
          }
        }
      });

      xlsx = new XLSXService();
      data = [{key: 'value', key2: '45'}];
    });

    it('toXLSX', () => {
      let workBook = xlsx.toXLSX(data);

      expect(workBook.SheetNames).toEqual(['Sheet1']);
      expect(XLSX.utils.sheet_to_json(workBook.Sheets['Sheet1'])).toEqual(data);
    });

    it('toCSV', () => {
      let csv = xlsx.toCSV(data);

      let resultString =
        'key;key2\n' +
        'value;45\n';

      expect(csv).toEqual(resultString);
    });

    it('saveToXLSX', () => {
      xlsx.saveToXLSX(data);
      expect(SaveAs.saveAs).toHaveBeenCalled();
    });

    it('saveToCSV', () => {
      xlsx.saveToCSV(data);
      expect(SaveAs.saveAs).toHaveBeenCalled();
    })
  });

});
