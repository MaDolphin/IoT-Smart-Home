/* (c) https://github.com/MontiCore/monticore */

import { DataTable } from '@components/data-table/data-table';
import { TestViewModel } from './helper';

describe('Data Table', () => {

  describe('Class', () => {

    let dt;

    beforeEach(() => {
      dt = new DataTable([], (_: string, __: TestViewModel) => { return true; });
    });

    describe('addEntry', () => {

      beforeEach(() => {
        spyOn(dt, 'addEntry').and.callThrough();
      });

      it('different entries', () => {
        dt.addEntry(new TestViewModel('0'));
        expect(dt.addEntry).toHaveBeenCalled();
        expect(dt.size).toEqual(1);

        dt.addEntry(new TestViewModel('1'));
        expect(dt.size).toEqual(2);
      });

      it('same entry', () => {
        let tvm = new TestViewModel('1');
        dt.addEntry(tvm);

        expect(() => dt.addEntry(tvm))
          .toThrowError(Error, /entry is already added to the data table/);
        expect(dt.size).toEqual(1);
      });

      it('addEntries different entries', () => {
        dt.addEntries([new TestViewModel('0'), new TestViewModel('1')]);
        expect(dt.addEntry).toHaveBeenCalledTimes(2);
        expect(dt.size).toEqual(2);
      });

      it('addEntries same entries', () => {
        expect(() => dt
          .addEntries([new TestViewModel('0'), new TestViewModel('0')]))
          .toThrowError(Error, /entry is already added to the data table/);
        expect(dt.addEntry).toHaveBeenCalledTimes(2);
        expect(dt.size).toEqual(1);
      });

    });

    describe('removeEntry', () => {

      beforeEach(() => {
        spyOn(dt, 'removeEntryByKey').and.callThrough();

        dt.addEntries([new TestViewModel('0'), new TestViewModel('1')]);
      });

      it('removeEntryByKey entry exists', () => {
        expect(dt.hasEntryByKey('0')).toBeTruthy();

        dt.removeEntryByKey('0');
        expect(dt.size).toEqual(1);
        expect(dt.hasEntryByKey('0')).toBeFalsy();
      });

      it('removeEntryByKey entry exists', () => {
        expect(dt.hasEntryByKey('2')).toBeFalsy();

        expect(() => dt.removeEntryByKey('2'))
          .toThrowError(Error, /entry is not added to the data table/);
      });

      it('entry exists', () => {
        dt.removeEntry(new TestViewModel('0'));
        expect(dt.size).toEqual(1);

        expect(() => dt.removeEntry(new TestViewModel('0')))
          .toThrowError(Error, /entry is not added to the data table/);

        expect(dt.removeEntryByKey).toHaveBeenCalledTimes(2);
      });

      it('entry not exists', () => {
        expect(() => dt.removeEntry(new TestViewModel('2')))
          .toThrowError(Error, /entry is not added to the data table/);
      });

    });

    describe('hasEntry', () => {

      beforeEach(() => {
        dt.addEntries([new TestViewModel('0'), new TestViewModel('1')]);
      });

      it('test', () => {
        expect(dt.hasEntry(new TestViewModel('0'))).toBeTruthy();
        expect(dt.hasEntry(new TestViewModel('2'))).toBeFalsy();
      });

      it('hasEntryByKey', () => {
        expect(dt.hasEntryByKey('0')).toBeTruthy();
        expect(dt.hasEntryByKey('2')).toBeFalsy();
      })

    });

  });

});
