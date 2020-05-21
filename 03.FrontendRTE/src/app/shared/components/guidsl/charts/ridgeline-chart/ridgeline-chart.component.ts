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
  public set_params(data_rows : number, 
    canvas_width: number, 
    canvas_height: number, 
    grid_line_start_x: number, 
    grid_line_end_x: number, 
    x_axis_start: number, 
    font_size: string,
    overshoot: number)
  {
                                    // divide by everything which has to fit into the canvas space
                                    // i.e. the number of ridges (dat_rows), the space which might be needed for the overshoot
                                    // at the top and the bottom (2*overshoot/100), and the additional space for negative values
                                    // needed at the bottom (+1)
                                    // At the moment this covers the axis-description implicitly
    this.ridges_offset = canvas_height / (data_rows + 2 * overshoot/100 + 1); // Space after which the next ridge begins
    this.ridges_height = canvas_height / (data_rows + 2 * overshoot/100 + 1) * (1 + overshoot/100); 

    this.grid_line_start_x = grid_line_start_x;
    this.grid_line_end_x = grid_line_end_x;
    this.grid_line_start_y = this.ridges_height;

    this.font_size = font_size;

    this.x_axis_start = x_axis_start;
    this.x_axis_width = canvas_width;
    this.x_axis_value_offset = 5;                      // TODO set this value automatically dependent on canvas and data

    this.y_axis_start = this.ridges_height;
  }
}






class Data
{
  private values : number[][][]; //Underlying data (list of 2D data)
  private transformed_values : number[][][]; //Transformed data (for drawing)

  public x_range: number; //Range in x of data (range of min to max value)
  public y_range: number; //Range in y of data (range of 0 to absolute maximum of min and max)
                          // so it equals either y_max or -y_min
  public xy_min_max: number[][]; //min and max value in x and y dimension of data values
  public x_start_value : number; //Smallest x value in data

  //Value for color gradient - can be accessed by getter for each data row index
  private transformed_gradient_max_y_values : number[];

  //Convenience values
  public length: number;

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
    //this.y_range = Math.abs(this.xy_min_max[0][1] - this.xy_min_max[1][1]);
    this.y_range = Math.max(Math.abs(this.xy_min_max[0][1]), Math.abs(this.xy_min_max[1][1]));
    this.x_start_value = this.xy_min_max[0][0];
  }

  /**
   * Creates the data for all ridgelines
   * @returns number[][][]
   */
  public setup_with_dummy_data(){
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
    
    this.setup(all_data);
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

  public get_transformed_max_gradient_y(index : number)
  {
    if (this.transformed_gradient_max_y_values.length > index && index >= 0)
    {
      return this.transformed_gradient_max_y_values[index];
    }
    else
    {
      //TODO: Show error
      return 0;
    }
  }

  /**
   * Transform data to start at or after x_start value regarding the (same) coordinate system,
   * scale the coordinate system to x_range/y_range over a given pixel width/height, start at
   * some (x,y)-pixel using translate_x/_y
   * Also invert y value for drawing
   */
  public transform_data(config : Ridgeline_Config, gradient_max_y_value: number)
  {
    //Make a copy of values before transformation, to keep values unchanged
    this.transformed_values = [];
    this.transformed_gradient_max_y_values = [];

    //Transform data to start at center point (translate_x, translate_y) and resize to desired width and height, and invert y coordinate
    //Also, for a uniform x scale, make it start s.t. it regards the smallest actual x_start_value
    //Range in context with width/height gives a scale factor for the data, so all data have a uniform x and y scale in the end
    let width_scale = (config.x_axis_width - config.x_axis_start) / this.x_range;
    let height_scale = config.ridges_height / this.y_range;
    for (let i = 0; i < this.values.length; ++i)
    {
      let y_from = config.y_axis_start + config.ridges_offset * i;
      this.transformed_values[i] = this.values[i].map(
        (item: number[]) =>
        {
          return [(item[0] - this.x_start_value) * width_scale + config.x_axis_start, -(item[1] * height_scale) + y_from];
        }
      );

      this.transformed_gradient_max_y_values[i] = -(gradient_max_y_value * height_scale) + y_from;
    }
  }
}




@Component({
  selector: 'ridgeline-chart',
  templateUrl: './ridgeline-chart.component.html',
})
export class RidgelineChartComponent implements OnInit {
  @Input() smooth: boolean;
  @Input() overshoot: number;
  
  //Input function and value for max y value of ridgeline color gradient
  private color_gradient_max_y : number = 0.3; //Should match default value in HTML
  enter_gradient_max_y(value: string)
  {
    this.color_gradient_max_y = parseFloat(value);
  }

  //private canvas_div: HTMLDivElement;
  private canvas: HTMLCanvasElement;
  private ctx: CanvasRenderingContext2D;
  private path: paper.Path;

  //Ridgeline data and configuration
  private data : Data;
  private config: Ridgeline_Config;


  /*
  @Input() 
  public set smooth(smooth: boolean){
    console.log("Smoothed set to: "+smooth);
  }*/



  public ngOnInit(): void {
  }


  ngAfterViewInit() {    
    this.canvas = document.getElementById('canvas') as HTMLCanvasElement;
    this.canvas.setAttribute("resize", "true");
    this.canvas.setAttribute("width", "100%");
    this.canvas.setAttribute("height", "100%");
    this.ctx = this.canvas.getContext('2d');

    window.addEventListener('resize', () => {
      //Width is set through CSS property / HTML "resize" property because we cannot always use window.innerWidth as orientation
      //paper.view.viewSize.width = this.canvas.width;
      // this.canvas.width = this.canvas.parentElement.parentElement.clientWidth;
      // this.canvas.height = this.canvas.parentElement.parentElement.clientHeight;
      // paper.view.viewSize.width = this.canvas.parentElement.parentElement.clientWidth;
      // paper.view.viewSize.height = this.canvas.parentElement.parentElement.clientHeight;

      let width = Math.random() * 300 + 600;
      //let height = Math.random() * 300 + 600;
      let height = this.data.length * 100; // Choose an arbitrary value which might fit to the number of ridges

      this.canvas.width = width;
      this.canvas.height = height;
      paper.view.viewSize.width = width;
      paper.view.viewSize.height = height;
    }, false);

    // Create test data
    this.data = new Data();
    this.data.setup_with_dummy_data();
    // console.log(this.data.xy_min_max);
    // console.log(this.data.x_range);
    // console.log(this.data.y_range);
    // console.log(this.data.x_start_value);

    // Setup all necessary parameters for drawing
    this.config = new Ridgeline_Config();

    // Set up paperjs with the given canvas
    paper.setup(this.canvas);

    let height = this.data.length * 100; // Choose an arbitrary value which might fit to the number of ridges

    this.canvas.width = 900;
    this.canvas.height = height;
    paper.view.viewSize.width = 900;
    paper.view.viewSize.height = height;

    paper.view.onFrame = (event) => {
      // Should not be called too often
      paper.project.clear();

      this.config.set_params(this.data.length, this.canvas.width, this.canvas.height, 150, this.canvas.width, 150, "1em", this.overshoot);
      this.data.transform_data(this.config, this.color_gradient_max_y);

      //Draw grid for data
      //TODO: Labels should be part of data, text size should determine starting point for drawing lines of grid / plots
      this.plot_grid(['test1', 'test2', 'test3', 'custom', 'random sinus'], this.data, this.config);

      //TODO: Proper arguments for grid drawing
      //TODO: Draw grid for x values (vertical)

      //Draw test data
      for (let i = 0; i < this.data.length; ++i)
      {
        // ToDo: further refinement of config object and function call to avoid so many parameters
        this.plot_ridge(this.data.get_transformed_data_row(i), this.data.get_transformed_max_gradient_y(i), this.config, i); //TODO: Improve color (should be distinct, not white, ...) -> New: Should be the same, use color gradient
      }

      /* // Lines to see some parameters in real life
      let line = new paper.Path.Line(new paper.Point(0, 0), new paper.Point(this.canvas.width, this.canvas.height));
      line.strokeColor = new paper.Color(0.2, 0.2, 0.2, 0.2);
      let line2 = new paper.Path.Line(new paper.Point(0, 0), new paper.Point(0, this.config.y_axis_start));
      line2.strokeColor = new paper.Color(0.2, 0.2, 0.2, 0.2);
      */

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

    //Text scale calculation -------------------------------------------------
    //Calculate width of longest label - allows to scale text s.t. it ends before the line begins
    // let longest_label = labels.reduce(function(prev, current) { return (prev.length > current.length) ? prev : current; } );
    // let temp_text = new paper.PointText(new paper.Point(0, 0));
    // temp_text.justification = 'left';
    // temp_text.fillColor = new paper.Color(0.0, 0.0, 0.0);
    // temp_text.content = longest_label;
    // let longest_width = temp_text.strokeBounds.width;
    // temp_text.remove();

    // //Calculate text scale
    // let text_scale = (line_start_x - 5) / longest_width;

    //Make sure that small texts are not too small
    //Text scale calculation end -------------------------------------------------

    //TODO: Not that clean, text should have a min. width / lines might need to be moved according to that width

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
    for (let x = xy_min_max[0][0]; x < xy_min_max[1][0]; x += x_value_offset)
    {
      let x_point = (x - xy_min_max[0][0]) / (xy_min_max[1][0] - xy_min_max[0][0]) * (x_axis_width - config.x_axis_start) + x_axis_start;
      let line = new paper.Path.Line(new paper.Point(x_point, line_start_y - offset_horizontal), new paper.Point(x_point, line_start_y + offset_horizontal * labels.length));
      line.strokeColor = new paper.Color(0.2, 0.2, 0.2, 0.2);

      //Put text below line
      let text = new paper.PointText(new paper.Point(x_point, line_start_y + offset_horizontal * labels.length + 10));
      text.justification = 'center';
      text.fillColor = new paper.Color(0.0, 0.0, 0.0);
      text.content = '' + x;
      text.fontSize = font_size;
      //text.scale(text_scale);
    }
  }

  private plot_ridge(data_row: number[][], gradient_max_y_value: number, config: Ridgeline_Config, index: number)
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
    path_filler.fillColor = new paper.Color({
      gradient: {
        stops: ['red', 'blue']
      },
      origin: [data_row[0][0], gradient_max_y_value],
      destination: [data_row[0][0], y_from]
    });
  }
}
