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

  //Color gradient data structure which can be used to set gradients / color stops to color specific y values of the visualization
  public color_gradients : [number, string][] = [];
  //Callback function for button to enter color stop
  public enter_color_stop(event, value: string)
  {
    //Don't refresh the page
    event.preventDefault();

    let stop = parseFloat(value);
    let color_string = "0x";
    color_string += Math.floor(Math.random() * 10);
    color_string += Math.floor(Math.random() * 10);
    color_string += Math.floor(Math.random() * 10);
    color_string += Math.floor(Math.random() * 10);
    color_string += Math.floor(Math.random() * 10);
    color_string += Math.floor(Math.random() * 10);
    this.color_gradients.push([ stop, color_string ]);
  }
  
  private dummyData; // helping variable
  private lastSeconds = 0;
  private dummyDataIndex = 0;

  private realtimeTest : boolean = true;



  ngOnInit(): void {
    super.ngOnInit();
    this.dummyData = this.createDummyData(); // To avoid that this is done every time again
  }

  public subscribelineChartDataSocket(): void {
    if (this.lineChartDataSocket) {
      this.subscriptions.push(this.lineChartDataSocket.subscribe(message => {
        this.data = this.getData();
      }, err =>
        console.log(err)
      ));
    } else {
      console.error('Socket is not initialized. Initialize socket in the component constructor');
    }
  }


  // NOTE: ONLY FOR TESTING (QUICK AND DIRTY)
  public getData(){
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
