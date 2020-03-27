/* (c) https://github.com/MontiCore/monticore */
import { AfterViewInit, Component, OnInit, QueryList, ViewChildren } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { BarChartComponent, IBarChartDataRange } from "@components/charts/bar-chart/bar-chart.component";
import {
  beispieleBarChartTransformation1,
  beispieleBarChartTransformation2,
  getAllLettersInRange,
  getAllMonthsInRange,
  transformFinanzierungsJahreDTO
} from "@components/charts/bar-chart/bar-chart.transformation";
import { BeispieleBarChart_getById } from "@commands/beispielebarchart.getbyid";
import { IDTO } from "@shared/architecture";
import { BeispieleBarChartDTO } from "@targetdtos/beispielebarchart.dto";
import * as moment from "moment";
import { transformBeispiel2 } from "@components/charts/time-line-chart/time-line-chart.transformation";
import { BeispieleBarchartsGenComponentTOP } from "@targetgui/beispiele-barcharts-gen.component/beispiele-barcharts-gen.component-top";

/**
 * See BeispielePieChartDTO.java, BeispielePieChartDTOLoader.java for more details on how to use PieCharts
 */
@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-barcharts-gen.component/beispiele-barcharts-gen.component.html',
})
export class BeispieleBarchartsGenComponent extends BeispieleBarchartsGenComponentTOP implements OnInit, AfterViewInit {

  constructor(
    _router: Router,
    _route: ActivatedRoute,
    _commandRestService: CommandRestService,
  ) {
    super(_commandRestService, _route, _router);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setNavigationBarLinks();
  }

//region Bar Chart
  // -- YOU COULD ALSO USE EXAMPLE 2 --

  // represents the maximum data range of the available data
  private currentYearRange: IBarChartDataRange = {
    min: moment(new Date()).startOf('year').toDate(),
    max: moment(new Date()).endOf('year').startOf('month').toDate(),
  };

  public beispieleBarChartDataRange: IBarChartDataRange = {
    min: "b",
    max: "y",
  };

  @ViewChildren(BarChartComponent) barchart: QueryList<BarChartComponent>;

  public barChartXAxisDataValueStringifyFn_fz = (date: Date) => {
    return moment(date).locale('de').format("MMM YYYY"); // e.g. 'Nov. 2019'
  };
  public getAllXAxisDataValuesFn_fz = getAllMonthsInRange;
  public rangeTransformation_fz = transformFinanzierungsJahreDTO;
  public dataTransformation_fz = beispieleBarChartTransformation1;
  public sortFn_fz = (d1: any, d2: any) => moment(d1).startOf('month').toDate().getTime() - moment(d2).startOf('month').toDate().getTime();
  public shownDataRange_fz = this.currentYearRange;

  public barChartXAxisDataValueStringifyFn_fz2 = (letter: string) => letter; // TOTO MF: Remove this
  public getAllXAxisDataValuesFn_fz2 = getAllLettersInRange;
  public dataTransformation_fz2 = beispieleBarChartTransformation2;
  public sortFn_fz2 = (s1: string, s2: string) => s1.localeCompare(s2, 'de');
  public rangeTransformation_fz2 = transformBeispiel2;


  // date range changed
  onUpdateBarChart_fz(range: IBarChartDataRange): void {
    // fetch data from server for current data range
    // this is only for demonstration purposes and not complete..
    // you should use a command like getByIDAndYear to get data within the specified range
    this.commandManager.addCommand(new BeispieleBarChart_getById(this.id), (dto: IDTO) => {
      if (dto instanceof BeispieleBarChartDTO) {
        this.fz = dto;
      } else {
        console.error("received wrong dto type");
      }
    });

    this.commandManager.sendCommands();
  }

  onUpdateBarChart_fz2(range: IBarChartDataRange): void {
    // fetch data from server for current data range
    // this is only for demonstration purposes and not complete..
    // you should use a command like getByIDAndYear to get data within the specified range
    this.commandManager.addCommand(new BeispieleBarChart_getById(this.id), (dto: IDTO) => {
      if (dto instanceof BeispieleBarChartDTO) {
        this.fz2 = dto;
      } else {
        console.error("received wrong dto type");
      }
    });

    this.commandManager.sendCommands();
  }

  ngAfterViewInit(): void {
    // additional customization
    this.barchart.last.yAxisType = "STUNDE";
    this.barchart.last.stacked = true;
  }
  //endregion

}
