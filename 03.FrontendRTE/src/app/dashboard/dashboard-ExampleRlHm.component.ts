/* (c) https://github.com/MontiCore/monticore */

import { Component, OnInit } from "@angular/core";
import { DashboardExampleRlHmComponentTOP} from "@targetgui/dashboard-ExampleRlHm.component/dashboard-ExampleRlHm.component-top";
import { ActivatedRoute, Router } from "@angular/router";
import { CommandRestService } from "@shared/architecture/command/rte/command.rest.service";
import { WebSocketService } from "@services/websocket.service";
import { RightOutSelDirective } from "ng5-slider/slider.component";
import { TemperatureDataDTO } from '@temperaturedata-dto/temperaturedata.dto';
import { MotionSensorDataDTO } from '@motionsensordata-dto/motionsensordata.dto';
import { DensitySensorDataDTO } from '@densitysensordata-dto/densitysensordata.dto';

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

  /**
   * This field of sensors is used by the selection drop-down menu to select a certain sensor to display in the graph
   */
  public sensors : string[] = ["Temperature", "Motion"];

  /**
   * Reaction function for change of sensor type
   * @param event Event triggered by change in input field
   */
  public on_ridgeline_sensor_change(event)
  {
    if (event.value == "Temperature")
    {
      let res = [...this.data_ridgeline_temp]; // Create a copy of the current gradients
      this.data_ridgeline = res;
      this.labels_ridgeline = this.labels_ridgeline_temp;
      this.color_gradients_ridgeline = [["#29b6f6", 0], ["#66bb6a", 18], ["#ff7043", 30]];
    }
    else if (event.value == "Motion")
    {
      let res = [...this.data_ridgeline_motion]; // Create a copy of the current gradients
      this.data_ridgeline = res;
      this.labels_ridgeline = this.labels_ridgeline_motion;
      this.color_gradients_ridgeline = [["#29b6f6", 0], ["#66bb6a", 0.9], ["#ff7043", 1]];
    }
  }

  //Data structures for currently set ridgeline chart
  public data_ridgeline = [];
  public labels_ridgeline : string[] = [];
  public color_gradients_ridgeline : [string, number][] = [];
  //Data structures for precomputed ridgeline chart data
  public data_ridgeline_temp = [];
  public labels_ridgeline_temp : string[] = [];
  public data_ridgeline_motion = [];
  public labels_ridgeline_motion : string[] = [];
  public data_ridgeline_density = [];
  public labels_ridgeline_density : string[] = [];

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

        this.data_ridgeline_temp = new_ridgeline_data;
        this.labels_ridgeline_temp = new_ridgeline_labels;

      });
  }

  public initMotionSensorDataDTOmotionsensordata(): void {
    MotionSensorDataDTO.getAll(this.commandManager)
      .then((model: MotionSensorDataDTO) => {
        let new_ridgeline_data = [];
        let new_ridgeline_labels = [];

        for (let entry of model.entries)
        {
          let ridge_index = new_ridgeline_labels.findIndex(label => label === entry.name);
          //Ridge already exist, add to ridge - 1 for sensor detect, 0 in between
          if (ridge_index >= 0)
          {
            new_ridgeline_data[ridge_index].push([(entry.timestamp * 1000) - 1, 0]); //*1000 to translate to ms
            new_ridgeline_data[ridge_index].push([entry.timestamp * 1000, 1]); //*1000 to translate to ms
            new_ridgeline_data[ridge_index].push([(entry.timestamp * 1000) + 1, 0]); //*1000 to translate to ms
          }
          else
          {
            //Else: Create new ridge
            new_ridgeline_labels.push(entry.name);
            new_ridgeline_data.push([[(entry.timestamp * 1000) - 1, 0], [entry.timestamp * 1000, 1], [(entry.timestamp * 1000) + 1, 0]]);
          }
        }

        this.data_ridgeline_motion = new_ridgeline_data;
        this.labels_ridgeline_motion = new_ridgeline_labels;

      });
  }
  
  public initDensitySensorDataDTOdensitysensordata(): void {
    DensitySensorDataDTO.getAll(this.commandManager)
      .then((model: DensitySensorDataDTO) => {
        this.densitysensordata = model;
        //TODO
      });
  }
}
