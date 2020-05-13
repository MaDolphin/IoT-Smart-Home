import { Component, OnInit, ViewChild } from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleBarChart_getById } from "@commands/beispielebarchart.getbyid";
import { IDTO } from "@shared/architecture";
import { BeispieleBarChartDTO } from "@targetdtos/beispielebarchart.dto";
import * as moment from "moment";
import { beispielTransformation, transformDatumsbereichDTO } from "@components/charts/time-line-chart/time-line-chart.transformation";
import { ILineChartDataRange, TimeLineChartComponent } from "@components/charts/time-line-chart/time-line-chart.component";
import { DensityChartComponent } from "@components/charts/density-chart/density-chart.component";
import { BeispieleDensitychartsGenComponentTOP } from "@targetgui/beispiele-densitychart-gen.component/beispiele-densitychart-gen.component-top";

/**
 * See BeispielePieChartDTO.java, BeispielePieChartDTOLoader.java for more details on how to use PieCharts
 */
@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-densitycharts-gen.component/beispiele-densitycharts-gen.component.html',
})
export class BeispieleDensitychartsGenComponent extends BeispieleDensitychartsGenComponentTOP implements OnInit {

  constructor(
    _router: Router,
    _route: ActivatedRoute,
    _commandRestService: CommandRestService,
  ) {
    super(_commandRestService, _route, _router);
  }

  @ViewChild(DensityChartComponent) timeLine: TimeLineChartComponent;

  ngOnInit(): void {
    super.ngOnInit();

  }


}
