import { Component, OnInit, Input, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import * as paper from 'paper';
import { Config } from 'protractor';


/**
 * This class is a configuration class saving important configuration values
 * needed to transform and plot the data of the Ridgelines.
 * Export ONLY for testing!
 */
export class RidgelineConfig {
  /// ridges
  /** Describes the size ot the space between two subsequent ridges. */
  ridges_offset: number;
  /** Describes the maximum height, which one ridge can use. This space can overlap with subsequent ones. */
  ridges_height: number;

  /// values for grid
  /** The x-coordinate at which the grid will start. Left of that the names of the ridges reside. */
  grid_line_start_x: number;
  /** The x-coordinate at which the grid will end. Nothing is considered on the right side of that so it will always correspond to the width of the canvas. */
  grid_line_end_x: number;
  /** The y-coordinate at which the grid will start. Below that the x-axis-description resides. */
  grid_line_start_y: number;
  /** The font size for the names of the ridges and the x-axis-description. */
  font_size: number;

  /// values for x axis of data plots
  /** The x-coordinate at which the x-axis will start. */
  x_axis_start: number;
  /** The length of the x-axis. */
  x_axis_width: number;
  /** The value offset between two x-axis descriptions. */
  x_axis_value_offset: number;
  /** The y-coordinate at which the y-axis will start. */
  y_axis_start: number;

  //Detect resize event (window resize callback leads to strange behaviour, so we had to do this differently)
  previous_canvas_width : number = 0;
  previous_canvas_height : number = 0;
  size_was_changed : boolean = false;


  /**
   * This function has to be called in order to set all parameters.
   * @param labels          The labels of all ridges. Necessary in order to compute needed space on the left side of diagram
   * @param data_rows       The number of ridges.
   * @param canvas_width    Current width of the canvas (which may change)
   * @param canvas_height   Current height of the canvas (which may change)
   * @param font_size       font size of axis description in pixel
   * @param overshoot       how far one ridge overlaps into another one (in percentage)
   * @param number_of_lines_of_labels for how many lines of the x-axis labels should space be reserved
   *                                  (e.g. for the label "Line1\nLine2" this should be 2)
   * @param x_axis_value_offset which numerical distance should be between two x-axis-descriptions
   *                                  (e.g. show 9:00am ----(60min)---> 10:00am ...)
   */
  public set_params(
    labels : string[],
    data_rows : number, 
    canvas_width: number, 
    canvas_height: number, 
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

    this.grid_line_end_x = canvas_width;
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





/**
 * Data class used for managing, storing and transforming the data of Ridgeline-Charts (ridgelines, data range and
 * color gradients) to be drawn on the canvas.
 * Export ONLY for testing!
 */
export class Data
{
  /** Underlying data (list of 2D data) */
  private values : number[][][] = [];

  /** Transformed data (for drawing, transformed to coordinate system of the canvas) */
  private transformed_values : number[][][] = [];


  /** Range in x direction of data (range of min to max value) */
  public x_range: number = 0;

  /** Range in y direction of data (range of min to max value) */
  public y_range: number = 0;

  /** Range in y direction of data (range of 0 to absolute maximum of min and max) so it equals either y_max or -y_min (used for overshoot / in combination with ridge height scaling) */
  public max_y_range_from_0: number = 0; //

  /** Min and max value in x and y dimension of data values, i.e., [[x_min, y_min],[x_max, y_max]] */
  public xy_min_max: number[][] = [];

  /** Smallest x value in data (-> where the graph starts) */
  public x_start_value : number = 0;


  /** Initial value for color gradient */
  private color_gradients : [string, number][] = [];
  /** Transformed value for color gradient - can be accessed by getter for each data row index (stores color with alpha value set by user, and contains gradient y-coordinates already transformed to the canvas' coordinates) */
  private transformed_color_gradients : [paper.Color, number][][] = [];
  /** See bounding_factor_gradients of RidgelineChartComponent */
  public bounding_factor_gradients = 10;


  /** 
   * 'Watcher' for data changes - transform functions are only applied if data or the scaling of the canvas has changed.
   * Set to true if data changed, set to false after transform
   */
  private has_untransformed_data = false;
  /** 
   * 'Watcher' for data changes - transform functions are only applied if data or the scaling of the canvas has changed.
   * Set to true if data changed, set to false after transform
   */
  private has_untransformed_color_gradients = false;

  /** Convenience value */
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
 * Returns the minimal and maximal values out of all points of all ridge-lines
 * @param data_array  The data of all ridge-lines, assumed to be sorted by x per ridgeline!
 * @returns           [[min_x, min_y], [max_x, max_y]]
 */
  private get_xy_min_max(data_array : number[][][])
  {
    //Store as [[min_x, min_y],[max_x, max_y]]
    let result : number[][] = [[0,0],[0,0]];
    let initialized = false;

    for (let data of data_array)
    {
      let min_y = this.get_min_y(data);
      let max_y = this.get_max_y(data);

      let len = data.length;
      let min_x = len > 0 ? data[0][0] : Number.POSITIVE_INFINITY;
      let max_x = len > 0 ? data[len - 1][0] : Number.NEGATIVE_INFINITY;

      //Store as [[min_x, min_y],[max_x, max_y]]
      if (!initialized){
        result[0][0] = min_x;
        result[0][1] = min_y;
        result[1][0] = max_x;
        result[1][1] = max_y;
        initialized = true;
      } else {
        result[0][0] = Math.min(result[0][0], min_x);
        result[0][1] = Math.min(result[0][1], min_y);
        result[1][0] = Math.max(result[1][0], max_x);
        result[1][1] = Math.max(result[1][1], max_y);
      }
    }

    return result;
  }

  /**
   * Returns the minimal values of y over all y-values in data (x values can be obtained more efficiently because the data is sorted by x)
   * @param data  The data of one of the ridge-lines
   * @returns In general: min_y. 
   *          If there is no entry in data: INFINITY.
   */
  private get_min_y(data: number[][])
  {
    return data.reduce(
      (previous_value: number, current_value: number[]) =>
      {
        return Math.min(previous_value, current_value[1]);
      },
      Number.POSITIVE_INFINITY);
  }

  /**
   * Returns the maximum values of y over all y-values in data (x values can be obtained more efficiently because the data is sorted by x)
   * @param data  The data of one of the ridge-lines
   * @returns In general: max_y.
   *          If there is no entry in data: -INFINITY.
   */
  private get_max_y(data: number[][])
  {
    return data.reduce(
      (previous_value: number, current_value: number[]) =>
      {
        return Math.max(previous_value, current_value[1]);
      },
      Number.NEGATIVE_INFINITY);
  }

  /**
   * Returns the data restricted to the given x range (to [max_x - x_range, max_x]), only if x_range > 0
   * @param data Data to restrict, assumed to be sorted by x values per ridgeline!
   * @param x_range Desired range of the x values up to the highest value, ignored if <= 0
   */
  private restrict_to_x_range(data: number[][][], x_range: number)
  {
    //Abort if x_range is too small
    if (x_range <= 0) return;

    //Data is assumed to be sorted - max x can thus be obtained easily
    let max_x = Number.NEGATIVE_INFINITY;
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
        if (new_start_index > 0) data[i] = data[i].slice(new_start_index, data[i].length);
        else if (new_start_index < 0) data[i] = [];
      }
    }
  }

  /**
   * This function sets up all variables of this class dependent on the data. Existing old data gets overriden.
   * @param data The data of all ridge-line charts.
   * @param max_x_range The window size of the data
   */
  public set_raw_data(data: number[][][], max_x_range: number){
    //Only continue if data is present
    if (data.length == 0)
    {
      //Clear old data
      this.values.length = 0;
      this.length = 0;
      this.transformed_values.length = 0;
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
      //Store new data length, if new rows were added
      if (new_data.length > this.values.length)
      {
        this.length = new_data.length;
      }

      //Sort data by x for drawing
      this.sort_by_x(new_data);

      //Update min max values only using new data if no sliding window method is used - else, everything might have to be recomputed, so we would not benefit from this
      if (max_x_range <= 0)
      {
        //Update all relevant data information
        let updated_xy_min_max = this.get_xy_min_max(new_data);

        //First update xy_min_max if required
        this.xy_min_max[0][0] = Math.min(this.xy_min_max[0][0], updated_xy_min_max[0][0]);
        this.xy_min_max[0][1] = Math.min(this.xy_min_max[0][1], updated_xy_min_max[0][1]);
        this.xy_min_max[1][0] = Math.max(this.xy_min_max[1][0], updated_xy_min_max[1][0]);
        this.xy_min_max[1][1] = Math.max(this.xy_min_max[1][1], updated_xy_min_max[1][1]);

        //Update other data information based on updated xy_min_max
        this.compute_ranges();
      }

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

    //Update all relevant data information (both x and y values might have changed) if sliding window is used
    if (max_x_range > 0)
    {
      this.xy_min_max = this.get_xy_min_max(this.values);
      this.compute_ranges();
    }

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


  /**
   * Sets the list of color gradients (ONE list of color gradients for ALL ridges)
   * @param {[string, number][]} color_gradients
   */
  public set_color_gradients(color_gradients : [string, number][])
  {
    // Add and sort color_gradients (from min to max data point (regarding y value))
    this.color_gradients = this.sort_by_y_gradient(color_gradients);

    //Transform needs to be called
    this.has_untransformed_color_gradients = true;
  }


  /**
   * Returns the transformed data of the ridge with the given index.
   * @param index Index for which the ridge should be given.
   * @returns Transformed data of the ridge or [] if index is greater than the number of ridges (then also an error-message will be logged).
   */
  public get_transformed_data_row(index : number)
  {
    if (this.transformed_values.length > index && index >= 0)
    {
      return this.transformed_values[index];
    }
    else
    {
      console.log("Error: Could not transform data row (in get_transformed_data_row(), ridgeline-chart component)");
      return [];
    }
  }


  /**
   * Returns the color gradients specifically transformed for the ridge with the given index,
   * i.e., the values correspond to the exact points on the canvas, which depend on the ridge.
   * @param index Index for the ridge for which the color gradients should be given.
   * @returns Color gradient values for the current ridge or [] if index is greater than the number of ridges (then also an error-message will be logged).
   */
  public get_transformed_color_gradients(index : number)
  {
    if (this.transformed_color_gradients.length > index && index >= 0)
    {
      return this.transformed_color_gradients[index];
    }
    else
    {
      console.log("Error: Could not transform color gradients row (in get_transformed_color_gradients(), ridgeline-chart component)");
      return [];
    }
  }

  /**
   * Transform data to start at or after x_start value regarding the (same) coordinate system for all ridges,
   * scale the coordinate system to x_range/y_range over a given pixel width/height, start at
   * some (x,y)-pixel using translate_x/_y
   * Also invert y value for drawing
   * @param config Configuration data to find out on which values to scale / transform the data (e.g. fit data to coordinate system of the canvas)
   */
  public transform_data(config : RidgelineConfig)
  {
    //Only transform if that is necessary
    if (!this.has_untransformed_data && !config.size_was_changed)
    {
      return;
    }

    //Make a copy of values before transformation, to keep values unchanged
    this.transformed_values = [];

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
   * Add alpha value to gradients (if desired, else set it to 1.0).
   * This function will log a warning if a computed color stop will reside too far outside of the visible area
   * (see param canvas_height).
   * @param config Configuration data to find out on which values to scale / transform the data (e.g. fit data to coordinate system of the canvas)
   * @param alpha Alpha value to be applied to the colors previously set by the user
   * @param canvas_height The current height of the canvas times the constant 5 is used here to limit the
   *                      y-coordinate at which a color stop may reside. That is due to the problem that the
   *                      color gradients will not work properly if they reside too far outside of the visible area.
   */
  public transform_color_gradients(config : RidgelineConfig, alpha : number, canvas_height : number)
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

      // limit the y-coordinates at which a color stop may reside
      let max_height = this.bounding_factor_gradients*canvas_height;
      let min_height = -(this.bounding_factor_gradients - 1) * canvas_height; // allow same non-visible space in negative direction (-1 since negative space is never visible)
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
          console.warn("The color stop with color "+this.color_gradients[j][0]+" would be too far outside of the ridgeline-plot and is thus capped to "+max_height+"!");
        }
        if (color_pixel_height_y < min_height)
        {
          color_pixel_height_y = min_height;
          console.warn("The color stop with color "+this.color_gradients[j][0]+" would be too far outside of the ridgeline-plot and is thus capped to "+min_height+"!");
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

  /**
   * This function sorts all gradients by the y-coordinate (rising).
   * @param gradients The gradients which are to be sorted.
   */
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

  /**
   * Can be used to check if the internal data structure for the ridgelines was set
   * @returns {boolean} True if data is set, false otherwise
   */
  public has_data(): boolean{
    return this.values.length > 0;
  }
}








/**
 * This component can be used to create / show a ridgeline chart.
 * If you want to display density charts, please provide 2D density data already
 * Expected data format: 
 * List of list of [x,y] data (i.e. list for each ridge).
 * Please make sure that the number of labels corresponds to the number of ridges which should be displayed.
 * (First label corresponds to first ridge etc.).
 * 
 * @example
 * <ridgeline-chart
 *  [alpha]=0.8
 *  [overshoot]=30
 *  [raw_data]=data_ridgeline
 *  [overwrite_data]="true"
 *  [labels]=labels_ridgeline
 *  [font_size]=12
 *  [color_gradients]=color_gradients_ridgeline
 *  [x_is_time]="true"
 *  [show_date]="true"
 *  [align_x_label_to]=60000000>
 * </ridgeline-chart>
 */
@Component({
  selector: 'ridgeline-chart',
  templateUrl: './ridgeline-chart.component.html',
  styleUrls: ['./ridgeline-chart.component.scss'],
})
export class RidgelineChartComponent implements AfterViewInit {
  /**
   * Specifies how much one ridge will overlap with the subsequent one in %. Consequently,
   * the maximum y-value will overlap exactly that far.
   */
  @Input() overshoot: number = 30;

  /**
   * The labels for the ridges. Will be shown on the left side of the ridges. Must correspond to the number of ridges (first label -> first ridge etc.)
   */
  @Input() labels: string[] = [];

  /**
   * The font sizes of the x-axis-descriptions and the labels on the left side. Has to be specified in pixels.
   */
  @Input() font_size: number = 12;

  /**
   * Changes the operating mode of the diagram.
   * If true, all data given earlier will be overridden when new data arrives.
   * If false, the newly given data will be appended to the old data in the following manner: The smallest x-value in the new
   * data is detected and every entry in the old data with an x-value greater or equal than this smallest one deleted.
   * Afterwards all new points are appended to the old ones.
   * To specify the maximum x-range, i.e., when points are deleted again, see [max_x_range][@link max_x_range]. This creates a "sliding window".
   */
  @Input() overwrite_data: boolean;

  /**
   * Set whether the x-value should be interpreted as timestamp in milliseconds since epoch. The x-axis-description
   * will change accordingly.
   */
  @Input() x_is_time: boolean=true;

  /**
   * Only relevant if x_is_time=true. Then it determines whether the date should be shown on the x-axis in addition to
   * the time (hours:minutes:seconds) or not.
   */
  @Input() show_date: boolean=false;

  /**
   * This parameter is only active if x_is_time=false and x-values are interpreted as usual.
   * With this parameter it can be controlled how many positions after decimal point are given regarding the x-labels.
   * The decision is done relatively to the value offset between two subsequent x-labels.
   * If this offset is in the order of 10^x, there will be max(0, realtive_x_precision-x) positions after decimal point.
   * Note: Negative values are also allowed and will work as specified but will not be desired most likely.
   */
  @Input() relative_x_precision: number=2;

  /**
   * Determines at which x-positions the diagram will plot an x-axis-description.
   * E.g. if the x-values can be interpreted as milliseconds the value 1000 will specify that there will be a
   * x-axis-description at each exact second (all positions after the fourth one will be 0 at the description).
   */
  @Input() align_x_label_to = 1000;

  /**
   * Only applied if > 0 and [overwrite_data][@link overwrite_data]=false.
   * If new data is appended to the already existing data, every entry with an x-value lower than max(x)-max_x_range
   * will be deleted from the currently shown data. The result is a sliding x-window.
   */
  @Input() max_x_range = -1;

  /**
   * This parameter regards a technical detail for drawing color gradients. If a y-value of a gradient-stop (given in var
   * color_gradients) is significantly larger/smaller than the max/min of the data shown in the diagram,
   * the algorithm will limit the gradient-stop's y-value. If that isn't done, the gradients would not work properly in
   * some cases due to paperjs as the underlying framework. The limit is computed depending on the current canvas height
   * multiplied with this parameter. If the limiting is applied, a warning will be logged.
   * @param factor Default is 10.
   */
  @Input()
  public set bounding_factor_gradients (factor : number){
    this.data.bounding_factor_gradients = factor;
  }


  /**
   * The alpha value used for all color-stops.
   */
  private _alpha: number = 1.0;

  

  /** Canvas view, used to draw on */
  @ViewChild('canvas', { read: ElementRef }) canvas_view : ElementRef<HTMLCanvasElement>;
  /** Canvas as HTML element, extracted from canvas view, used to set up paperjs object and for rescaling */
  private canvas: HTMLCanvasElement;
  /** Scope for paperjs for this graph */
  private scope: paper.PaperScope;

  /** Ridgeline data object, see [Data class][@link Data] if you want to know implementation internals */
  private data : Data = new Data();
  /** Ridgeline configuration object, see [Configuration class (RidgelineConfig)][@link RidgelineConfig] if you want to know implementation internals */
  private config: RidgelineConfig = new RidgelineConfig();

  /**
   * Remember if data has ever been set before (for first-time setup)
   */
  private had_data_before: boolean = false;

  /**
   * Transparency of the colors the graphs are filled with
   * 
   * @param {number} alpha The alpha value for all colors (0 - 1)
   * 0: fully transparent, 1: not transparent
   */
  @Input() 
  public set alpha (alpha: number){
    if (alpha >= 0 && alpha <= 1)
    {
      this._alpha = alpha;
    }
  }


  /**
   * Set raw data which is then transformed and shown in the plot
   * 
   * @param {number[][][]} raw_data 
   * Consists of a list of:
   *  a list of [x, y] data (for each ridgeline)
   * to be displayed. 
   * Note: If you want to display density charts, please note that you need to compute them yourself, and then just pass them as [x, y] data
   */
  @Input()
  public set raw_data(raw_data: number[][][]){
    // if (raw_data.length <= 0)
    // {
    //   return;
    // }

    this.had_data_before = this.has_data();

    //Change behaviour depending on whether we only add data and want to perform our analysis only on that, or whether we actually want to reset the previously set data and overwrite everything
    //Also: First-time behaviour (if no data was set previously) is the same
    if (this.overwrite_data || !this.had_data_before)
    {
      this.data.set_raw_data(raw_data, this.max_x_range);
    }
    else if (raw_data.length > 0)
    {
      //This is a bit less CPU-expensive - we set the data block that needs to be updated, which can change e.g. the x-range of our data
      this.data.update_raw_data(raw_data, this.max_x_range);
    }

    //Transformation of the (new) data now depends e.g. on the size on the canvas and thus is done within UI functions
  }


  /**
   * Use this to set color gradients:  
   * The gradient-stops then start at y_value with a color given by color_string.
   * Note: If the y-value of a gradient-stop is significantly larger/smaller than the max/min of the data shown in the diagram,
   * the algorithm will limit the gradient-stop's y-value (see [bounding_factor_gradients][@link bounding_factor_gradients]). If that is not done, the gradients would not work properly in
   * some cases due to paperjs as the underlying framework. If the limiting is applied, a warning will be logged. 
   * 
   * @type {[string, number][]} gradients in the form of [color_string, y_value_stop]
   * Color strings: Hex value (like #000000) or color strings like 'blue' (if these are accepted by paperjs)
   * Alpha: Set separately for all gradients with the [alpha]{@link alpha} parameter
   * y_value_stop: y value in your data sets from which you want the color stop to start - note: color fade starts from previous color stop to this color stop
   * -> Example for sharp color stop (only 0.1 y-range for gradient between the two colors)
   * 
   * @example
   * [["#29b6f6", 0], ["#29b6f6", 19.9], ["#ff7043", 20]]
   */
  @Input() 
  public set color_gradients(gradients : [string, number][])
  {
    this.data.set_color_gradients(gradients);
  }


  /**
   * Can be used to check if the internal data structure for the ridgelines was set
   * @returns {boolean} True if data is set, false otherwise
   */
  public has_data(): boolean{
    return this.data.has_data();
  }


  /**
   * Resets the data of the plot.
   */
  public reset_data() {
    this.data.set_raw_data([],1);
  }

  /**
   * Callback resize event listener of the window, to adjust canvas & paperjs size
   */
  private adjust_size()
  {
    //Canvas scales automatically to width
    //Height always depends on the current window height (other methods did not seem to make more sense)
    this.canvas.width = this.canvas.offsetWidth;
    this.canvas.height = window.innerHeight / 2;
    this.scope.view.viewSize.width = this.canvas.offsetWidth;
    this.scope.view.viewSize.height = window.innerHeight / 2;
  }

  /**
   * This function sets the canvas, a resize callback and creates the paperjs context and frame function for updating the graph on content change
   */
  ngAfterViewInit() {
    this.canvas = this.canvas_view.nativeElement as HTMLCanvasElement;
    this.canvas.setAttribute("resize", "true");

    this.canvas.width = this.canvas.offsetWidth;
    this.canvas.height = 200;

    // Set up paperjs with the given canvas
    this.scope = new paper.PaperScope();
    this.scope.setup(this.canvas);

    this.adjust_size();

    window.addEventListener('resize', this.adjust_size.bind(this), false);

    //Paperjs framerate: 60fps
    //We do not need to update the view that often, 3fps should be enough to see real-time data
    let frame_count = 0;

    this.scope.view.onFrame = (event) => {
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
      this.config.set_params(this.labels, this.data.length, this.canvas.width, this.canvas.height, this.font_size, this.overshoot, number_of_lines_of_labels, this.align_x_label_to);
      this.data.transform_data(this.config);
      this.data.transform_color_gradients(this.config, this._alpha, this.canvas.height);

      // Should not be called too often
      if (this.has_data()){
        this.scope.project.clear();

        //Draw grid for data
        this.plot_grid(this.labels, this.data, this.config);

        //Draw test data
        for (let i = 0; i < this.data.length; ++i)
        {
          this.plot_ridge(this.data.get_transformed_data_row(i), this.data.get_transformed_color_gradients(i), i);
        }
      }
      else
      {
        this.scope.project.clear();
      }
    }
  }

  /**
   * Draws a grid for the x and y axis, and labels on the left side of each ridge
   * @param labels Array of labels for each ridge, drawn left of the ridge
   * @param data Data class which stores information about e.g. the data range, which is useful for drawing the x- and y-axis
   * @param config Configuration data, explicitly set by the user or derived from the set data, to e.g. find out where to draw the labels
   */
  private plot_grid(labels: string[], data: Data, config: RidgelineConfig)
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

    //Vertical lines and labels
    let y_bottom_line_end = line_start_y + offset_horizontal * (labels.length - 1) + config.ridges_height;
                            // Consider only length-1 offsets and instead for the last ridge the whole height (offset+overshoot)
    
    let x_label_precision = this.get_x_label_precision(x_value_offset);
    let x_label_start_value = Math.ceil(xy_min_max[0][0] / this.align_x_label_to) * this.align_x_label_to;

    for (let x = x_label_start_value; x <= xy_min_max[1][0]; x += x_value_offset)
    {
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

  /**
   * Plot one of the ridges with this function; index is used to determine the y-offset of each ridge, to plot them below one another
   * @param data_row Data for one ridge ([x,y]-data), already transformed from the raw data to pixel values on the canvas
   * @param gradient_max_y_values Gradient values, to set color stops for filling the ridges
   * @param index Index of data_row, to find out where to draw the ridge baseline on the y axis
   */
  private plot_ridge(data_row: number[][], gradient_max_y_values: [paper.Color, number][], index: number)
  {
    let y_from = this.config.y_axis_start + this.config.ridges_offset * index;

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
   * @param x_value_offset  The value-space between two vertical lines in the plot
   * @example               Example for relative_x_precision=2:
   *                            x_value_offset is greater than | return
   *                                           100                  0
   *                                            10                  1
   *                                             1                  2
   *                                             0.1                3
   */
  public get_x_label_precision(x_value_offset: number): number{
    return Math.max(0, this.relative_x_precision-Math.floor(Math.log10(x_value_offset)));
  }


  /**
   * This function is called when drawing the grid and converts the input x-value into a String which will
   * be shown as label on the x-axis.
   * If x_is_time==true the x value will be interpreted as epoch milliseconds
   * @param x          The x-value residing at the vertical line for which a label is needed
   * @param precision  How many positions after decimal point are given (expected to be a natural number >0)
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
