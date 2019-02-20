import { A } from './mocking';
import { DateToDaysConverter } from '@buchungform/expert/util/DateToDaysConverter';


describe('DateConverterTest', () => {
  let days: number;

  it('Days in Year', A(async () => {
    let valid = [
        { days: 365, date: new Date(2019, 11, 1)},
        { days: 366, date: new Date(2016, 11, 1)},
    ];

    valid.forEach( (value) => {
      days = DateToDaysConverter.getDaysInYear(value.date);
      expect(days).toBe(value.days);
    });
  }));

  it('Days to end of year', A(async () => {
    let valid = [
      { days: 1, date: new Date(2019, 11, 31)},
      { days: 31, date: new Date(2019, 11, 1)},
    ];

    valid.forEach( (value) => {
      days = DateToDaysConverter.getDaysToYearEnd(value.date);
      expect(days).toBe(value.days);
    });
  }));

  it('Days from start of the year', A(async () => {
    let valid = [
      { days: 1, date: new Date(2019, 0, 1)},
      { days: 31, date: new Date(2019, 0, 31)},
      { days: 39, date: new Date(2019, 1, 8)},
    ];



    valid.forEach( (value) => {
      days = DateToDaysConverter.getDaysFromStartYear(value.date);
      expect(days).toBe(value.days);
    });
  }));

  it('Days in Time Frame', A(async () => {
    let valid = [
      { days: 366, date1: new Date(2018, 0, 1), date2: new Date(2019, 0, 1)},
      { days: 2, date1: new Date(2018, 0, 1), date2: new Date(2018, 0, 2)},
      { days: 2, date1: new Date(2017, 11, 31), date2: new Date(2018, 0, 1)},
    ];

    valid.forEach( (value) => {
      days = DateToDaysConverter.getDaysInTimeFrame(value.date1, value.date2);
      expect(days).toBe(value.days);
    });
  }));



  it('Days in Time Frame equal sum of days', A(async () => {
    let daySum: number;

    let valid = [
      { date1: new Date(2018, 0, 1), date2: new Date(2019, 0, 1)},
      { date1: new Date(2018, 11, 12), date2: new Date(2019, 1, 8)},
    ];

    valid.forEach( (value) => {
      daySum = 0;
      days = DateToDaysConverter.getDaysInTimeFrame(value.date1, value.date2);

      let n = value.date2.getFullYear() - value.date1.getFullYear();

      for (let i = 1; i < n - 1; i++) {
        let date = new Date();
        date.setFullYear(value.date1.getFullYear() + i);

        daySum += DateToDaysConverter.getDaysInYear(date);
      }

      daySum += DateToDaysConverter.getDaysToYearEnd(value.date1);
      daySum += DateToDaysConverter.getDaysFromStartYear(value.date2);

      expect(daySum).toBe(days);
    });
  }));




});