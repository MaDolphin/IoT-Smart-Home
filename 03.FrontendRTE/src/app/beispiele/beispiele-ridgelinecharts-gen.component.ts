/* (c) https://github.com/MontiCore/monticore */

import { Component, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { ILineChartDataRange, TimeLineChartComponent } from "@components/charts/time-line-chart/time-line-chart.component";
import { RidgelineChartComponent } from "@components/charts/ridgeline-chart/ridgeline-chart.component";
import { BeispieleRidgelinechartsGenComponentTOP } from "@targetgui/beispiele-ridgelinecharts-gen.component/beispiele-ridgelinecharts-gen.component-top";
import { WebSocketService } from '@shared/architecture/services/websocket.service';


import { TypedJSON } from '@upe/typedjson';
import { RidgelineChartDataDTO } from '@ridgelinechartdata-dto/ridgelinechartdata.dto';

/**
 * See BeispielePieChartDTO.java, BeispielePieChartDTOLoader.java for more details on how to use PieCharts
 */
@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-ridgelinecharts-gen.component/beispiele-ridgelinecharts-gen.component.html',
})
export class BeispieleRidgelinechartsGenComponent extends BeispieleRidgelinechartsGenComponentTOP implements OnInit {

  constructor(
    protected _router: Router,
    protected _route: ActivatedRoute,
    protected _commandRestService: CommandRestService,
    protected _webSocketService: WebSocketService,
  ) {
    super(_commandRestService, _route, _router, _webSocketService);
  }

  public data_static = []; // The data which is actually given to the static diagram
  public labels_static = [];

  public data_dynamic = []; // The data which is actually given to the dynamic diagram
  public labels_dynamic = [];


  private dummyData; // helping variable
  private lastSeconds = 0;
  private dummyDataIndex = 0;

  private realtimeTest : boolean = true;

  private useDynamicBackendData: boolean = false;


  // -------------------------------- Variables for color gradient configuration --------------------------------
  //Color gradient data_static structure which can be used to set gradients / color stops to color specific y values of the visualization
  public color_gradients_static : [string, number][] = [["#e91e63", 0], ["#2196f3", 1], ["#2196f3", 3]];
  public color_gradients_dynamic : [string, number][] = [];

  //Color picker functions
  public show_color_picker = false;
  public selected_color = '#ffffff';

  //Input field to get input value
  private color_stop_y_input: HTMLInputElement;


  // ---------------------------------------------- Init Functions ----------------------------------------------

  ngOnInit(): void {
    super.ngOnInit();
    this.dummyData = this.createDummyData(); // To avoid that this is done every time again
  }

  ngAfterViewInit() {  
    super.ngAfterViewInit();  
    this.color_stop_y_input = document.getElementById('color_stop_y_input') as HTMLInputElement;
  }



  // ----------------------------------------- Color Gradient Utilities -----------------------------------------

  /**
   * Blend in / out color picker
   */
  public toggle_color_picker(event)
  {
    //Don't refresh the page
    event.preventDefault();

    this.show_color_picker = !this.show_color_picker;
  }

  /** 
   * Callback function for input keydown
   */
  public ignore_keydown(event)
  {
    //Don't refresh the page
    event.preventDefault();
  }

  /**
   * Callback function for input to change color
   */
  public change_color(event, value: string)
  {
    //Don't refresh the page
    event.preventDefault();

    let valid_hex = /^#[0-9A-F]{6}$/i.test(value);
    if(valid_hex)
    {
      this.selected_color = value;
    }
  }

  /**
   * Callback function for input to enter color stop
   */
  public add_color_stop(event)
  {
    //Don't refresh the page
    event.preventDefault();
    console.log("Add color");

    //Get color gradient y value
    let value : string = this.color_stop_y_input.value;
    let stop = parseFloat(value);

    let res = [...this.color_gradients_dynamic]; // Create a copy of the current gradients

    //Add new color gradient section only if the value does not yet exist
    if (res.find(element => element[1] == stop) == undefined)
    {
      res.push([ this.selected_color, stop ]);
    }
    
    // Overwrite the old value; only pushing to the old wouldn't force Angular to send
    // the new one to the ridgeline-chart.component
    this.color_gradients_dynamic = res;
    console.log(this.color_gradients_dynamic);
  }

  /**
   * Callback function for button to reset all previous color stop input
   */
  public reset_color_stop(event)
  {
    //Don't refresh the page
    event.preventDefault();
    
    this.color_gradients_dynamic = [];
  }
  
  
// ---------------------------------------------- data Management ----------------------------------------------
  


  public subscribechartData1Socket(): void {
    if (this.chartData1Socket) {
      this.subscriptions.push(this.chartData1Socket.subscribe(message => {
        this.data_dynamic = this.getData(message);
        //this.color_gradients_dynamic = [["#b3e5fc", 0]];
        //console.log(this.data_static);
      }, err =>
        console.log(err)
      ));
    } else {
      console.error('Socket is not initialized. Initialize socket in the component constructor');
    }
  }

  //REST 'all'-getter that sets static data
  public initRidgelineChartDataDTOchartData(): void {
    RidgelineChartDataDTO.getAll(this.commandManager)
      .then((model: RidgelineChartDataDTO) => {
        this.chartData = model;
        let data_transformed = this.transformDTO(this.chartData);
        this.data_static = data_transformed[0];
        this.labels_static = data_transformed[1];
        console.log(this.data_static);
        console.log(this.labels_static);
        console.log(this.data_static.length);
        //Just for presentation
        // this.data_static = [];
        // for (let j = 0; j < 2; ++j)
        // {
        //   this.data_static[j] = [];
        //   for (let i = -2; i < 15; i+=0.25)
        //   {
        //     this.data_static[j].push([i * 360000, Math.sin((i + j) / 2.0) * Math.random() * 20]);
        //   }
        // }
        // this.labels_static = ["Income Mon", "Income Tue"];
      });
  }
  
  public getData(message){
    if (this.useDynamicBackendData){ // Currently sent via REST (all) and not via websocket
      // Transforme data_static to that which is necessary here
      // let data_static = this.transformDTO(this.chartData);
      // this.labels_static = data_static[1];
      // return data_static[0];
      console.log("TODO: Add socket getData part again!");

    } else {
      // NOTE: ONLY FOR TESTING (QUICK AND DIRTY)
      if (!this.realtimeTest){
        return this.dummyData;
      }

      // Realtime test
      const currentSeconds = Date.now() / 1000; // seconds since epoch
      if (currentSeconds > this.lastSeconds + 0.1){
        this.dummyDataIndex += 1;
        this.lastSeconds = currentSeconds;
      }
      var res = [];
      for (var i=0; i<this.dummyData.length; i++){
        if (i==3){ // For live testing leave the fourth component out
          continue;
        }
        res.push(this.dummyData[i].slice(0,Math.min(this.dummyData[i].length, this.dummyDataIndex)));
      }
      
      this.labels_dynamic = ['test1', 'test2', 'test3', 'random sinus'];

      return res;
    }
  }

  /**
   * Transforms data_static
   * @param input a RidgelineChartDataDTO object
   * @return a tuple consisting of the values (number[][][]) and the y-labels_static (string[])
   */
  public transformDTO(input: RidgelineChartDataDTO){
    let values_result = [];
    let labels_result = [];
    for (const ridgelineDataDTO of input.ridgelines){
      labels_result.push(ridgelineDataDTO.label);

      let ridge = [];
      for (const entry of ridgelineDataDTO.entries){
        ridge.push([entry.x, entry.y]);
      }
      values_result.push(ridge);
    }
    return [values_result, labels_result];
  }


  /**
   * Creates the data_static for all ridgelines
   * @returns number[][][]
   */
  public createDummyData(){
    let test_data_1 = this.get_test_data(-15, 15, 0.25);
    let test_data_2 = this.get_test_data(-10, 20, 0.25);
    let test_data_3 = this.get_test_data(-5, 20, 0.25);
    let test_data_4 = [[19,0.1], [-5, 0.1], [24, 0.05], [-7, -0.1]];
    let test_data_5 : number[][] = [];

    for (let i = -2; i < 15; i+=0.25)
    {
      test_data_5.push([i, Math.sin(i / 2.0) * Math.random()]);
    }
    let all_data = [test_data_1, test_data_2, test_data_3, test_data_4, test_data_5];
    
    return all_data;
  }
  


  //Creates function data_static array from x_1 to x_2, in step_size steps, with one of two random functions
  private get_test_data(x_1: number, x_2: number, step_size: number)
  {
    let test_data : number[][] = [];

    let sigma = Math.random() * 5;
    let mu = Math.random() * 5;
    let frac_1 = 1 / Math.sqrt(2 * Math.PI * Math.pow(sigma, 2));
    let frac_2 = - 1 / (2 * Math.pow(sigma, 2));
    
    for (let x = x_1; x < x_2; x += step_size)
    {
      test_data.push([x, frac_1 * Math.exp(frac_2 * Math.pow(x - mu, 2))]);
    }

    return test_data;
  }
}
