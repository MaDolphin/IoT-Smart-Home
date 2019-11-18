import { AfterViewInit, Component, OnInit, QueryList, ViewChildren } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleChartsGenComponentTOP } from '@targetgui/beispiele-charts-gen.component/beispiele-charts-gen.component-top';
import { BarChartComponent, IBarChartDataRange } from "@components/charts/bar-chart/bar-chart.component";
import { beispieleBarChartTransformation1, beispieleBarChartTransformation2 } from "@components/charts/bar-chart/bar-chart.transformation";
import { BeispieleBarChart_getById } from "@commands/beispielebarchart.getbyid";
import { IDTO } from "@shared/architecture";
import { BeispieleBarChartDTO } from "@targetdtos/beispielebarchart.dto";


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
  public beispieleBarChartDataRange1: IBarChartDataRange = {
    min: new Date("2018-01"), // e.g. YYYY-MM
    max: new Date("2018-12"),
  };

  // public beispieleBarChartDataRange2: IBarChartDataRange = {
  //   min: "b", // first day of 2018
  //   max: "y", // last day of 2020
  // };

  @ViewChildren(BarChartComponent) barchart: QueryList<BarChartComponent>;

  ngAfterViewInit(): void {
    this.barchart.first.dataTransformation = beispieleBarChartTransformation1;
    this.barchart.first.sortFn = (d1: Date, d2: Date) => d1.getTime() - d2.getTime();
    this.barchart.first.dataRange = this.beispieleBarChartDataRange1;

    // this.barchart.first.dataTransformation = beispieleBarChartTransformation2;
    // this.barchart.first.sortFn = (s1: string, s2: string) => s1.localeCompare(s2, 'de');
    // this.barchart.first.dataRange = this.beispieleBarChartDataRange2; // date range is optional for this example
    // this.barchart.first.yAxisType = "STUNDE";
    // this.barchart.first.stacked = true;

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
  }
  //endregion

}
