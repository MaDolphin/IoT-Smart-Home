import { AfterViewInit, Component, OnInit, QueryList, ViewChildren } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleChartsGenComponentTOP } from '@targetgui/beispiele-charts-gen.component/beispiele-charts-gen.component-top';
import { BarChartComponent } from "@components/charts/bar-chart/bar-chart.component";
import { IDTO } from "@shared/architecture";
import { BeispieleBarChart_getByIdAndYear } from "@commands/beispielebarchart_getbyidandyear";
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
  @ViewChildren(BarChartComponent) barchart: QueryList<BarChartComponent>;

  ngAfterViewInit(): void {
    // date range changed
    this.barchart.first.updateEvent.subscribe( (value) => {
      let date = JSON.parse(value);
      const dateRange: string[] = [date.start, date.end];

      this.commandManager.addCommand(new BeispieleBarChart_getByIdAndYear(this.id, dateRange),
        (dto: IDTO) => {
          if (dto instanceof BeispieleBarChartDTO) {
            this.fz = dto;

            setTimeout( () => {
              this.barchart.first.updateChart();
            }, 100);
          } else {
            console.error("Received wrong dto type");
          }
        });

      this.commandManager.sendCommands();
    });
  }
  //endregion

}
