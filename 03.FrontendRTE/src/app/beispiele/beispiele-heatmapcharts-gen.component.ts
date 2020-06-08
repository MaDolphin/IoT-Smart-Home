/* (c) https://github.com/MontiCore/monticore */
import {Component} from '@angular/core';
import {WebSocketService} from '@shared/architecture/services/websocket.service';
import {ActivatedRoute, Router} from '@angular/router';
import {CommandRestService} from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleHeatmapchartsGenComponentTOP } from "@targetgui/beispiele-heatmapcharts-gen.component/beispiele-heatmapcharts-gen.component-top";
import { LineDataGroup } from "@components/charts/line-chart/line-chart.component";
import { BeispieleLineChartDTO } from "@targetdtos/beispielelinechart.dto";
import { NgxChartsModule } from '@swimlane/ngx-charts';

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
    public data;
    public motionsensordata;
    private getMyOwnData() {
      return this.single;
    }
    public subscribedataSocket(): void {
      if (this.dataSocket) {
        this.subscriptions.push(this.dataSocket.subscribe(message => {this.motionsensordata = this.getMyOwnData();}, err => console.error(err)));
      }
      else {
        console.error('Socket is not initialized. Initialize socket in the component constructor');
      }

    }
    //Data input in shape of the MotionSensor Class
    public single = 
    [
      {
        "name": "Sensor Bad",
        "timestamp": 1590544020
      },
      {
        "name": "Sensor Bad",
        "timestamp": 1590550200
      },
      {
        "name": "Sensor Bad",
        "timestamp": 1590568200
      },
      {
        "name": "Sensor Bad",
        "timestamp": 1590570360
      },
      {
        "name": "Sensor Bad",
        "timestamp": 1590575280
      },
      {
        "name": "Sensor Garageneinfahrt",
        "timestamp": 1590588780
      },
      {
        "name": "Sensor Garageneinfahrt",
        "timestamp": 1590598380
      },
      {
        "name": "Sensor Garageneinfahrt",
        "timestamp": 1590600000
      },
      {
        "name": "Sensor Garageneinfahrt",
        "timestamp": 1590609660
      },
      {
        "name": "Sensor Garageneinfahrt",
        "timestamp": 1590610560
      },
      {
        "name": "Störung",
        "timestamp": 1590544920
      },
      {
        "name": "Störung",
        "timestamp": 1590546360
      },
      {
        "name": "Störung",
        "timestamp": 1590547020
      },
      {
        "name": "Störung",
        "timestamp": 1590547200
      },
      {
        "name": "Störung",
        "timestamp": 1590545760
      },
      {
        "name": "Störung",
        "timestamp": 1590547500
      },
      {
        "name": "Störung",
        "timestamp": 1590548040
      },
      {
        "name": "Störung",
        "timestamp": 1590545280
      },
      {
        "name": "Störung",
        "timestamp": 1590545820
      },
      {
        "name": "Störung",
        "timestamp": 1590545880
      },
      {
        "name": "Störung",
        "timestamp": 1590545880
      },
      {
        "name": "Sensor Haustür",
        "timestamp": 1590563580
      },
      {
        "name": "Sensor Haustür",
        "timestamp": 1590580920
      },
      {
        "name": "Sensor Haustür",
        "timestamp": 1590581220
      },
      {
        "name": "Sensor Haustür",
        "timestamp": 1590586800
      },
      {
        "name": "Sensor Haustür",
        "timestamp": 1590590820
      },
      {
        "name": "Sensor Haustür",
        "timestamp": 1590610260
      },
      {
        "name": "Sensor Haustür",
        "timestamp": 1590622080
      },
      {
        "name": "Sensor Haustür",
        "timestamp": 1590623100
      }
    ];
  
  


}
