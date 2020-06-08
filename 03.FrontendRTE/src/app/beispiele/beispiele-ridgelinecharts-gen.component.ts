/* (c) https://github.com/MontiCore/monticore */

import { Component, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { ILineChartDataRange, TimeLineChartComponent } from "@components/charts/time-line-chart/time-line-chart.component";
import { RidgelineChartComponent } from "@components/charts/ridgeline-chart/ridgeline-chart.component";
import { BeispieleRidgelinechartsGenComponentTOP } from "@targetgui/beispiele-ridgelinecharts-gen.component/beispiele-ridgelinecharts-gen.component-top";
import { WebSocketService } from '@shared/architecture/services/websocket.service';

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

  public data = []; // The data which is actually given to the diagram
  public labels = ['test1', 'test2', 'test3', 'custom', 'random sinus'];


  private dummyData; // helping variable
  private lastSeconds = 0;
  private dummyDataIndex = 0;

  private realtimeTest : boolean = true;

  private useBackendData: boolean = false;



  ngOnInit(): void {
    super.ngOnInit();
    this.dummyData = this.createDummyData(); // To avoid that this is done every time again
  }

  ngAfterViewInit() {  
    super.ngAfterViewInit();  
    this.color_stop_y_input = document.getElementById('color_stop_y_input') as HTMLInputElement;
  }


  // -------------------------------- Variables for color gradient configuration --------------------------------
  //Color gradient data structure which can be used to set gradients / color stops to color specific y values of the visualization
  public color_gradients : [string, number][] = [];

  //Color picker functions
  public show_color_picker = false;
  public selected_color = '#ffffff';

  //Input field to get input value
  private color_stop_y_input: HTMLInputElement;


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

    //Get color gradient y value
    let value : string = this.color_stop_y_input.value;
    let stop = parseFloat(value);

    //Add new color gradient section only if the value does not yet exist
    if (this.color_gradients.find(element => element[1] == stop) == undefined)
    {
      this.color_gradients.push([ this.selected_color, stop ]);
    }
  }

  /**
   * Callback function for button to reset all previous color stop input
   */
  public reset_color_stop(event)
  {
    //Don't refresh the page
    event.preventDefault();
    
    this.color_gradients.length = 0;
  }
  
  
// ---------------------------------------------- Data Management ----------------------------------------------
  
  public subscribechartData1Socket(): void {
    if (this.chartData1Socket) {
      this.subscriptions.push(this.chartData1Socket.subscribe(message => {
        this.data = this.getData(message);
        
      }, err =>
        console.log(err)
      ));
    } else {
      console.error('Socket is not initialized. Initialize socket in the component constructor');
    }
  }


  
  public getData(message){
    if (this.useBackendData){
      console.log(message.data);
      // Transforme Data to that which is necessary here
      return [];

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
      
      this.labels = ['test1', 'test2', 'test3', 'random sinus'];

      return res;
    }
  }


  /**
   * Creates the data for all ridgelines
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
  


  //Creates function data array from x_1 to x_2, in step_size steps, with one of two random functions
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
