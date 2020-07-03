/* (c) https://github.com/MontiCore/monticore */

import { Component, OnInit } from "@angular/core";
import { DashboardExampleRlHmComponentTOP} from "@targetgui/dashboard-ExampleRlHm.component/dashboard-ExampleRlHm.component-top";
import { ActivatedRoute, Router } from "@angular/router";
import { CommandRestService } from "@shared/architecture/command/rte/command.rest.service";
import { WebSocketService } from "@services/websocket.service";
import { RightOutSelDirective } from "ng5-slider/slider.component";
import { TemperatureDataDTO } from '@temperaturedata-dto/temperaturedata.dto';

@Component({
  templateUrl: '../../../target/generated-sources/gui/dashboard-ExampleRlHm.component/dashboard-ExampleRlHm.component.html',
})

export class DashboardExampleRlHmComponent extends DashboardExampleRlHmComponentTOP implements OnInit {
  protected socket;

  InColor1 = "#ffa500";
  InColNum1 = 20;
  InColor2 = "#eeeeee";
  InColNum2 = 12;

  public constructor(
    _router: Router,
    _route: ActivatedRoute,
    _commandRestService: CommandRestService,
    protected socketService: WebSocketService) {
    super(_commandRestService, _route, _router);

    this.socket = socketService.open('None', ['Info']);
  }

  public ngOnInit(): void {
    super.ngOnInit();

    this.socket.subscribe(message => {
        console.log('received a message: ', message.data);
      }, err =>
        console.error(err)
    );
    this.socket.next("Hello World!");
  }

  //Data structures for ridgeline chart
  public data_ridgeline = [];
  public labels_ridgeline : string[] = [];
  public color_gradients_ridgeline : [string, number][] = [["#29b6f6", 0], ["#66bb6a", 18], ["#ff7043", 30]];

  public initTemperatureDataDTOtemperaturedata(): void {
    TemperatureDataDTO.getAll(this.commandManager)
      .then((model: TemperatureDataDTO) => {
        this.temperaturedata = model;

        let new_ridgeline_data = [];
        let new_ridgeline_labels = [];

        for (let entry of model.entries)
        {
          let ridge_index = new_ridgeline_labels.findIndex(label => label === entry.name);
          //Ridge already exist, add to ridge
          if (ridge_index >= 0)
          {
            new_ridgeline_data[ridge_index].push([entry.timestamp * 1000, entry.value]); //*1000 to translate to ms
          }
          else
          {
            //Else: Create new ridge
            new_ridgeline_labels.push(entry.name);
            new_ridgeline_data.push([[entry.timestamp * 1000, entry.value]]);
          }
        }

        this.data_ridgeline = new_ridgeline_data;
        this.labels_ridgeline = new_ridgeline_labels;

      });
  }
}
