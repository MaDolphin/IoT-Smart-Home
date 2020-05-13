 /* (c) https://github.com/MontiCore/monticore */
import { BeispieleBarChartDTO } from "@targetdtos/beispielebarchart.dto";
import { BarChartData, BarChartYAxisData, IBarChartDataEntry, IBarChartDataRange } from "@components/charts/bar-chart/bar-chart.component";

import * as moment from 'moment';
import { BeispieleBarChartEntryDTO } from "@targetdtos/beispielebarchartentry.dto";

interface IBeispielBarChartRange {
  min: Date,
  max: Date
}

export function beispieleBarChartTransformation3(dto: BeispieleBarChartDTO, range: IBarChartDataRange) {
  let dtoEntries = dto.entries;
  if (dtoEntries.length === 0) {
    return []
  }

  // TODO: Set Y Axis Type

  let data: BarChartData = createEmptyDataEntries(range);

  dtoEntries.forEach((dtoEntry: BeispieleBarChartEntryDTO) => {
    let date = moment().year(dtoEntry.jahr).month(dtoEntry.monat - 1).startOf('month');

    let planUmfangValue: BarChartYAxisData = {
      label: "Planumfang",
      value: parseFloat((+dtoEntry.planUmfang.wert).toFixed(2)) / 100
    };

    let beschaeftigungsUmfangValue: BarChartYAxisData = {
      label: "Beschäftigungsumfang",
      value: parseFloat((+dtoEntry.umfang.wert).toFixed(2)) / 100
    };

    data = data.map((entry: IBarChartDataEntry) => {
      if (moment(entry.xAxis.value).isSame(date, 'month')) {
        entry.yAxis.push(planUmfangValue);
        entry.yAxis.push(beschaeftigungsUmfangValue);
      }
      return entry;
    })
  });

  return data;
}

function createEmptyDataEntries(range: IBarChartDataRange): BarChartData {
  let startDate = moment(range.min);
  let endDate = moment(range.max);

  let allDates: Date[] = [];
  allDates.push(startDate.toDate());

  let month = moment(startDate);
  while (month < endDate) {
    month.add(1, "month");
    allDates.push(month.toDate());
  }

  let data: BarChartData = [];

  for (let date of allDates) {
    let entry: IBarChartDataEntry = {
      xAxis: date,
      yAxis: []
    };
    data.push(entry);
  }

  return data;
}

export function beispieleBarChartTransformation1(dto: BeispieleBarChartDTO, range: IBeispielBarChartRange): BarChartData {
  if (dto.entries.length === 0) {
    return [];
  }

  let data: BarChartData = [];

  let startDate = moment(range.min).startOf('month');
  let endDate = moment(range.max).startOf('month');

  let allDates: Date[] = [];
  allDates.push(startDate.toDate());

  let month = moment(startDate);
  while (month < endDate) {
    month.add(1, "month");
    allDates.push(month.toDate());
  }

  for (let date of allDates) {
    let entry: IBarChartDataEntry = {
      xAxis: date,
      yAxis: [
        {
          label: "Katekorie 1",
          value: Math.random() * 100
        },
        {
          label: "Kategorie 2",
          value: Math.random() * 100
        },
        {
          label: "Kategorie 3",
          value: Math.random() * 100
        }
      ]
    };
    data.push(entry);
  }
  return data;
}

function alphabetPosition(letter: string): number {
  return letter.charCodeAt(0) - 97;
}

export function beispieleBarChartTransformation2(dto: BeispieleBarChartDTO, range?: IBarChartDataRange): BarChartData {
  let data: BarChartData = [];

  // compute alphabet
  let allLetters: string[] = getAllLettersInRange(range);

  for (let letter of allLetters) {
    let entry: IBarChartDataEntry = {
      xAxis: letter,
      yAxis: [
        {
          label: "Foo",
          value: Math.random() * 5
        },
        {
          label: "Bar",
          value: Math.random() * 5
        }
      ]
    };
    data.push(entry);
  }
  return data;
}

/**
 * Returns an array containing exactly 1 date object element for each month that fits into the given range.
 * @param range
 */
export function getAllMonthsInRange(range: IBeispielBarChartRange): Date[] {
  let startDate = moment(range.min).startOf('month');
  let endDate = moment(range.max).startOf('month');

  let allDates: Date[] = [];
  allDates.push(startDate.toDate());

  let month = moment(startDate);
  while (month < endDate) {
    month.add(1, "month");
    allDates.push(month.toDate());
  }

  return allDates;
}

/**
 * Returns an array containing exactly 1 date object element for each month that fits into the given range.
 * @param range
 */
export function getAllLettersInRange(range: IBarChartDataRange): string[] {
  // compute alphabet
  let allLetters: string[] = [];
  const first = range !== undefined ? alphabetPosition(range.min) : 0;
  const last = range !== undefined ? alphabetPosition(range.max) : 25;
  for (let i = first; i <= last; i++) {
    allLetters.push(String.fromCharCode(97 + i));
  }
  return allLetters
}

export function transformFinanzierungsJahreDTO(dto: any): IBarChartDataRange {
  return {
    min: moment().set('year', dto.startjahr).startOf('year').toDate(),
    max: moment().set('year', dto.abschlussjahr).endOf('year').toDate()
  };
}
