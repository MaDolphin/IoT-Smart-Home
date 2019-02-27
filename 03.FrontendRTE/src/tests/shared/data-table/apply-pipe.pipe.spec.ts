/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { ApplyPipePipe } from '@components/data-table/apply-pipe.pipe';
import { DateToStringPipe } from '@shared/pipes/date-to-string.pipe';
import { PipeTransform } from '@angular/core';

describe('Data Table', () => {

  describe('Apply pipe', () => {

    class TestPipe implements PipeTransform {
      transform(value: any): any {
        return value;
      }
    }

    it('transform with TestPipe', () => {
      let app = new ApplyPipePipe();
      let pipe = new TestPipe();

      let values = [
        new Date(),
        'string',
        5,
        [],
      ];

      for (let value of values) {
        expect(app.transform(value, pipe)).toEqual(pipe.transform(value));
      }
    });

    it('transform with DateToStringPipe', () => {
      let app = new ApplyPipePipe();
      let pipe = new DateToStringPipe();

      let values = [
        new Date(),
        new Date(12346789),
        new Date('Jan 1, 1970'),
        new Date(1970, 1)
      ];

      for (let value of values) {
        expect(app.transform(value, pipe)).toEqual(pipe.transform(value));
      }
    });

  });

});
