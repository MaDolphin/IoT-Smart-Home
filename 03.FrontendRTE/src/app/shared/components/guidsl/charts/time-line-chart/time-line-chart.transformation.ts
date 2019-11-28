import {
  ILineChartDataRange,
  LineChartData, TimeLineChartComponent
} from "@shared/components/guidsl/charts/time-line-chart/time-line-chart.component";
import * as moment from "moment";
import { BeispieleJahreDTO } from "@targetdtos/beispielejahre.dto";

export function transformBeispieleJahreDTO(dto: BeispieleJahreDTO): ILineChartDataRange {
  return TimeLineChartComponent.correctRange({
    min: moment().set('year', dto.startjahr).startOf('year').toDate(),
    max: moment().set('year', dto.abschlussjahr).endOf('year').toDate()
  });
}

/**
 * In this method, a DTO should be transformed into the data format of the timeline chart.
 * Attention: In this example an object is directly output in the correct format without transformation of a DTO.
 * @param dto DTO received from backend to transform into a LineChartData object
 * @param range Range that can be used for transformation. Even if this is not used, only data in the range will be displayed.
 */
export function beispielTransformation(dto: any, range?: ILineChartDataRange): LineChartData {
  return [
    {
      label: "Kategorie 1",
      lines: [
        {
          start: new Date("2019-01"),
          end: new Date("2019-12-15")
        }
      ]
    },
    {
      label: "Kategorie 2",
      lines: [
        {
          start: new Date("2019-01"),
          end: new Date("2019-05-15")
        }
      ]
    },
    {
      label: "Kategorie 3",
      lines: [
        {
          start: new Date("2019-01"),
          end: new Date("2019-03-31")
        },
        {
          start: new Date("2019-05"),
          end: new Date("2020-06-31")
        }
      ]
    }
  ];
}
