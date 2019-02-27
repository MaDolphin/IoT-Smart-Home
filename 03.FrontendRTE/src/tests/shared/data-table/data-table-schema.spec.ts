/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { DataTableSchema, JsonSchema } from '@components/data-table/data-table-schema';

describe('Data Table', () => {

  describe('Schema', () => {

    class TestJsonSchema implements JsonSchema {
      title: string;
      type: string;
      key: string;

      constructor(values: string[]) {
        this.title = values[0];
        this.type = values[1];
        this.key = values[2];
      }
    }

    let dts, tjs, tjs2;

    beforeEach(() => {
      tjs = new TestJsonSchema(['title', 'type', 'key']);
      tjs2 = new TestJsonSchema(['title2', 'type2', 'key2']);

      dts = new DataTableSchema([tjs, tjs2]);
    });

    it('value test', () => {
      expect(dts.columnTitles).toEqual(['title', 'title2']);
      expect(dts.columnKeys).toEqual(['key', 'key2']);
      expect(dts.columnTypes).toEqual(['type', 'type2']);
    });

    describe('reOrder', () => {

      it('wrong key', () => {
        expect(() => dts.reOrder('key1', '_'))
          .toThrowError(Error, /column with key: key1 not found/);

        expect(dts.columnMeta).toEqual([tjs, tjs2]);

        expect(() => dts.reOrder('key', 'key1'))
          .toThrowError(Error, /column with key: key1 not found/);

        expect(dts.columnMeta).toEqual([tjs, tjs2]);
      });

      it('correct', () => {
        dts.reOrder('key', 'key2');
        expect(dts.columnMeta).toEqual([tjs2, tjs]);
      });

      it('multiple correct', () => {
        let tjs3 = new TestJsonSchema(['title3', 'type3', 'key3']);
        dts = new DataTableSchema([tjs, tjs2, tjs3]);

        dts.reOrder('key', 'key3');
        expect(dts.columnMeta).toEqual([tjs2, tjs3, tjs]);
      });

    });

  });

});
