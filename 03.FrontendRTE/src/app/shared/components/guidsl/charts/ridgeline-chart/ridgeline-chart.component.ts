import { Component, OnInit, Input, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
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
  
  font_size: number;

  // values for x axis of data plots
  x_axis_start: number;
  x_axis_width: number;
  x_axis_value_offset: number; // The value offset between two x-axis descriptions (e.g. 5 --> 5,10,15,...)
                          // TODO: set this value automatically dependent on data range
  y_axis_start: number;

  //Detect resize event (window resize callback leads to strange behaviour, so we had to do this differently)
  previous_canvas_width : number = 0;
  previous_canvas_height : number = 0;
  size_was_changed : boolean = false;


  /**
   * This function has to be called in order to set all parameters.
   * @param labels          The labels of all ridges. Necessary in order to compute needed space on the left side of diagram
   * @param data_rows       The number of ridges.
   * @param canvas_width    
   * @param canvas_height 
   * @param grid_line_end_x x-Point on canvas at which the grid should end in x-direction on the right side
   * @param font_size       font size of axis description in pixel
   * @param overshoot       how far do one ridge overlaps into another one (in percentage)
   * @param number_of_lines_of_labels for how many lines of the x-axis labels should be space reserved
   *                                  (e.g. for the label "Line1\nLine2" this should be 2)
   * @param x_axis_value_offset which numerical distance should be between two x-axis-descriptions
   */
  public set_params(
    labels : string[],
    data_rows : number, 
    canvas_width: number, 
    canvas_height: number, 
    grid_line_end_x: number, 
    font_size: number,
    overshoot: number,
    number_of_lines_of_labels: number,
    x_axis_value_offset: number)
  {
    let abs_space_x_axis_legend_per_line = font_size + 2;
    let abs_space_x_axis_legend = abs_space_x_axis_legend_per_line * number_of_lines_of_labels;
                                    // divide by everything which has to fit into the canvas space
                                    // i.e. the number of ridges (dat_rows), the space which might be needed for the overshoot
                                    // at the top and the bottom (2*overshoot/100), and the additional space for negative values
                                    // needed at the bottom (+1)
                                    // At the moment this covers the axis-description implicitly
                          // Substract some space which is used by x-axis-legend
    this.ridges_offset = (canvas_height - abs_space_x_axis_legend) / (data_rows + 2 * overshoot/100 + 1); // Space after which the next ridge begins
    this.ridges_height = (canvas_height - abs_space_x_axis_legend) / (data_rows + 2 * overshoot/100 + 1) * (1 + overshoot/100); 

    //Compute line start from font size and biggest label
    //Text scale calculation -------------------------------------------------
    let longest_width = 0;
    if (labels.length > 0)
    {
      let longest_label = labels.reduce(function(prev, current) { return (prev.length > current.length) ? prev : current; } );
      let temp_text = new paper.PointText(new paper.Point(0, 0));
      temp_text.justification = 'left';
      temp_text.fillColor = new paper.Color(0.0, 0.0, 0.0);
      temp_text.content = longest_label;
      temp_text.fontSize = font_size;
      longest_width = temp_text.strokeBounds.width;
      temp_text.remove();
    }
    //Text scale calculation end -------------------------------------------------
    this.grid_line_start_x = longest_width + 10;

    this.grid_line_end_x = grid_line_end_x;
    this.grid_line_start_y = this.ridges_height;

    this.font_size = font_size;

    this.x_axis_start = this.grid_line_start_x;
    this.x_axis_width = canvas_width - this.x_axis_start;

    this.x_axis_value_offset = x_axis_value_offset;

    this.y_axis_start = this.ridges_height;

    //Detect resize event
    if (this.previous_canvas_height != canvas_height || this.previous_canvas_width != canvas_width)
    {
      this.size_was_changed = true;
    }
    else
    {
      this.size_was_changed = false;
    }
    this.previous_canvas_height = canvas_height;
    this.previous_canvas_width = canvas_width;
  }
}






export class Data
{
  private values : number[][][] = []; //Underlying data (list of 2D data)
  private transformed_values : number[][][] = []; //Transformed data (for drawing)

  public x_range: number = 0; //Range in x of data (range of min to max value)
  public y_range: number = 0; //Range in y of data (range of min to max value)
  public max_y_range_from_0: number = 0; //Range in y of data (range of 0 to absolute maximum of min and max)
                          // so it equals either y_max or -y_min
  public xy_min_max: number[][] = []; //min and max value in x and y dimension of data values
  public x_start_value : number = 0; //Smallest x value in data

  //Value for color gradient - can be accessed by getter for each data row index
  private color_gradients : [string, number][] = [];
  private transformed_color_gradients : [paper.Color, number][][] = [];

  //'Watcher' for data changes - transform functions are only applied if data has changed
  //Set to true if data changed, set to false after transform
  private has_untransformed_data = false;
  private has_untransformed_color_gradients = false;

  //Convenience values
  public length: number = 0;

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
    let initialized = false;

    for (let data of data_array)
    {
      let min = this.get_min_x_y(data);
      let max = this.get_max_x_y(data);

      //Store as [[min_x, min_y],[max_x, max_y]]
      if (!initialized){
        result[0][0] = min[0];
        result[0][1] = min[1];
        result[1][0] = max[0];
        result[1][1] = max[1];
        initialized = true;
      } else {
        result[0][0] = Math.min(result[0][0], min[0]);
        result[0][1] = Math.min(result[0][1], min[1]);
        result[1][0] = Math.max(result[1][0], max[0]);
        result[1][1] = Math.max(result[1][1], max[1]);
      }
    }

    return result;
  }

  /**
   * Returns the minimal values of x and y over all x- and y-values in data
   * @returns [min_x, min_y]
   *          [INFINITY, INFINITY] if there is no entry in data
   */
  private get_min_x_y(data: number[][])
  {
    // if (data.length == 0){

    // }
    return data.reduce(
      (previous_value: number[], current_value: number[]) =>
      {
        return [Math.min(previous_value[0], current_value[0]), Math.min(previous_value[1], current_value[1])];
      },
      [Number.POSITIVE_INFINITY, Number.POSITIVE_INFINITY]);
  }

  /**
   * Returns the maximum values of x and y over all x- and y-values in data
   * @returns [max_x, max_y]
   *          [-INFINITY, -INFINITY] if there is no entry in data
   */
  private get_max_x_y(data: number[][])
  {
    return data.reduce(
      (previous_value: number[], current_value: number[]) =>
      {
        return [Math.max(previous_value[0], current_value[0]), Math.max(previous_value[1], current_value[1])];
      },
      [Number.NEGATIVE_INFINITY, Number.NEGATIVE_INFINITY]);
  }

  /**
   * Returns the data restricted to the given x range (to [max_x - x_range, max_x]), only if x_range > 0
   * @param data Data to restrict, assumed to be sorted!
   * @param x_range Desired range of the x values up to the highest value
   */
  private restrict_to_x_range(data: number[][][], x_range: number)
  {
    //Abort if x_range is too small
    if (x_range <= 0) return;

    //Data is assumed to be sorted - max x can thus be obtained easily
    let max_x = 0;
    for (let i = 0; i < data.length; ++i)
    {
      if (data[i].length > 0)
      {
        max_x = Math.max(max_x, data[i][data[i].length - 1][0]);
      }
    }

    for (let i = 0; i < data.length; ++i)
    {
      if (data[i].length > 0)
      {
        let new_start_index = this.values[i].findIndex((element) => element[0] >= (max_x - x_range));

        if (new_start_index >= 0) data[i] = data[i].slice(new_start_index - 1, data[i].length);
        else data[i] = [];
      }
    }
  }

  /**
   * This function sets up all variables of this class dependent on the data.
   * @param data The data of all ridge-line charts.
   * @param max_x_range The window size of the data
   */
  public set_raw_data(data: number[][][], max_x_range: number){
    //Only continue if data is present
    if (data.length == 0)
    {
      return;
    }

    //Store data
    this.values = data;
    this.length = this.values.length;

    //Sort data by x for drawing
    this.sort_by_x(this.values);

    //Restrict data to x range
    this.restrict_to_x_range(this.values, max_x_range);

    //Get relevant data information
    this.xy_min_max = this.get_xy_min_max(this.values);
    this.compute_ranges();

    //Transform needs to be called
    this.has_untransformed_data = true;
    this.has_untransformed_color_gradients = true;
  }

  /**
   * This function updates all variables of this class dependent on the newly added data, and merges it with old data
   * @param new_data The data to add to all ridge-line charts.
   *                 If it contains an empty ridge, nothing changes for this ridge
   * @param max_x_range The window size of the data
   */
  public update_raw_data(new_data: number[][][], max_x_range: number){
    if (new_data.length > 0)
    {
      //Store data
      if (new_data.length > this.values.length)
      {
        this.length = new_data.length;
      }

      //Sort data by x for drawing
      this.sort_by_x(new_data);

      //Update all relevant data information
      let updated_xy_min_max = this.get_xy_min_max(new_data);

      //First update xy_min_max if required
      this.xy_min_max[0][0] = Math.min(this.xy_min_max[0][0], updated_xy_min_max[0][0]);
      this.xy_min_max[0][1] = Math.min(this.xy_min_max[0][1], updated_xy_min_max[0][1]);
      this.xy_min_max[1][0] = Math.max(this.xy_min_max[1][0], updated_xy_min_max[1][0]);
      this.xy_min_max[1][1] = Math.max(this.xy_min_max[1][1], updated_xy_min_max[1][1]);

      //Update other data information based on updated xy_min_max
      this.compute_ranges();

      //Merge new with old data
      for (let i = 0; i < new_data.length; ++i) // iterate over ridges
      {
        //Merge from point where x data starts in new data, else add a new entry if updated data has more rows
        if (i < this.values.length)
        {
          if (new_data[i].length > 0)
          {
            //Delete every value in this.values (old data) with an x value higher or equal to the added data's x values
            let lowest_x = new_data[i][0][0];
            let obsolete_index = this.values[i].findIndex((element) => element[0] >= lowest_x);
            if (obsolete_index >= 0)
            {
              this.values[i].length = obsolete_index;
            }

            //Then add new data
            for (let j = 0; j < new_data[i].length; ++j)
            {
              this.values[i].push(new_data[i][j]);
            }
          }
        }
        else
        {
          this.values[i] = new_data[i];
        }
      }
    }

    //Restrict data to x range
    this.restrict_to_x_range(this.values, max_x_range);
    this.xy_min_max = this.get_xy_min_max(this.values);
    this.compute_ranges();
    //TODO: Change behaviour for xy_min_max in this function, change xy_min_max to expect sorted data (which happens anyway) s.t. computation is much faster (see restrict_to_x_range)

    //Transform needs to be called
    this.has_untransformed_data = true;
  }

  /**
   * Computes x_range, y_range, max_y_range_from_0 and x_start_value dependent on the current xy_min_max
   */
  private compute_ranges(){
    this.x_range = Math.abs(this.xy_min_max[0][0] - this.xy_min_max[1][0]);
    this.y_range = Math.abs(this.xy_min_max[0][1] - this.xy_min_max[1][1]);
    this.max_y_range_from_0 = Math.max(Math.abs(this.xy_min_max[0][1]), Math.abs(this.xy_min_max[1][1]));
    this.x_start_value = this.xy_min_max[0][0];
  }


  public set_color_gradients(color_gradients : [string, number][])
  {
    // Add and sort color_gradients (from min to max data point (regarding y value))
    this.color_gradients = this.sort_by_y_gradient(color_gradients);

    //Transform needs to be called
    this.has_untransformed_color_gradients = true;
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
  public transform_data(config : Ridgeline_Config)
  {
    //Only transform if that is necessary
    if (!this.has_untransformed_data && !config.size_was_changed)
    {
      return;
    }

    //Make a copy of values before transformation, to keep values unchanged
    this.transformed_values = [];
    //this.transformed_color_gradients = [];

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
    }

    //Data is now transformed, so we do not need to transform it again unless it gets changed
    this.has_untransformed_data = false;
  }

  /**
   * Transform color gradients to y-coordinate-values for each ridge, sorted from lowest to highest
   * Add alpha value to gradients (if desired, else set it to 1.0)
   */
  public transform_color_gradients(config : Ridgeline_Config, alpha : number)
  {
    //Only transform if that is necessary
    if ((!this.has_untransformed_color_gradients || this.values.length == 0) && !config.size_was_changed)
    {
      return;
    }

    this.transformed_color_gradients = [];

    //Transform color gradient values - should start and stop relative to 0-line of current ridge (y_from), translate from data point to y-coordinates in paperjs
    let height_scale = config.ridges_height / this.max_y_range_from_0;
    for (let i = 0; i < this.values.length; ++i)
    {
      let y_from = config.y_axis_start + config.ridges_offset * i;

      this.transformed_color_gradients.push([]);

      let max_height = window.innerHeight;
      let min_height = 0;
      for (let j = 0; j < this.color_gradients.length; ++j)
      {
        //Cap gradient y value s.t. we do not get an overflow
        let color_y_value = 0.0;
        if (this.color_gradients[j][1] > 300000000)
        {
          color_y_value = 300000000;
        }
        else if (this.color_gradients[j][1] < -300000000)
        {
          color_y_value = -300000000;
        }
        else
        {
          color_y_value = this.color_gradients[j][1];
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

        //Add transparency to color
        let rgba_color = new paper.Color(this.color_gradients[j][0]);
        rgba_color.alpha = alpha;
        this.transformed_color_gradients[i].push([rgba_color, color_pixel_height_y ]);
      }
    }

    //Gradients are now transformed, so we do not need to transform it again unless it gets changed
    this.has_untransformed_color_gradients = false;
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
export class RidgelineChartComponent implements OnInit, AfterViewInit {
  @Input() overshoot: number;
  @Input() labels: string[]; // The labels of the ridges
  @Input() font_size: number; // The font size in pixel
  @Input() overwrite_data: boolean; //If true, overwrite data on set, else just update/merge it with old data; also has an influence on further data processing
  @Input() x_is_time: boolean=true;
  @Input() show_date: boolean=false; // Only relevant, if x_is_time==true;
  @Input() relative_x_precision: number=2;
  @Input() align_x_label_to = 1000;
  @Input() max_x_range = -1; //For sliding window, applied if > 0
  _rawData: number[][][] = [];
  _alpha: number = 1.0;
  
  //Input function and value for max y value of ridgeline color gradient
  // private color_gradient_max_y : number = 0.3; //Should match default value in HTML
  // enter_gradient_max_y(value: string)
  // {
  //   this.color_gradient_max_y = parseFloat(value);
  // }

  //private canvas_div: HTMLDivElement;
  @ViewChild('canvasContainer') canvas_container_view : ElementRef<HTMLDivElement>;
  @ViewChild('canvas') canvas_view : ElementRef<HTMLCanvasElement>;
  private canvas_container: HTMLDivElement;
  private canvas: HTMLCanvasElement;
  private scope: paper.PaperScope;

  //Ridgeline data and configuration
  private data : Data = new Data();
  private config: Ridgeline_Config = new Ridgeline_Config();
  private had_data_before: boolean = false; //Remember if data has ever been set before (for first-time setup)

  //Transparency of the color the graphs are filled with
  @Input() 
  public set alpha (alpha: number){
    if (alpha >= 0 && alpha <= 1)
    {
      this._alpha = alpha;
    }
  }

  /*
  @Input() 
  public set smooth(smooth: boolean){
    console.log("Smoothed set to: "+smooth);
  }*/

  //Raw data which is transformed and then shown in the plot
  @Input()
  public set rawData(rawData: number[][][]){
    if (rawData.length <= 0)
    {
      return;
    }

    //TODO: Revert this later on, is due to currently faulty data for static example

    this.had_data_before = this._rawData.length > 0;
    this._rawData = rawData;

    //Change behaviour depending on whether we only add data and want to perform our analysis only on that, or whether we actually want to reset the previously set data and overwrite everything
    //Also: First-time behaviour (if no data was set previously) is the same
    if (this.overwrite_data || !this.had_data_before)
    {
      this.data.set_raw_data(rawData, this.max_x_range);
    }
    else
    {
      //This is less CPU-expensive - we set the data block that needs to be updated, which can change e.g. the x-range of our data
      this.data.update_raw_data(rawData, this.max_x_range);
    }

    //Transformation of the (new) data now depends e.g. on the size on the canvas and thus is done within UI functions
  }


  //Color gradients: [y_value, color_string] for gradient shown on displayed data
  @Input() 
  public set color_gradients(gradients : [string, number][])
  {
    this.data.set_color_gradients(gradients);
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
    // let viewport_width_proportion = (0.2/1000 * window.innerWidth + 0.36) * 100;
    // if (viewport_width_proportion > 90)
    // {
    //   viewport_width_proportion = 90;
    // }
    // let canvas_container_width = "width: " + viewport_width_proportion + "vw";
    // this.canvas_container.setAttribute("style", canvas_container_width);

    let width_factor = this.initial_parent_width / this.initial_view_width;
    let height_factor = this.initial_parent_height / this.initial_view_height;

    //Width is set through CSS property / HTML "resize" property because we cannot always use window.innerWidth as orientation
    //paper.view.viewSize.width = this.canvas.width;
    //this.canvas_container.clientWidth - 10
    this.canvas.width = width_factor * window.innerWidth;
    this.canvas.height = height_factor * window.innerHeight;
    this.scope.view.viewSize.width = width_factor * window.innerWidth;
    this.scope.view.viewSize.height = height_factor * window.innerHeight;
  }


  private initial_parent_width;
  private initial_parent_height;
  private initial_view_width;
  private initial_view_height;

  ngAfterViewInit() {
    this.canvas_container = this.canvas_container_view.nativeElement as HTMLDivElement;
    this.canvas = this.canvas_view.nativeElement as HTMLCanvasElement;
    this.canvas.setAttribute("resize", "true");
    this.canvas.setAttribute("width", "100%");
    this.canvas.setAttribute("height", "100%");

    this.initial_parent_width = this.canvas_container.clientWidth;
    this.initial_parent_height = this.canvas_container.clientHeight;
    this.initial_view_width = window.innerWidth;
    this.initial_view_height = window.innerHeight;

    // Set up paperjs with the given canvas
    this.scope = new paper.PaperScope();
    this.scope.setup(this.canvas);

    this.adjust_size();

    window.addEventListener('resize', this.adjust_size.bind(this), false);

    //Paperjs framerate: 60fps
    //We do not need to update the view that often, 3fps should be enough to see real-time data
    let frame_count = 0;

    this.scope.view.onFrame = (event) => {
      console.log("Frame");
      //Only show every 10th frame
      if (frame_count % 20 != 0)
      {
        ++frame_count;
        return;
      }
      frame_count = 1;

      //Update or set data if required - these functions only change the data / gradients if their values have been modified (see @Input set functions)
      let number_of_lines_of_labels = 1
      if (this.x_is_time && this.show_date){
        number_of_lines_of_labels = 2;
      }
      this.config.set_params(this.labels, this.data.length, this.canvas.width, this.canvas.height, this.canvas.width, this.font_size, this.overshoot, number_of_lines_of_labels, this.align_x_label_to);
      this.data.transform_data(this.config);
      this.data.transform_color_gradients(this.config, this._alpha);

      // Should not be called too often
      if (this.hasData()){
        this.scope.project.clear();

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
      // let line = new this.scope.Path.Line(new paper.Point(0, 0), new paper.Point(this.canvas.width, this.canvas.height));
      // line.strokeColor = new paper.Color(0.2, 0.2, 0.2, 0.2);
      // let line2 = new paper.Path.Line(new paper.Point(0, 0), new paper.Point(0, this.config.y_axis_start));
      // line2.strokeColor = new paper.Color(0.2, 0.2, 0.2, 0.2);
      

    }
  }

  private plot_grid(labels: string[], data: Data, config: Ridgeline_Config)
  {
    //Val for later
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
      let line = new this.scope.Path.Line(new this.scope.Point(line_start_x, line_start_y + offset_horizontal*i), new this.scope.Point(line_end_x, line_start_y + offset_horizontal*i));
      line.strokeColor = new this.scope.Color(0.2, 0.2, 0.2, 0.2);

      //Put text left of line
      let text = new this.scope.PointText(new this.scope.Point(0, line_start_y + offset_horizontal*i));
      text.justification = 'left';
      text.fillColor = new this.scope.Color(0.0, 0.0, 0.0);
      text.content = labels[i];
      text.fontSize = font_size;
      //text.scale(text_scale, new this.scope.Point(0, line_start_y + offset_horizontal*i));
    }

    //Vertical lines and labels (TODO)
    let y_bottom_line_end = line_start_y + offset_horizontal * (labels.length - 1) + config.ridges_height;
                            // Consider only length-1 offsets and instead for the last ridge the whole height (offset+overshoot)
    
    let x_label_precision = this.get_x_label_precision(x_value_offset);
    let x_label_start_value = Math.ceil(xy_min_max[0][0] / this.align_x_label_to) * this.align_x_label_to;

    for (let x = x_label_start_value; x <= xy_min_max[1][0]; x += x_value_offset)
    {
      //Round x - TODO: Does of course not work depending on the x axis (scale)
      //x = Math.round((x + Number.EPSILON) * 100) / 100;

      let x_point = (x - xy_min_max[0][0]) / (xy_min_max[1][0] - xy_min_max[0][0]) * x_axis_width + x_axis_start;

      let line = new this.scope.Path.Line(new this.scope.Point(x_point, line_start_y - config.ridges_height),
                                     new this.scope.Point(x_point, y_bottom_line_end));
      line.strokeColor = new this.scope.Color(0.2, 0.2, 0.2, 0.2);

      //Put text below line
      let text = new this.scope.PointText(new this.scope.Point(x_point, y_bottom_line_end + 10));
      text.justification = 'center';
      text.fillColor = new this.scope.Color(0.0, 0.0, 0.0);
      text.content = this.x_to_text(x, x_label_precision);
      text.fontSize = font_size;
      //text.scale(text_scale);
    }
  }

  private plot_ridge(data_row: number[][], gradient_max_y_values: [paper.Color, number][], config: Ridgeline_Config, index: number)
  {
    let y_from = this.config.y_axis_start + this.config.ridges_offset * index;

    //console.log(data_row);

    //Only draw a plot if there's actually any data
    if (data_row.length == 0)
    {
      return;
    }

    let path = new this.scope.Path({
      segments: [new this.scope.Point(data_row[0][0], data_row[0][1])],
      strokeColor: 'black',
      strokeWidth: '1',
    });

    for (let i = 1; i < data_row.length; ++i)
    {
      path.lineTo(new this.scope.Point(data_row[i][0], data_row[i][1]));
    }

    //path.smooth(); -> Leads to problems if we have straight lines in between (line might not stay straight then)

    let path_filler = new this.scope.Path({
      segments: [new this.scope.Point(data_row[0][0], data_row[0][1])],
      strokeColor: new this.scope.Color(0.0, 0.0, 0.0, 0.0),
      strokeWidth: '2',
    });

    for (let i = 1; i < data_row.length; ++i)
    {
      path_filler.lineTo(new this.scope.Point(data_row[i][0], data_row[i][1]));
    }
    //path_filler.smooth();
    path_filler.lineTo(new this.scope.Point(data_row[data_row.length - 1][0], y_from));
    path_filler.lineTo(new this.scope.Point(data_row[0][0], y_from));




    if (gradient_max_y_values.length < 2)
    {
      path_filler.fillColor = new this.scope.Color(0.1, 0.4, 1.0, 0.5);
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

      path_filler.fillColor = new this.scope.Color({
        gradient: {
          stops: gradient_percent
        },
        origin: [data_row[0][0], gradient_max],
        destination: [data_row[0][0], gradient_min]
      });
    }
  }

  /**
   * This function computes how many positions after decimal point are given regarding the x-labels.
   * That can be configured using the parameter relative_x_precision, deciding how many positions are
   * used for every offset > 10**(relative_x_precision).
   * @param x_value_offset  The space between two vertical lines in the plot
   * @returns               Example for relative_x_precision=2:
   *                            x_value_offset > 100 ==> 0
   *                            x_value_offset > 10  ==> 1
   *                            x_value_offset > 1   ==> 2
   *                            x_value_offset > 0.1 ==> 3
   */
  private get_x_label_precision(x_value_offset: number): number{
    let res = 0;
    while (x_value_offset < 10**(this.relative_x_precision-res)){
      res += 1;
    }
    return res;
  }


  /**
   * This function is called when drawing the grid and converts the input x-value into a String which will
   * be shown as label on the x-axis.
   * If x_is_time==true the x value will be interpreted as epoch seconds
   * @param x          The x-value residing at the vertical line for which a label is needed
   * @param precision  How many positions after decimal point are given
   */
  private x_to_text(x: number, precision: number) : string{
    if (this.x_is_time){
      let res = '';
      let date = new Date(x);

      if (this.show_date){
        let y_str = date.getFullYear().toString();
        let month_str = (date.getMonth() + 1).toString().padStart(2,"0");
        let d_str = date.getDate().toString().padStart(2,"0");
        res += d_str+"."+month_str+"."+y_str+"\n";
      }

      let h_str = date.getHours().toString().padStart(2,"0");
      let min_str = date.getMinutes().toString().padStart(2,"0");
      let s_str = date.getSeconds().toString().padStart(2,"0");
      //let ms_str = date.getMilliseconds().toString().padStart(3,"0");

      res += h_str+':'+min_str+':'+s_str;//+':'+ms_str;
      return res;
      
    } else {
      return ''+x.toFixed(precision);
    }
  }
}
