import { Component, OnInit, Input, ViewChild } from '@angular/core';
import * as paper from 'paper';
import { Config } from 'protractor';


/**
 * This class is a configuration class saving important configuration values
 * needed to transform and plot the data of the Ridgelines.
 */
class Ridgeline_Config {
  // ridges
  ridges_offset: number; // == offset horizontal // describes the size ot the space between two subsequent ridges
  ridges_height: number; // describes the maximum height, which one ridge can use. This space can overlap with subsequent ones

  /// values for grid
  grid_line_start_x: number;
  grid_line_end_x: number;
  grid_line_start_y: number;
  
  font_size: string;

  // values for x axis of data plots
  x_axis_start: number;
  x_axis_width: number;
  x_axis_value_offset: number; // The value offset between two x-axis descriptions (e.g. 5 --> 5,10,15,...)
                          // TODO: set this value automatically dependent on data range
  y_axis_start: number;

  /**
   * TODO Rename this function with a better suited name
   * @param all_data 
   * @param canvas_width 
   * @param canvas_height 
   * @param overshoot     how far do one ridge overlaps into another one (in percentage)
   */
  public set_params(
    labels : string[],
    data_rows : number, 
    data_x_range: number,
    canvas_width: number, 
    canvas_height: number, 
    grid_line_end_x: number, 
    font_size: string,
    overshoot: number)
  {
                                    // divide by everything which has to fit into the canvas space
                                    // i.e. the number of ridges (dat_rows), the space which might be needed for the overshoot
                                    // at the top and the bottom (2*overshoot/100), and the additional space for negative values
                                    // needed at the bottom (+1)
                                    // At the moment this covers the axis-description implicitly
                          // Substract some space which is used by y-axis-legend
    this.ridges_offset = (canvas_height - 10) / (data_rows + 2 * overshoot/100 + 1); // Space after which the next ridge begins
    this.ridges_height = (canvas_height - 10) / (data_rows + 2 * overshoot/100 + 1) * (1 + overshoot/100); 

    //Compute line start from font size and biggest label
    //Text scale calculation -------------------------------------------------
    let longest_label = labels.reduce(function(prev, current) { return (prev.length > current.length) ? prev : current; } );
    let temp_text = new paper.PointText(new paper.Point(0, 0));
    temp_text.justification = 'left';
    temp_text.fillColor = new paper.Color(0.0, 0.0, 0.0);
    temp_text.content = longest_label;
    temp_text.fontSize = font_size;
    let longest_width = temp_text.strokeBounds.width;
    temp_text.remove();
    //Text scale calculation end -------------------------------------------------
    this.grid_line_start_x = longest_width + 10;

    this.grid_line_end_x = grid_line_end_x;
    this.grid_line_start_y = this.ridges_height;

    this.font_size = font_size;

    this.x_axis_start = this.grid_line_start_x;
    this.x_axis_width = canvas_width - this.x_axis_start;
    
    //Set x-axis value offset when drawing legend from min to max x value (vertical line every offset)
    this.x_axis_value_offset = data_x_range / canvas_width * 80; //Every 80px    
    //Round this value to at most two decimal places
    this.x_axis_value_offset = Math.round((this.x_axis_value_offset + Number.EPSILON) * 100) / 100;

    this.y_axis_start = this.ridges_height;
  }
}






export class Data
{
  private values : number[][][]; //Underlying data (list of 2D data)
  private transformed_values : number[][][]; //Transformed data (for drawing)

  public x_range: number; //Range in x of data (range of min to max value)
  public y_range: number; //Range in y of data (range of min to max value)
  public max_y_range_from_0: number; //Range in y of data (range of 0 to absolute maximum of min and max)
                          // so it equals either y_max or -y_min
  public xy_min_max: number[][]; //min and max value in x and y dimension of data values
  public x_start_value : number; //Smallest x value in data

  //Value for color gradient - can be accessed by getter for each data row index
  private transformed_color_gradients : [string, number][][] = [];

  //Convenience values
  public length: number;

  //Values for updating data - store updated data until it gets merged with old data, check if old data needs to be transformed as well
  private update_values : number[][][];
  private update_old_data : boolean;

  /**
   * For all given ridge-lines this function sorts the points in each ridge-line
   * by the x-coordinate (rising)
   * @param data_array The data of all ridge-lines
   */
  private sort_by_x(data_array : number[][][])
  {
    data_array.forEach(
      (data) =>
      {
        data.sort(
          (point_1 : number[], point_2 : number[]) =>
          {
            if (point_1[0] === point_2[0])
            {
              return 0;
            }
            else if (point_1[0] < point_2[0])
            {
              return -1;
            }
            else
            {
              return 1;
            }
          }
        );
      }
    );
  }

/**
 * Returns the minimal an maximal values out of all points of all ridge-lines
 * @param data_array  The data of all ridge-lines
 * @returns           [[min_x, min_y], [max_x, max_y]]
 */
  private get_xy_min_max(data_array : number[][][])
  {
    //Store as [[min_x, min_y],[max_x, max_y]]
    let result : number[][] = [[0,0],[0,0]];

    for (let data of data_array)
    {
      let min = this.get_min_x_y(data);
      let max = this.get_max_x_y(data);

      //Store as [[min_x, min_y],[max_x, max_y]]
      result[0][0] = Math.min(result[0][0], min[0]);
      result[0][1] = Math.min(result[0][1], min[1]);
      result[1][0] = Math.max(result[1][0], max[0]);
      result[1][1] = Math.max(result[1][1], max[1]);
    }

    return result;
  }

  /**
   * Returns the minimal values of x and y over all x- and y-values in data
   * @returns [min_x, min_y]
   */
  private get_min_x_y(data: number[][])
  {
    return data.reduce(
      function(previous_value: number[], current_value: number[])
      {
        return [Math.min(previous_value[0], current_value[0]), Math.min(previous_value[1], current_value[1])];
      }
    );
  }

  /**
   * Returns the maximum values of x and y over all x- and y-values in data
   * @returns [max_x, max_y]
   */
  private get_max_x_y(data: number[][])
  {
    return data.reduce(
      function(previous_value: number[], current_value: number[])
      {
        return [Math.max(previous_value[0], current_value[0]), Math.max(previous_value[1], current_value[1])];
      }
    );
  }


  /**
   * This function sets up all variables of this class dependent on the data.
   * @param data The data of all ridge-line charts.
   */
  public setup(data: number[][][]){
    //Store data
    this.values = data;
    this.length = this.values.length;

    //Sort data by x for drawing
    this.sort_by_x(this.values);

    //Get relevant data information
    this.xy_min_max = this.get_xy_min_max(this.values);
    this.x_range = Math.abs(this.xy_min_max[0][0] - this.xy_min_max[1][0]);
    this.y_range = Math.abs(this.xy_min_max[0][1] - this.xy_min_max[1][1]);
    this.max_y_range_from_0 = Math.max(Math.abs(this.xy_min_max[0][1]), Math.abs(this.xy_min_max[1][1]));
    this.x_start_value = this.xy_min_max[0][0];
    this.update_old_data = false; //True only if data is updated and xy_min_max changes
  }

  /**
   * This function updates all variables of this class dependent on the newly added data, and stores it for merging later upon data transform
   * @param new_data The data to add to all ridge-line charts.
   */
  public update(new_data: number[][][]){
    if (new_data.length > 0)
    {
      //Store data
      this.update_values = new_data;
      if (new_data.length > this.values.length)
      {
        this.length = new_data.length;
      }

      //Sort data by x for drawing
      this.sort_by_x(this.update_values);

      //Update all relevant data information
      let updated_xy_min_max = this.get_xy_min_max(this.update_values);
      //Check if the transformation will have to change - in that case, old data needs to be transformed again as well
      if (updated_xy_min_max[0][0] < this.xy_min_max[0][0]
        || updated_xy_min_max[0][1] < this.xy_min_max[0][1]
        || updated_xy_min_max[1][0] > this.xy_min_max[1][0]
        || updated_xy_min_max[1][1] > this.xy_min_max[1][1]
      )
      {
        this.update_old_data = true;
      }

      //First update xy_min_max if required
      this.xy_min_max[0][0] = Math.min(this.xy_min_max[0][0], updated_xy_min_max[0][0]);
      this.xy_min_max[0][1] = Math.min(this.xy_min_max[0][1], updated_xy_min_max[0][1]);
      this.xy_min_max[1][0] = Math.max(this.xy_min_max[1][0], updated_xy_min_max[1][0]);
      this.xy_min_max[1][1] = Math.max(this.xy_min_max[1][1], updated_xy_min_max[1][1]);

      //Update other data information based on updated xy_min_max
      this.x_range = Math.abs(this.xy_min_max[0][0] - this.xy_min_max[1][0]);
      this.y_range = Math.abs(this.xy_min_max[0][1] - this.xy_min_max[1][1]);
      this.max_y_range_from_0 = Math.max(Math.abs(this.xy_min_max[0][1]), Math.abs(this.xy_min_max[1][1]));
      this.x_start_value = this.xy_min_max[0][0];
    }
  }


  public get_transformed_data_row(index : number)
  {
    if (this.transformed_values.length > index && index >= 0)
    {
      return this.transformed_values[index];
    }
    else
    {
      //TODO: Show error
      return [];
    }
  }

  public get_transformed_color_gradients(index : number)
  {
    if (this.transformed_color_gradients.length > index && index >= 0)
    {
      return this.transformed_color_gradients[index];
    }
    else
    {
      //TODO: Show error
      return [];
    }
  }

  /**
   * Transform data to start at or after x_start value regarding the (same) coordinate system,
   * scale the coordinate system to x_range/y_range over a given pixel width/height, start at
   * some (x,y)-pixel using translate_x/_y
   * Also invert y value for drawing
   */
  public transform_data(config : Ridgeline_Config, color_gradients : [string, number][])
  {
    //Make a copy of values before transformation, to keep values unchanged
    this.transformed_values = [];
    this.transformed_color_gradients = [];

    // sort color_gradients
    color_gradients = this.sort_by_y_gradient(color_gradients);

    //Transform data to start at center point (translate_x, translate_y) and resize to desired width and height, and invert y coordinate
    //Also, for a uniform x scale, make it start s.t. it regards the smallest actual x_start_value
    //Range in context with width/height gives a scale factor for the data, so all data have a uniform x and y scale in the end
    let width_scale = config.x_axis_width / this.x_range;
    let height_scale = config.ridges_height / this.max_y_range_from_0;
    for (let i = 0; i < this.values.length; ++i)
    {
      let y_from = config.y_axis_start + config.ridges_offset * i;
      this.transformed_values[i] = this.values[i].map(
        (item: number[]) =>
        {
          return [(item[0] - this.x_start_value) * width_scale + config.x_axis_start, -(item[1] * height_scale) + y_from];
        }
      );

      //Transform color gradient values
      this.transformed_color_gradients.push([]);
      let max_height = window.innerHeight;
      let min_height = 0;
      for (let j = 0; j < color_gradients.length; ++j)
      {
        //Cap gradient y value s.t. we do not get an overflow
        let color_y_value = 0.0;
        if (color_gradients[j][1] > 300000000)
        {
          color_y_value = 300000000;
        }
        else if (color_gradients[j][1] < -300000000)
        {
          color_y_value = -300000000;
        }
        else
        {
          color_y_value = color_gradients[j][1];
        }

        //Also cap height values that do not make sense
        let color_pixel_height_y = -color_y_value * height_scale + y_from;
        if (color_pixel_height_y > max_height)
        {
          color_pixel_height_y = max_height;
        }
        if (color_pixel_height_y < min_height)
        {
          color_pixel_height_y = min_height;
        }

        this.transformed_color_gradients[i].push([color_gradients[j][0], color_pixel_height_y ]);
      }
    }
  }

  /**
   * For performance reasons: Perform transformation on added / old data only where necessary
   * Transform data to start at or after x_start value regarding the (same) coordinate system,
   * scale the coordinate system to x_range/y_range over a given pixel width/height, start at
   * some (x,y)-pixel using translate_x/_y
   * Also invert y value for drawing
   */
  public transfrom_and_migrate_updated_data(config : Ridgeline_Config, color_gradients : [string, number][])
  {
    if (this.update_values.length > 0)
    {
      //Merge with values
      for (let i = 0; i < this.update_values.length; ++i)
      {
        //Merge from point where x data starts in new data, else add a new entry if updated data has more rows
        if (i < this.values.length)
        {
          if (this.update_values[i].length > 0)
          {
            //Delete every value in this.values (old data) with an x value higher or equal to the added data's x values
            let lowest_x = this.update_values[i][0][0];
            let obsolete_index = this.values[i].findIndex((element) => element[0] >= lowest_x);
            if (obsolete_index >= 0)
            {
              this.values[i].length = obsolete_index;
            }

            //Then add new data
            for (let j = 0; j < this.update_values[i].length; ++j)
            {
              this.values[i].push(this.update_values[i][j]);
            }
          }
        }
        else
        {
          this.values[i] = this.update_values[i];
        }
      }

      //If old data needs to be updated as well, just transform everything again
      if (this.update_old_data)
      {
        this.transform_data(config, color_gradients);
        this.update_old_data = false;
        this.update_values = [];
      }
      else
      {
        //Just transform new data, then add it to transformed old data
        let transformed_new_data : number[][][] = [];
        //Transform data to start at center point (translate_x, translate_y) and resize to desired width and height, and invert y coordinate
        //Also, for a uniform x scale, make it start s.t. it regards the smallest actual x_start_value
        //Range in context with width/height gives a scale factor for the data, so all data have a uniform x and y scale in the end
        let width_scale = config.x_axis_width / this.x_range;
        let height_scale = config.ridges_height / this.max_y_range_from_0;
        for (let i = 0; i < this.update_values.length; ++i)
        {
          let y_from = config.y_axis_start + config.ridges_offset * i;
          transformed_new_data[i] = this.update_values[i].map(
            (item: number[]) =>
            {
              return [(item[0] - this.x_start_value) * width_scale + config.x_axis_start, -(item[1] * height_scale) + y_from];
            }
          );
        }

        //Then just merge with updated values
        for (let i = 0; i < transformed_new_data.length; ++i)
        {
          //Merge from point where x data starts in new data, else add a new entry if updated data has more rows
          if (i < this.transformed_values.length)
          {
            if (transformed_new_data[i].length > 0)
            {
              //Delete every value in this.transformed values (old data) with an x value higher or equal to the added data's x values
              let lowest_x = transformed_new_data[i][0][0];
              let obsolete_index = this.transformed_values[i].findIndex((element) => element[0] >= lowest_x);
              if (obsolete_index >= 0)
              {
                this.transformed_values[i].length = obsolete_index;
              }

              //Then add new data
              for (let j = 0; j < transformed_new_data[i].length; ++j)
              {
                this.transformed_values[i].push(transformed_new_data[i][j]);
              }
            }
          }
          else
          {
            this.transformed_values[i] = transformed_new_data[i];
          }
        }
      }
    }
  }

  private sort_by_y_gradient(gradients : [string, number][])
  {
    gradients.sort(
      (gradient_1 : [string, number], gradient_2 : [string, number]) =>
      {
        if (gradient_1[1] === gradient_2[1])
        {
          return 0;
        }
        else if (gradient_1[1] < gradient_2[1])
        {
          return -1;
        }
        else
        {
          return 1;
        }
      }
    );
    return gradients;
  }
}


@Component({
  selector: 'ridgeline-chart',
  templateUrl: './ridgeline-chart.component.html',
  styleUrls: ['./ridgeline-chart.component.scss'],
})
export class RidgelineChartComponent implements OnInit {
  @Input() smooth: boolean;
  @Input() overshoot: number;
  @Input() labels: string[]; // The labels of the ridges
  @Input() font_size: string;
  @Input() color_gradients: [string, number][]; //Color gradients: [y_value, color_string]
  @Input() overwrite_data: boolean; //If true, overwrite data on set, else just update/merge it with old data; also has an influence on further data processing
  _rawData: number[][][] = [];
  
  //Input function and value for max y value of ridgeline color gradient
  // private color_gradient_max_y : number = 0.3; //Should match default value in HTML
  // enter_gradient_max_y(value: string)
  // {
  //   this.color_gradient_max_y = parseFloat(value);
  // }

  //private canvas_div: HTMLDivElement;
  private canvas_container: HTMLDivElement;
  private canvas: HTMLCanvasElement;
  private ctx: CanvasRenderingContext2D;
  private path: paper.Path;

  //Ridgeline data and configuration
  private data : Data = new Data();
  private config: Ridgeline_Config = new Ridgeline_Config();
  private had_data_before: boolean = false; //Remember if data has ever been set before (for first-time setup)

  /*
  @Input() 
  public set smooth(smooth: boolean){
    console.log("Smoothed set to: "+smooth);
  }*/

  @Input()
  public set rawData(rawData: number[][][]){
    this.had_data_before = this._rawData.length > 0;
    this._rawData = rawData;

    //Change behaviour depending on whether we only add data and want to perform our analysis only on that, or whether we actually want to reset the previously set data and overwrite everything
    //Also: First-time behaviour (if no data was set previously) is the same
    if (this.overwrite_data || !this.had_data_before)
    {
      this.data.setup(rawData);
    }
    else
    {
      //This is less CPU-expensive - we set the data block that needs to be updated, which can change e.g. the x-range of our data
      this.data.update(rawData);
    }

    //Transformation of the (new) data now depends e.g. on the size on the canvas and thus is done within UI functions
  }


  public hasData(): boolean{
    return this._rawData.length > 0;
  }


  public ngOnInit(): void {
  }

  private adjust_size()
  {
    //Set div size to viewport width part depending on current window size
    //With about 500px window width, we only have half of the screen left, at 2000px we have about 90 percent
    let viewport_width_proportion = (0.2/1000 * window.innerWidth + 0.36) * 100;
    if (viewport_width_proportion > 90)
    {
      viewport_width_proportion = 90;
    }
    let canvas_container_width = "width: " + viewport_width_proportion + "vw";
    this.canvas_container.setAttribute("style", canvas_container_width);

    //Width is set through CSS property / HTML "resize" property because we cannot always use window.innerWidth as orientation
    //paper.view.viewSize.width = this.canvas.width;
    this.canvas.width = this.canvas_container.clientWidth - 10;
    this.canvas.height = this.canvas_container.clientHeight - 10;
    paper.view.viewSize.width = this.canvas_container.clientWidth - 10;
    paper.view.viewSize.height = this.canvas_container.clientHeight - 10;
  }


  ngAfterViewInit() {    
    this.canvas_container = document.getElementById('canvas_container') as HTMLDivElement;
    this.canvas = document.getElementById('canvas') as HTMLCanvasElement;
    this.canvas.setAttribute("resize", "true");
    this.canvas.setAttribute("width", "100%");
    this.canvas.setAttribute("height", "100%");
    this.ctx = this.canvas.getContext('2d');

    // Set up paperjs with the given canvas
    paper.setup(this.canvas);

    let height = 5 * 100; // Choose an arbitrary value which might fit to the number of ridges

    this.adjust_size();

    window.addEventListener('resize', this.adjust_size, false);

    //Paperjs framerate: 60fps
    //We do not need to update the view that often, 3fps should be enough to see real-time data
    let frame_count = 0;
    paper.view.onFrame = (event) => {
      //Only show every 10th frame
      if (frame_count % 20 != 0)
      {
        ++frame_count;
        return;
      }
      frame_count = 1;

      //Update or set data if required
      if (this.overwrite_data || !this.had_data_before)
      {
        this.config.set_params(this.labels, this.data.length, this.data.x_range, this.canvas.width, this.canvas.height, this.canvas.width, this.font_size, this.overshoot);
        this.data.transform_data(this.config, this.color_gradients);
      }
      else
      {
        //Thus, we need to update the configuration again
        this.config.set_params(this.labels, this.data.length, this.data.x_range, this.canvas.width, this.canvas.height, this.canvas.width, this.font_size, this.overshoot);
        //After that, we just need to transform the updated data and have to merge it with the already existing data
        this.data.transfrom_and_migrate_updated_data(this.config, this.color_gradients);
      }

      // Should not be called too often
      if (this.hasData()){
        paper.project.clear();

        //Draw grid for data
        this.plot_grid(this.labels, this.data, this.config);

        //TODO: Proper arguments for grid drawing

        //Draw test data
        for (let i = 0; i < this.data.length; ++i)
        {
          // ToDo: further refinement of config object and function call to avoid so many parameters
          this.plot_ridge(this.data.get_transformed_data_row(i), this.data.get_transformed_color_gradients(i), this.config, i);
        }
      }

      // Lines to see some parameters in real life
      // let line = new paper.Path.Line(new paper.Point(0, 0), new paper.Point(this.canvas.width, this.canvas.height));
      // line.strokeColor = new paper.Color(0.2, 0.2, 0.2, 0.2);
      // let line2 = new paper.Path.Line(new paper.Point(0, 0), new paper.Point(0, this.config.y_axis_start));
      // line2.strokeColor = new paper.Color(0.2, 0.2, 0.2, 0.2);
      

    }
  }

  private plot_grid(labels: string[], data: Data, config: Ridgeline_Config)
  {
    //Values for later
    let line_start_x = config.grid_line_start_x;
    let line_end_x = config.grid_line_end_x;
    let line_start_y = config.grid_line_start_y;

    let offset_horizontal = config.ridges_offset;

    let xy_min_max = data.xy_min_max;

    let x_axis_start = config.x_axis_start;
    let x_axis_width = config.x_axis_width;
    let x_value_offset = config.x_axis_value_offset;

    let font_size = config.font_size;

    //Horizontal lines and labels
    for (let i = 0; i < labels.length; ++i)
    {
      let line = new paper.Path.Line(new paper.Point(line_start_x, line_start_y + offset_horizontal*i), new paper.Point(line_end_x, line_start_y + offset_horizontal*i));
      line.strokeColor = new paper.Color(0.2, 0.2, 0.2, 0.2);

      //Put text left of line
      let text = new paper.PointText(new paper.Point(0, line_start_y + offset_horizontal*i));
      text.justification = 'left';
      text.fillColor = new paper.Color(0.0, 0.0, 0.0);
      text.content = labels[i];
      text.fontSize = font_size;
      //text.scale(text_scale, new paper.Point(0, line_start_y + offset_horizontal*i));
    }

    //Vertical lines and labels (TODO)
    let y_bottom_line_end = line_start_y + offset_horizontal * (labels.length - 1) + config.ridges_height;
                            // Consider only length-1 offsets and instead for the last ridge the whole height (offset+overshoot)

    for (let x = xy_min_max[0][0]; x < xy_min_max[1][0]; x += x_value_offset)
    {
      //Round x - TODO: Does of course not work depending on the x axis (scale)
      x = Math.round((x + Number.EPSILON) * 100) / 100;

      let x_point = (x - xy_min_max[0][0]) / (xy_min_max[1][0] - xy_min_max[0][0]) * x_axis_width + x_axis_start;

      let line = new paper.Path.Line(new paper.Point(x_point, line_start_y - config.ridges_height),
                                     new paper.Point(x_point, y_bottom_line_end));
      line.strokeColor = new paper.Color(0.2, 0.2, 0.2, 0.2);

      //Put text below line
      let text = new paper.PointText(new paper.Point(x_point, y_bottom_line_end + 10));
      text.justification = 'center';
      text.fillColor = new paper.Color(0.0, 0.0, 0.0);
      text.content = '' + x.toFixed(2); //Only show last two decimals - TODO: Change depending on used scale
      text.fontSize = font_size;
      //text.scale(text_scale);
    }
  }

  private plot_ridge(data_row: number[][], gradient_max_y_values: [string, number][], config: Ridgeline_Config, index: number)
  {
    let y_from = this.config.y_axis_start + this.config.ridges_offset * index;

    //console.log(data_row);

    //Only draw a plot if there's actually any data
    if (data_row.length == 0)
    {
      return;
    }

    let path = new paper.Path({
      segments: [new paper.Point(data_row[0][0], data_row[0][1])],
      strokeColor: 'black',
      strokeWidth: '1',
    });

    for (let i = 1; i < data_row.length; ++i)
    {
      path.lineTo(new paper.Point(data_row[i][0], data_row[i][1]));
    }

    //path.smooth(); -> Leads to problems if we have straight lines in between (line might not stay straight then)

    let path_filler = new paper.Path({
      segments: [new paper.Point(data_row[0][0], data_row[0][1])],
      strokeColor: new paper.Color(0.0, 0.0, 0.0, 0.0),
      strokeWidth: '2',
    });

    for (let i = 1; i < data_row.length; ++i)
    {
      path_filler.lineTo(new paper.Point(data_row[i][0], data_row[i][1]));
    }
    //path_filler.smooth();
    path_filler.lineTo(new paper.Point(data_row[data_row.length - 1][0], y_from));
    path_filler.lineTo(new paper.Point(data_row[0][0], y_from));




    if (gradient_max_y_values.length < 2)
    {
      path_filler.fillColor = new paper.Color(0.1, 0.4, 1.0, 0.5);
    }
    else
    {
      // color_gradients are sorted (see transform_data) before they are converted to canvas-y-data. Thus, we find the
      // max y at index 0 and the min y at the last index
      let gradient_max = gradient_max_y_values[0][1];
      let gradient_min = gradient_max_y_values[gradient_max_y_values.length-1][1];
      let gradient_range = Math.abs(gradient_max-gradient_min);
      let gradient_percent = [];

      // Dependent on the gradient min and max data (in canvas units) compute the percentage (0-1) of all gradients
      // where they are between these min and max values
      for (let i=0; i<gradient_max_y_values.length; ++i){
        //Cap percentage value (too low values do not make sense here)
        let y_percentage = 1 - (gradient_max_y_values[i][1]-gradient_min)/gradient_range; // 1 - ... because lower pixel values correspond to higher parts
        if (y_percentage < 0.0001)
        {
          y_percentage = 0.0001;
        }

        gradient_percent.push([gradient_max_y_values[i][0], y_percentage])
      }

      path_filler.fillColor = new paper.Color({
        gradient: {
          stops: gradient_percent
        },
        origin: [data_row[0][0], gradient_max],
        destination: [data_row[0][0], gradient_min]
      });
    }
  }
}
