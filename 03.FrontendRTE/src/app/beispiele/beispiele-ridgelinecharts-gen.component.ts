/* (c) https://github.com/MontiCore/monticore */

import { Component, OnInit, ViewChild } from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleBarChart_getById } from "@commands/beispielebarchart.getbyid";
import { IDTO } from "@shared/architecture";
import { BeispieleBarChartDTO } from "@targetdtos/beispielebarchart.dto";
import * as moment from "moment";
import { beispielTransformation, transformDatumsbereichDTO } from "@components/charts/time-line-chart/time-line-chart.transformation";
import { ILineChartDataRange, TimeLineChartComponent } from "@components/charts/time-line-chart/time-line-chart.component";
import { RidgelineChartComponent } from "@components/charts/ridgeline-chart/ridgeline-chart.component";
import { BeispieleRidgelinechartsGenComponentTOP } from "@targetgui/beispiele-ridgelinecharts-gen.component/beispiele-ridgelinecharts-gen.component-top";

/**
 * See BeispielePieChartDTO.java, BeispielePieChartDTOLoader.java for more details on how to use PieCharts
 */
@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-ridgelinecharts-gen.component/beispiele-ridgelinecharts-gen.component.html',
})
export class BeispieleRidgelinechartsGenComponent extends BeispieleRidgelinechartsGenComponentTOP implements OnInit {

  constructor(
    _router: Router,
    _route: ActivatedRoute,
    _commandRestService: CommandRestService,
  ) {
    super(_commandRestService, _route, _router);
  }

/*
  //region TimeLine Chart
  transformFnTimeLineChart_fz3 = beispielTransformation;
  rangeTransformFnTimeLineChart_fz3 = transformDatumsbereichDTO;
  shownRangeTimeLineChart_fz3 = {
    "min": moment(new Date()).startOf('year').toDate(),
    "max": moment(new Date()).endOf('year').toDate()
  };
*/
  @ViewChild(RidgelineChartComponent) timeLine: TimeLineChartComponent;

  ngOnInit(): void {
    super.ngOnInit();
/*
    this.timeLine.updateEvent.subscribe((range: ILineChartDataRange) => {
      // const dateRange: string[] = ['' + range.min.getFullYear(), '' + range.max.getFullYear()];

      this.commandManager.addCommand(new BeispieleBarChart_getById(this.id),
        (dto: IDTO) => {
          if (dto instanceof BeispieleBarChartDTO) {
            this.fz3 = dto;
          } else {
            console.error("Received something wrong");
          }
        });

      this.commandManager.sendCommands();
    });*/
  }

  //endregion

}
