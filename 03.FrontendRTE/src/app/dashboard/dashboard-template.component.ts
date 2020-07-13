import {AfterViewInit, OnDestroy, OnInit, Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CommandRestService} from '@shared/architecture/command/rte/command.rest.service';
import {DashboardTemplateComponentTOP} from '@targetgui/dashboard-template.component/dashboard-template.component-top';
import { TemperatureDataDTO } from '@temperaturedata-dto/temperaturedata.dto';
import { MotionSensorDataDTO } from '@motionsensordata-dto/motionsensordata.dto';

@Component({
  templateUrl: '../../../target/generated-sources/gui/dashboard-template.component/dashboard-template.component.html'
})
export class DashboardTemplateComponent extends DashboardTemplateComponentTOP implements OnInit, OnDestroy, AfterViewInit {

  public constructor(
    protected _commandRestService: CommandRestService,
    protected _route: ActivatedRoute,
    protected _router: Router
  ) {
    super(_commandRestService, _route, _router);
  }

  public sensorType;
  public motionsensordata: MotionSensorDataDTO;
  public temperaturedata: TemperatureDataDTO;

  //Data structures for currently set ridgeline chart
  public data_ridgeline = [];
  public labels_ridgeline : string[] = [];
  public color_gradients_ridgeline : [string, number][] = [];
  public ridgeline_x_align;

  public ngOnInit(): void {
    super.ngOnInit();
    this.initDropDown();
  }

  private initDropDown() {
    this._sensorTypeControl.setOptions([
      'Temperature',
      'Movement'
    ]);
    this._sensorTypeControl.defaultValue = 'Temperature';
    this.initMotionSensorDataDTOmotionsensordata();
    this.initTemperatureDataDTOtemperaturedata();
    this.sendCommands();
    this.sensorType = "temperature";

    this._sensorTypeControl.valueChanges.subscribe(v => {
      if (v === 'Temperature') {
        console.log('temperature');
        // get data for temperature sensor, update your chart, something like
        // this.initTemperatureDataDTOtemperaturedata();
        // this.sendCommands();
        this.initTemperatureDataDTOtemperaturedata();
        this.sendCommands();
        this.sensorType = "temperature";

      } else {
        console.log('movement');
        // get data for movement sensor, ...
        this.initMotionSensorDataDTOmotionsensordata();
        this.sendCommands();
        this.sensorType = "movement";
      }
    })
  }

  public initTemperatureDataDTOtemperaturedata(): void {
    TemperatureDataDTO.getAll(this.commandManager)
      .then((model: TemperatureDataDTO) => {
        this.temperaturedata = model;

        //***************************************************
        //Process data for ridgeline
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
        this.ridgeline_x_align = 60000000;
        //this.color_gradients_ridgeline = [["#29b6f6", 0], ["#66bb6a", 18], ["#ff7043", 30]];
        this.color_gradients_ridgeline = [["#6699ff", 0], ["#66bb6a",15], /*["#ffff66", 18],*/ ["#ff7043", 25], ["#cc0000",/*30*/60]];
        //***************************************************
      });
  }

  public initMotionSensorDataDTOmotionsensordata(): void {
    MotionSensorDataDTO.getAll(this.commandManager)
      .then((model: MotionSensorDataDTO) => {
        this.motionsensordata = model;

        //***************************************************
        //Process data for ridgeline
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

        this.data_ridgeline = new_ridgeline_data;
        this.labels_ridgeline = new_ridgeline_labels;
        this.ridgeline_x_align = 10000;
        this.color_gradients_ridgeline = [["#29b6f6", 0], ["#66bb6a", 0.9], ["#ff7043", 1]];
        //***************************************************
      });
  }

}


