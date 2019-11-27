import { AfterViewInit, Component, OnInit, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleChartsGenComponentTOP } from '@targetgui/beispiele-charts-gen.component/beispiele-charts-gen.component-top';
import { BarChartComponent, IBarChartDataRange } from "@components/charts/bar-chart/bar-chart.component";
import {
  beispieleBarChartTransformation1,
  beispieleBarChartTransformation2,
  beispieleBarChartTransformation3
} from "@components/charts/bar-chart/bar-chart.transformation";
import { BeispieleBarChart_getById } from "@commands/beispielebarchart.getbyid";
import { IDTO } from "@shared/architecture";
import { BeispieleBarChartDTO } from "@targetdtos/beispielebarchart.dto";
import * as moment from "moment";
import { beispielTransformation, transformBeispieleJahreDTO } from "@components/charts/time-line-chart/time-line-chart.transformation";
import { ILineChartDataRange, TimeLineChartComponent } from "@components/charts/time-line-chart/time-line-chart.component";
import { BeispieleTimeline_getByIdAndYear } from "@commands/beispieletimeline_getbyidandyear";

/**
 * See BeispielePieChartDTO.java, BeispielePieChartDTOLoader.java for more details on how to use PieCharts
 */
@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-charts-gen.component/beispiele-charts-gen.component.html',
})
export class BeispieleChartsGenComponent extends BeispieleChartsGenComponentTOP implements OnInit, AfterViewInit {

  constructor(
    _router: Router,
    _route: ActivatedRoute,
    _commandRestService: CommandRestService,
  ) {
    super(_router, _route, _commandRestService);
  }

  //region Bar Chart
  // -- YOU COULD ALSO USE EXAMPLE 2 --

  // represents the maximum data range of the available data
  private beispieleBarChartDataRange1: IBarChartDataRange = {
    min: new Date("2018-01"), // e.g. YYYY-MM
    max: new Date("2018-12"),
  };

  // private beispieleBarChartDataRange2: IBarChartDataRange = {
  //   min: "b", // first day of 2018
  //   max: "y", // last day of 2020
  // };

  @ViewChildren(BarChartComponent) barchart: QueryList<BarChartComponent>;

  ngAfterViewInit(): void {
    this.barchart.first.dataTransformation = beispieleBarChartTransformation1;
    this.barchart.first.sortFn = (d1: any, d2: any) => moment(d1).toDate().getTime() - moment(d2).toDate().getTime();
    this.barchart.first.dataRange = this.beispieleBarChartDataRange1;

    this.barchart.last.dataTransformation = beispieleBarChartTransformation2;
    this.barchart.last.sortFn = (s1: string, s2: string) => s1.localeCompare(s2, 'de');
    // this.barchart.last.dataRange = this.beispieleBarChartDataRange2; // date range is optional for this example
    this.barchart.last.yAxisType = "STUNDE";
    this.barchart.last.stacked = true;

    // date range changed
    this.barchart.first.updateEvent.subscribe( (range: IBarChartDataRange) => {
      // fetch data from server for current data range
      // this is only for demonstration purposes and not complete..
      // you should use a command like getByIDAndYear to get data within the specified range
      this.commandManager.addCommand(new BeispieleBarChart_getById(this.id), (dto: IDTO) => {
        if (dto instanceof BeispieleBarChartDTO) {
          this.fz = dto;
          setTimeout( () => {
            this.barchart.first.updateChart();
          }, 100);
        } else {
          console.error("received wrong dto type");
        }
      });

      this.commandManager.sendCommands();
    });

    this.barchart.last.updateEvent.subscribe( (range: IBarChartDataRange) => {
      // fetch data from server for current data range
      // this is only for demonstration purposes and not complete..
      // you should use a command like getByIDAndYear to get data within the specified range
      this.commandManager.addCommand(new BeispieleBarChart_getById(this.id), (dto: IDTO) => {
        if (dto instanceof BeispieleBarChartDTO) {
          this.fz2 = dto;
          setTimeout( () => {
            this.barchart.last.updateChart();
          }, 100);
        } else {
          console.error("received wrong dto type");
        }
      });

      this.commandManager.sendCommands();
    });
  }
  //endregion

  //region TimeLine Chart
  transformFnTimeLineChart_fz3 = beispielTransformation;
  rangeTransformFnTimeLineChart_fz3 = transformBeispieleJahreDTO;
  shownRangeTimeLineChart_fz3 = {
    "min": moment(new Date()).startOf('year').toDate(),
    "max": moment(new Date()).endOf('year').toDate()
  };

  @ViewChild(TimeLineChartComponent) timeLine: TimeLineChartComponent;

  ngOnInit(): void {
    super.ngOnInit();

    this.timeLine.updateEvent.subscribe((range: ILineChartDataRange) => {
      // const dateRange: string[] = ['' + range.min.getFullYear(), '' + range.max.getFullYear()];

      this.commandManager.addCommand(new BeispieleBarChart_getById(this.id),
        (dto: IDTO) => {
          if (dto instanceof BeispieleBarChartDTO) {
            this.fz3 = dto;

            setTimeout( () => {
              this.timeLine.updateChart();
            }, 100);
          } else {
            console.error("Received something wrong");
          }
        });

      this.commandManager.sendCommands();
    });
  }

  //endregion

}
