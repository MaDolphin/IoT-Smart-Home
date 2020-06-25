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
  @Input() timestampdata = false;

  //Redistribute timestamps if data changes
  ngOnChanges(changes: SimpleChanges) {
    if(this.data) this.computed_data = this.rearrange_array(this.data.entries);
    
    if(this.timestampdata){
      this.prettyprint_timestamps=true;
      this.loop_factor=86400;
    } 
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
    domain: ['#A3CAEC','#98BCDB','#8CAECB','#819FBA','#7591AA','#6A8399','#5E7588','#536778','#536778','#3C4A57','#303C46']
  };


  color_to_html_hex(redv, greenv, bluev)
  {
    return ('#'+('0'+Math.floor(redv).toString(16)).slice(-2)+('0'+Math.floor(greenv).toString(16)).slice(-2)+('0'+Math.floor(bluev).toString(16)).slice(-2));
  }

  colorscalefactor = 0.3;

  color_domain_mid_steps = 10;

  enter_color(value:string)
  {
    var color = parseInt(value.substr(1),16);
    var blue = (color % 256);
    var green = ((color-blue)/256)%256;
    var red = (color-blue-(256*green))/65536;
    if((blue+green+red)>381)
    {
      var secondary_red =red*this.colorscalefactor;
      var secondary_green =green*this.colorscalefactor;
      var secondary_blue =blue*this.colorscalefactor;
    }
    else
    {
      var secondary_red =255-(255-red)*this.colorscalefactor;
      var secondary_green =255-(255-green)*this.colorscalefactor;
      var secondary_blue =255-(255-blue)*this.colorscalefactor;
    }
    var new_domain = [];
    for(var i = 1; i<=this.color_domain_mid_steps;i++)
    {
      new_domain.push(this.color_to_html_hex(((this.color_domain_mid_steps-i)*secondary_red+i*red)/this.color_domain_mid_steps,((this.color_domain_mid_steps-i)*secondary_green+i*green)/this.color_domain_mid_steps,((this.color_domain_mid_steps-i)*secondary_blue+i*blue)/this.color_domain_mid_steps));
    }
    new_domain.push(value);
    this.colorScheme = {domain: new_domain};
  }


  //Initial number of columns, same as html
  num_seperations  = 6;

  loop_factor  = 10;
  //If new column number is selected, clip to extremes and recalculate chart
  enter_num_seperations(value:number)
  {
    if(value<this.min_num_seperations) {this.num_seperations = this.min_num_seperations;}
    else if(value > this.max_num_seperations){this.num_seperations = this.max_num_seperations;}
    else {this.num_seperations = value;}
    if(this.data) this.computed_data = this.rearrange_array(this.data.entries);    
  }
  //Checkbox attributes
  prettyprint_timestamps = false;
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
  enter_num_loop_factor(value)
  {
    if(value<2) this.loop_factor=2;
    else this.loop_factor=value;
    if(this.data) this.computed_data = this.rearrange_array(this.data.entries); 
  }

  //Creates an [{"name",series:["name","value"]}] array from an [{"name", "timestamp"}] array by splitting the time into num_seperators equal periods and couting amount of entries for each period & sensor name
  rearrange_array(somedata) {
    if(this.timestampdata)
    {
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
    }
    else
    {
      //Earliest timestamp
      var min = somedata[0].value;
      //Latest timestamp
      var max = somedata[0].value;
      //List of Sensor Names
      var names = [];
      //Calculate the three values
      for (var i_data of somedata) {
        if(min>i_data.value){min=i_data.value;}
        if(max<i_data.value){max=i_data.value;}
        names.indexOf(i_data.name) === -1 ? names.push(i_data.name) : undefined;
      };
    }
    
    //Increse maximum by one, so highest element is below the max (otherwise it is not counted)
    max++;
    if(this.loop_days)
    {
      min = 0;
      max = this.loop_factor;
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

      if(this.timestampdata)
      {
        //Count the occurences in this chunk
        for (var entry of somedata) {
          if((this.loop_days) && ((min + j*step) <= (entry.timestamp % this.loop_factor) && (entry.timestamp % this.loop_factor) < (min + (j+1)*step))){
            counts[names.indexOf(entry.name)]++;
          }
          else if((min + j*step) <= entry.timestamp && entry.timestamp < (min + (j+1)*step)){
            counts[names.indexOf(entry.name)]++;
          }
        }
      }
      else
      {
        //Count the occurences in this chunk
        for (var entry of somedata) {
          if((this.loop_days) && ((min + j*step) <= (entry.value % this.loop_factor) && (entry.value % this.loop_factor) < (min + (j+1)*step))){
            counts[names.indexOf(entry.name)]++;
          }
          else if((min + j*step) <= entry.value && entry.value < (min + (j+1)*step)){
            counts[names.indexOf(entry.name)]++;
          }
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
