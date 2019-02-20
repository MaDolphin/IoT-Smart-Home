/* tslint:disable:no-unused-variable */

import { async, TestBed } from '@angular/core/testing';
import * as moment from 'moment';
import { AutoCompleteDatePipe } from '@shared/pipes/auto-complete-date.pipe';
import { AutoCompleteFromArrayPipe } from '@shared/pipes/auto-complete-from-array.pipe';
import { DateToStringPipe } from '@shared/pipes/date-to-string.pipe';
import { GetRelativeValueToPipe } from '@shared/pipes/get-relativ-value-to.pipe';
import { JoinPipe } from '@shared/pipes/join.pipe';
import { MoneyToNumberPipe } from '@shared/pipes/money-to-number.pipe';
import { NumberToMoneyPipe } from '@shared/pipes/number-to-money.pipe';
import { PieChartPriorityPipe } from '@shared/pipes/piechart-priority.pipe';
import { PipesModule } from '@shared/pipes';
import { PropertyFilterPipe } from '@shared/pipes/property-filter.pipe';
import { RemoveNullsPipe } from '@shared/pipes/remove-nulls.pipe';
import { RoundPipe } from '@shared/pipes/round.pipe';
import { StringArrayFilter } from '@shared/pipes/string-array-filter.pipe';
import { StringToDatePipe } from '@shared/pipes/string-to-date.pipe';
import { SumPipe } from '@shared/pipes/sum.pipe';

describe('Pipes', () => {

  describe('Auto complete date', () => {

    it('transform', () => {

      const pipe = new AutoCompleteDatePipe(new StringToDatePipe());

      expect(pipe.transform('1.1.17')).toEqual('01.01.2017', 'date completion error!');
      expect(pipe.transform('1.11.17')).toEqual('01.11.2017', 'date completion error!');
      expect(pipe.transform('1.11.7')).toEqual('01.11.2007', 'date completion error!');

    });

  });

  describe('Auto complete from array', () => {

    it('transform', () => {

      const pipe = new AutoCompleteFromArrayPipe();

      const completions = ['eins', 'zwei', 'drei'];
      let result = 'eins';

      expect(pipe.transform('ein', completions)).toEqual(result, 'completion missing!');

    });

  });

  describe('Clone', () => {

   /* it('transform', () => {

      const pipe = new ClonePipe();
      let account: Account = TypedJSON.parse(JSON.stringify(ModelHelper.DemoAccount), Account);
      expect(typeof pipe.transform(account)).toEqual(typeof account, 'not the same type!');

    });*/

  });

  describe('Date to string', () => {

    it('transform', () => {

      const pipe = new DateToStringPipe();

      const dates = ['01.01.2017'];

      for (const dateStr of dates) {
        // construct Date
        expect(pipe.transform(moment(dateStr, 'DD.MM.YYYY').toDate())).toEqual(dateStr, `${dateStr} should be converted to  ${dateStr}`);
      }

      expect(pipe.transform(null)).toEqual('', 'null should be converted to an empty string');


    });

  });

  describe('Get relative value to', () => {

    it('transform', () => {

      const pipe = new GetRelativeValueToPipe();
      expect(pipe.transform(4, 10)).toEqual(40);
      expect(pipe.transform(4, 0)).toEqual(0);
      expect(pipe.transform(0, 4)).toEqual(0);

    });

  });

  describe('Join', () => {

    it('transform', () => {

      const pipe = new JoinPipe();
      const elementArray = ['so', 'was'];

      expect(pipe.transform(elementArray)).toEqual('so was', 'not joined!');

    });

  });


    describe('Money to Number pipe', () => {
        // This pipe is a pure, stateless function so no need for BeforeEach
        let pipe = new MoneyToNumberPipe();

        it('transforms "100,00" to "10000"', () => {
          expect(pipe.transform('100,00')).toBe(10000);
        });

        it('transforms "1000,23 €" to "100023"', () => {
          expect(pipe.transform('1000,23 €')).toBe(100023);
        });

        xit('transforms "-123.45 €" to "12345"', () => {
          // expect(pipe.transform(100)).toBe('100,00 €');
        });


        it('transform', () => {
            const moneyValues = ['10.01', '10,01'];

            for (const moneyValue of moneyValues) {
              expect(pipe.transform(moneyValue)).toEqual(Number('1001'), 'wrong cent value!');
            }

            const moneyValuesBig = ['1.000.001', '1000001', '1000001,00'];

            for (const moneyValueBig of moneyValuesBig) {
              expect(pipe.transform(moneyValueBig)).toEqual(Number('100000100'), 'wrong cent value!');
            }
        });

    });



    describe('Number to money', () => {

    beforeAll(async(() => TestBed.configureTestingModule({
                                                           declarations: [],
                                                           imports:      [
                                                             // PipesModule.forRoot(),
                                                           ],
                                                         }).compileComponents()));

    xit('transform', () => {

      const pipe = TestBed.get(NumberToMoneyPipe);

      expect(pipe.transform(0)).toEqual('0,00');
      expect(pipe.transform(1)).toEqual('1,00');
      expect(pipe.transform(1.5)).toEqual('1,50');
      expect(pipe.transform(1.55)).toEqual('1,55');
      expect(pipe.transform(1000)).toEqual('1.000,00');

      expect(pipe.transform(0, true)).toEqual('0,00 €');
      expect(pipe.transform(1, true)).toEqual('1,00 €');
      expect(pipe.transform(1.5, true)).toEqual('1,50 €');
      expect(pipe.transform(1.55, true)).toEqual('1,55 €');
      expect(pipe.transform(1000, true)).toEqual('1.000,00 €');

      const symbol = '$';

      expect(pipe.transform(0, true, symbol)).toEqual('0,00 ' + symbol);
      expect(pipe.transform(1, true, symbol)).toEqual('1,00 ' + symbol);
      expect(pipe.transform(1.5, true, symbol)).toEqual('1,50 ' + symbol);
      expect(pipe.transform(1.55, true, symbol)).toEqual('1,55 ' + symbol);
      expect(pipe.transform(1000, true, symbol)).toEqual('1.000,00 ' + symbol);

    });

  });

  describe('Pie chart budget', () => {

    beforeAll(async(() => TestBed.configureTestingModule({
                                                           declarations: [],
                                                           imports:      [
                                                             PipesModule.forRoot(),
                                                           ],
                                                         }).compileComponents()));

  });

  describe('Pie chart priority', () => {

    it('transform', () => {

      const pipe = new PieChartPriorityPipe(new RoundPipe(), new SumPipe());

      // first + second + third = 100%
      expect(pipe.transform([9300, 200, 500], 10000)).toEqual([93, 2, 5]);

      // check if rounded properly?
      expect(pipe.transform([50005, 49995], 100000)).toEqual([50.01, 49.99]);

      // first > 100%
      expect(pipe.transform([9300, 200, 500], 3000)).toEqual([100]);

      // first + second > 100%
      expect(pipe.transform([1000, 2500, 500], 3000)).toEqual([33.33, 66.67]);

      // first + second + third < 100%
      expect(pipe.transform([1000, 1000, 500], 3000)).toEqual([33.33, 33.33, 33.34]);

      // first + second = 100%
      expect(pipe.transform([1000, 2000, 500], 3000)).toEqual([33.33, 66.67]);

      // first + second < 100% (no assumptions on input)
      expect(pipe.transform([1000, 1000], 3000)).toEqual([33.33, 66.67]);

    });

  });

  describe('Round', () => {

    it('transform', () => {

      const pipe = new RoundPipe();

      expect(pipe.transform(50.00000000001)).toEqual(50);

      expect(pipe.transform(50.2154125)).toEqual(50.22);

      expect(pipe.transform(50.2145)).toEqual(50.21);

      expect(pipe.transform(50.512442, 5)).toEqual(50.51244);

    });

  });

  describe('Property filter', () => {

    xit('transform', () => {

      const pipe = new PropertyFilterPipe();

      expect(true).toBeFalsy('not implemented yet!');

    });

  });

  describe('Remove nulls', () => {

    it('transform', () => {
      // TODO: do we want to keep undefined or to get rid of them?

      const pipe = new RemoveNullsPipe();
      let empty = null;

      const elements = ['eins', empty, 'drei'];
      const result = ['eins', undefined, 'drei'];

      expect(pipe.transform(elements)).toEqual(result, 'null not removed!');

      const yaelem = ['eins', 'zwei', null];
      const yares = ['eins', 'zwei', undefined];

      expect(pipe.transform(yaelem)).toEqual(yares, 'null not removed!');

    });

  });

  describe('Search filter', () => {

    xit('transform', () => {

      // const pipe = new SearchFilter();

      expect(true).toBeFalsy('not implemented yet!');

    });

  });

  describe('String array filter', () => {

    it('transform', () => {

      const pipe = new StringArrayFilter();

      const stringArray = ['eins', 'zwei', 'drei'];
      const filterArray = ['zwei', 'drei'];
      const resultArray = ['eins'];

      expect(pipe.transform(stringArray, filterArray)).toEqual( resultArray, 'filter error!');

    });

  });

  describe('String to Date', () => {

    it('transform', () => {

      const pipe = new StringToDatePipe();

      const dates = ['01.01.2017'];

      for (const dateStr of dates) {
        let dateRes = moment(dateStr, 'DD.MM.YYYY').toDate();
        expect(pipe.transform(dateStr)).toEqual(moment(dateStr, 'DD.MM.YYYY').toDate(), `${dateStr} should be converted to ${dateRes}`);
      }

      expect(pipe.transform(null)).toEqual(null, 'null should be converted to null');

      expect(pipe.transform('')).toEqual(null, 'an empty string should be converted to null');

    });

  });

  describe('Sum', () => {

    it('transform', () => {

      const pipe = new SumPipe();

      expect(pipe.transform([50.010, 50])).toEqual(100.01);


    });

  });


});
