/* (c) https://github.com/MontiCore/monticore */
import {Component} from '@angular/core';
import {WebSocketService} from '@shared/architecture/services/websocket.service';
import {ActivatedRoute, Router} from '@angular/router';
import {CommandRestService} from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleHeatmapchartsGenComponentTOP } from "@targetgui/beispiele-heatmapcharts-gen.component/beispiele-heatmapcharts-gen.component-top";
import { NgxChartsModule } from '@swimlane/ngx-charts';
import {MotionSensorDataDTO} from '@targetdtos/motionsensordata.dto';


@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-heatmapcharts-gen.component/beispiele-heatmapcharts-gen.component.html',
})
export class BeispieleHeatmapchartsGenComponent extends BeispieleHeatmapchartsGenComponentTOP {
  public constructor(
    protected _webSocketService: WebSocketService,
    protected _router: Router,
    protected _route: ActivatedRoute,
    protected _commandRestService: CommandRestService) {
    super(_commandRestService, _route, _router, _webSocketService);}
    // Gibt Beispieldaten für heatmap-chart
    title = 'Bewegungsmeldungen';
  
    //Hier werden die Achsenbeschriftungen festgelegt
    xAxisLabel = '';
    yAxisLabel = '';
}