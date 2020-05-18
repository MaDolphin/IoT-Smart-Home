import { Component, OnInit, Input, ViewChild } from '@angular/core';
import * as paper from 'paper';


/**
 * This class is a configuration class saving important configuration values
 * needed to transform and plot the data of the Ridgelines.
 */
class Ridgeline_Config {
  translate_x: number;
  translate_y: number;

  /// values for grid
  grid_line_start_x: number = 100;
  grid_line_end_x: number =   900;
  grid_line_start_y: number;

  // values axis
  x_axis_start: number;
  x_axis_width: number;
  x_axis_value_offset: number; // The value offset between two x-axis descriptions (e.g. 5 --> 5,10,15,...)
                          // TODO: set this value automatically dependent on data range

  // ridges
  ridges_offset: number; // == offset horizontal // describes the size ot the space between two subsequent ridges
  ridges_height: number; // describes the maximum height, which one ridge can use. This space can overlap with subsequent ones
  width: number;  // the width of all ridges // TODO BETTER NAME
  ridges_x_start: number; // replaces x_from
  ridges_y_start: number;


  /// values depending on data
  x_range: number;
  y_range: number;
  xy_min_max: number[][];
  x_start_value : number;
  colors: paper.Color[];

  /**
   * This function sets up all variables of this class dependent on the data.
   * @param data The data of all ridge-line charts.
   */
  public setup_param_data(data: number[][][]){
    //Get relevant data information
    this.xy_min_max = get_xy_min_max(data);
    this.x_range = Math.abs(this.xy_min_max[0][0] - this.xy_min_max[1][0]);
    this.y_range = Math.abs(this.xy_min_max[0][1] - this.xy_min_max[1][1]);
    this.x_start_value = this.xy_min_max[0][0];

    this.colors = [];
    for (let i = 0; i < data.length; ++i)
    {
      this.colors.push(new paper.Color(Math.random(), Math.random(), Math.random(), 0.2));
    }
  }


  /**
   * TODO Rename this function with a better suited name
   * @param all_data 
   * @param canvas_width 
   * @param canvas_height 
   */
  public setup_param(all_data: number[][][], canvas_width: number, canvas_height: number){
    //Set ridge offset etc
    this.ridges_offset = canvas_height / all_data.length * 1/3;
    this.ridges_height = canvas_height / all_data.length;
    this.width = canvas_width;
    this.ridges_x_start = 150;
    this.ridges_y_start = this.ridges_height;

    this.grid_line_start_y = this.ridges_y_start;
    this.x_axis_value_offset = 5;                      // TODO set this value automatically dependent on canvas and data
  }
}





@Component({
  selector: 'ridgeline-chart',
  templateUrl: './ridgeline-chart.component.html',
})
export class RidgelineChartComponent implements OnInit {
  @Input() smooth: boolean;

  //private canvas_div: HTMLDivElement;
  private canvas: HTMLCanvasElement;
  private ctx: CanvasRenderingContext2D;
  private path: paper.Path;

  private config: Ridgeline_Config;


  /*
  @Input() 
  public set smooth(smooth: boolean){
    console.log("Smoothed set to: "+smooth);
  }*/



  public ngOnInit(): void {
  }


  ngAfterViewInit() {
    console.log(this.smooth);
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
      let height = Math.random() * 300 + 600;

      this.canvas.width = width;
      this.canvas.height = height;
      paper.view.viewSize.width = width;
      paper.view.viewSize.height = height;
    }, false);

    //Create test data
    let all_data = get_all_test_data();

    //Sort data by x for drawing
    this.sort_by_x(all_data);

    // Setup all necessary parameters for drawing
    this.config = new Ridgeline_Config();
    this.config.setup_param_data(all_data);


    

    //Set up paperjs with the given canvas
    paper.setup(this.canvas);

    paper.view.onFrame = (event) => {
      //Sollte nicht zu oft aufgerufen werden
      paper.project.clear();

      let canvas_width = this.canvas.width;
      let canvas_height = this.canvas.height;

      this.config.setup_param(all_data, canvas_width, canvas_height);

      //Draw test data
      for (let i = 0; i < all_data.length; ++i)
      {
        // ToDo: further refinement of config object and function call to avoid so many parameters
        this.plot_ridge(all_data[i], this.config.ridges_y_start + this.config.ridges_offset * i, this.config.colors[i]); //TODO: Improve color (should be distinct, not white, ...)
      }

      //TODO: Proper arguments for grid drawing
      //TODO: Draw grid for x values (vertical)

      //Draw grid for data
      this.plot_grid(['test1', 'test2', 'test3', 'custom', 'random sinus']);
    }
  }

  private plot_grid(labels: string[])
  {
    let config = this.config;

    let line_start_x = config.grid_line_start_x;
    let line_end_x = config.grid_line_end_x;
    let line_start_y = config.grid_line_start_y;
    let offset_horizontal = config.ridges_offset;
    let xy_min_max = config.xy_min_max;
    let x_axis_start = config.ridges_x_start;
    let x_axis_width = config.width;
    let x_value_offset = config.x_axis_value_offset;


    //Calculate width of longest label - allows to scale text s.t. it ends before the line begins
    let longest_label = labels.reduce(function(prev, current) { return (prev.length > current.length) ? prev : current; } );
    let temp_text = new paper.PointText(new paper.Point(0, 0));
    temp_text.justification = 'left';
    temp_text.fillColor = new paper.Color(0.0, 0.0, 0.0);
    temp_text.content = longest_label;
    let longest_width = temp_text.strokeBounds.width;
    temp_text.remove();

    //Calculate text scale
    let text_scale = (line_start_x - 5) / longest_width;

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
      text.scale(text_scale, new paper.Point(0, line_start_y + offset_horizontal*i));
    }

    //Vertical lines and labels (TODO)
    for (let x = xy_min_max[0][0]; x < xy_min_max[1][0]; x += x_value_offset)
    {
      let x_point = (x - xy_min_max[0][0]) / (xy_min_max[1][0] - xy_min_max[0][0]) * x_axis_width + x_axis_start;
      let line = new paper.Path.Line(new paper.Point(x_point, line_start_y - offset_horizontal), new paper.Point(x_point, line_start_y + offset_horizontal * labels.length));
      line.strokeColor = new paper.Color(0.2, 0.2, 0.2, 0.2);

      //Put text below line
      let text = new paper.PointText(new paper.Point(x_point, line_start_y + offset_horizontal * labels.length + 10));
      text.justification = 'center';
      text.fillColor = new paper.Color(0.0, 0.0, 0.0);
      text.content = '' + x;
      text.scale(text_scale);
    }
  }

  private plot_ridge(data: number[][], y_from: number, color: paper.Color)
  {
    let config = this.config;

    let x_from = config.ridges_x_start;
    let width = config.width;
    let height = config.ridges_height;
    let x_range = config.x_range;
    let y_range = config.y_range;
    let x_start_value = config.x_start_value;


    data = this.transform_data(data, width, height, x_range, y_range, x_start_value, x_from, y_from);

    //Only draw a plot if there's actually any data
    if (data.length == 0)
    {
      return;
    }

    let path = new paper.Path({
      segments: [new paper.Point(data[0][0], data[0][1])],
      strokeColor: 'black',
      strokeWidth: '1',
    });

    for (let i = 1; i < data.length; ++i)
    {
      path.lineTo(new paper.Point(data[i][0], data[i][1]));
    }

    //path.smooth(); -> Leads to problems if we have straight lines in between (line might not stay straight then)

    let path_filler = new paper.Path({
      segments: [new paper.Point(data[0][0], data[0][1])],
      strokeColor: new paper.Color(0.0, 0.0, 0.0, 0.0),
      strokeWidth: '2',
    });

    for (let i = 1; i < data.length; ++i)
    {
      path_filler.lineTo(new paper.Point(data[i][0], data[i][1]));
    }
    //path_filler.smooth();
    path_filler.lineTo(new paper.Point(data[data.length - 1][0], y_from));
    path_filler.lineTo(new paper.Point(data[0][0], y_from));
    path_filler.fillColor = color;
  }



  /**
   * Transform data to start at or after x_start value regarding the (same) coordinate system,
   * scale the coordinate system to x_range/y_range over a given pixel width/height, start at
   * some (x,y)-pixel using translate_x/_y
   * Also invert y value for drawing
   * 
   * TODO: Also use config here!!!
   */
  private transform_data(data: number[][], width: number, height: number, x_range: number, y_range: number, x_start_value : number, translate_x: number, translate_y: number)
  {
    //Transform data to start at center point (translate_x, translate_y) and resize to desired width and height, and invert y coordinate
    let width_scale = width / x_range;
    let height_scale = height / y_range;
    return data.map(
      function(item: number[])
      {
        return [(item[0] - x_start_value) * width_scale + translate_x, -(item[1] * height_scale) + translate_y];
      }
    );
  }


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

}




// ----------------------------------------------------- Min and Max computations -----------------------------------------------------


/**
 * Returns the minimal an maximal values out of all points of all ridge-lines
 * @param data_array  The data of all ridge-lines
 * @returns           [[min_x, min_y], [max_x, max_y]]
 */
function get_xy_min_max(data_array : number[][][])
{
  //Store as [[min_x, min_y],[max_x, max_y]]
  let result : number[][] = [[0,0],[0,0]];

  for (let data of data_array)
  {
    let min = get_min_x_y(data);
    let max = get_max_x_y(data);

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
function get_min_x_y(data: number[][])
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
function get_max_x_y(data: number[][])
{
  return data.reduce(
    function(previous_value: number[], current_value: number[])
    {
      return [Math.max(previous_value[0], current_value[0]), Math.max(previous_value[1], current_value[1])];
    }
  );
}






// ----------------------------------------------------- Test Data -----------------------------------------------------

/**
 * Creates the data for all ridgelines
 * @returns number[][][]
 */
function get_all_test_data(){
  let test_data_1 = get_test_data(-15, 15, 0.25);
  let test_data_2 = get_test_data(-10, 20, 0.25);
  let test_data_3 = get_test_data(-5, 25, 0.25);
  let test_data_4 = [[19,0.1], [-5, 0.1], [24, 0.05], [-7, -0.1]];
  let test_data_5 : number[][] = [];

  for (let i = -2; i < 18; i+=0.25)
  {
    test_data_5.push([i, Math.sin(i / 2.0) * Math.random()]);
  }
  let all_data = [test_data_1, test_data_2, test_data_3, test_data_4, test_data_5];
  return all_data;
}


//Creates function data array from x_1 to x_2, in step_size steps, with one of two random functions
function get_test_data(x_1: number, x_2: number, step_size: number)
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
