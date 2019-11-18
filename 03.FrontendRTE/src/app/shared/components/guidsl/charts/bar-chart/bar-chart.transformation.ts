import { BeispieleBarChartDTO } from "@targetdtos/beispielebarchart.dto";
import { BarChartData, IBarChartDataEntry, IBarChartDataRange } from "@components/charts/bar-chart/bar-chart.component";

import * as moment from 'moment';

interface IBeispielBarChartRange {
  min: Date,
  max: Date
}

export function beispieleBarChartTransformation1(dto: BeispieleBarChartDTO, range: IBeispielBarChartRange): BarChartData {
  if (dto.entries.length === 0) {
    return [];
  }

  let data: BarChartData = [];

  let startDate = moment(range.min);
  let endDate = moment(range.max);

  let allDates: Date[] = [];
  allDates.push(startDate.toDate());

  let month = moment(startDate);
  while (month < endDate) {
    month.add(1, "month");
    allDates.push(month.toDate());
  }

  for (let date of allDates) {
    let entry: IBarChartDataEntry = {
      xAxisValue: {
        label: moment(date).locale('de').format("MMM YYYY"),
        value: date
      },
      yAxisValues: [
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

export function beispieleBarChartTransformation2(dto: BeispieleBarChartDTO, range: IBarChartDataRange): BarChartData {
  let data: BarChartData = [];

  // compute alphabet
  let allLetters: string[] = [];
  for (let i = alphabetPosition(range.min); i <= alphabetPosition(range.max); i++) {
    allLetters.push(String.fromCharCode(97 + i));
  }

  for (let letter of allLetters) {
    let entry: IBarChartDataEntry = {
      xAxisValue: {
        label: letter,
        value: letter
      },
      yAxisValues: [
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
