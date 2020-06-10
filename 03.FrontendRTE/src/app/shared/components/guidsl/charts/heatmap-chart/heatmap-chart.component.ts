/* (c) https://github.com/MontiCore/monticore */
import {Component, HostBinding, Input, ViewChild, OnChanges, SimpleChanges} from '@angular/core';
import { HeatMapComponent } from '@swimlane/ngx-charts/release/heat-map';
import { interval } from 'rxjs';

// A Wrapper for ngx-charts-heat-map

@Component({
  selector: 'heatmap-chart',
  templateUrl: './heatmap-chart.component.html',
})
export class HeatmapChartComponent implements OnChanges {
  //Now we only need data and labels, to create a heatmap-chart
  @Input() data;
  @Input() XLabel;
  @Input() YLabel;

  //Redistribute timestamps if data changes
  ngOnChanges(changes: SimpleChanges) {
    if(this.data) this.computed_data = this.rearrange_array(this.data.entries);
  }
  //Set minimum and maximum Column numbers
  private min_num_seperations = 1;
  private max_num_seperations = 32;

  computed_data;
  //Pre-set some ngx-charts properties (See ngx-charts documentation for details)
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = true;
  showXAxisLabel = false;
  showYAxisLabel = false;
  colorScheme = {
    domain: ['#A3CAEC', '#00549F']
  };
  //Initial number of columns, same as html
  num_seperations : number = 6;
  //If new column number is selected, clip to extremes and recalculate chart
  enter_num_seperations(value:number)
  {
    if(value<this.min_num_seperations) {this.num_seperations = this.min_num_seperations;}
    else if(value > this.max_num_seperations){this.num_seperations = this.max_num_seperations;}
    else {this.num_seperations = value;}
    if(this.data) this.computed_data = this.rearrange_array(this.data.entries);    
  }
  //Checkbox attributes
  prettyprint_timestamps = true;
  loop_days = false;
  //Refresh chart if changed
  enter_pretty_date(value)
  {
    this.prettyprint_timestamps = value;
    if(this.data) this.computed_data = this.rearrange_array(this.data.entries);  
  }
  enter_loop_days(value)
  {
    this.loop_days = value;
    if(this.data) this.computed_data = this.rearrange_array(this.data.entries);  
  }

  //Creates an [{"name",series:["name","value"]}] array from an [{"name", "timestamp"}] array by splitting the time into num_seperators equal periods and couting amount of entries for each period & sensor name
  rearrange_array(somedata) {
    
    //Earliest timestamp
    var min = somedata[0].timestamp;
    //Latest timestamp
    var max = somedata[0].timestamp;
    //List of Sensor Names
    var names = [];
    //Calculate the three values
    for (var i_data of somedata) {
      if(min>i_data.timestamp){min=i_data.timestamp;}
      if(max<i_data.timestamp){max=i_data.timestamp;}
      names.indexOf(i_data.name) === -1 ? names.push(i_data.name) : undefined;
    };
    //Increse maximum by one, so highest element is below the max (otherwise it is not counted)
    max++;
    if(this.loop_days)
    {
      min = 0;
      max = 86400;
    }

    //Period between earliest and latest timestamp 
    var diff;
    diff = max-min;
    //Period divided into num_sperations equal chunks
    var step = diff/this.num_seperations;
    //Define shape of outer ouput array
    let computed_d: {name:string, series:any[]}[] = [];
    
    //For each chunk
    for (var j = 0; j < this.num_seperations; j++)
    {
      //Array for counting occurence of "sensor_name" in time-chunk
      var counts = [];
      //Give it same number of entries as list of sensor names
      for (var name in names) {
        counts.push(0);
      }

      //Define shape of inner array
      let temp_series: {name: string,value:number}[] = [];

      //Count the occurences in this chunk
      for (var entry of somedata) {
        if((this.loop_days) && ((min + j*step) <= (entry.timestamp % 86400) && (entry.timestamp % 86400) < (min + (j+1)*step))){
          counts[names.indexOf(entry.name)]++;
        }
        else if((min + j*step) <= entry.timestamp && entry.timestamp < (min + (j+1)*step)){
          counts[names.indexOf(entry.name)]++;
        }
      }

      //Fill inner array
      for (var count in counts) {
        temp_series.push({name:names[count],value:counts[count]});
      }
      //Create String for time-chunk
      var step_name = Math.floor(min+j*step) + "-" + Math.floor(min+(j+1)*step);
      if(this.prettyprint_timestamps) step_name = this.timeToString(min+j*step, diff) + "-" + this.timeToString(min+(j+1)*step, diff);
      //Fill outer array
      computed_d.push({name:step_name,series:temp_series});
    }

    return computed_d;
  };


  //Create String from Timestamps
  timeToString(timestamp, difference){
    // Create a new JavaScript Date object based on the timestamp
    // multiplied by 1000 so that the argument is in milliseconds, not seconds.
    var date = new Date((timestamp) * 1000);
    
    // Hours part from the timestamp
    var hours = date.getUTCHours();
    // Minutes part from the timestamp
    var minutes = "0" + date.getUTCMinutes();
    var datum = date.getUTCDate();
    var month = date.getUTCMonth();
    var year = date.getUTCFullYear();

    if(difference>86400)
    {
      return hours + ':' + minutes.substr(-2) + ' ' + datum + '/' + month + '/' + year;
    }
    else{
      // Will display time in 10:23 format
      return hours + ':' + minutes.substr(-2);  
      //return timestamp;	
    }
  };

}
