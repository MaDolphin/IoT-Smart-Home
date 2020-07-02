import { Component, ElementRef, Input, OnChanges, ViewChild, ViewEncapsulation, HostListener, SimpleChanges } from '@angular/core';
import * as d3 from 'd3';

import {Data2Model, datatypes} from 'src/app/data/data.model';
@Component({
  selector: 'density-chart',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './density-chart.component.html'
})

export class DensityChartComponent implements OnChanges {
  @ViewChild('my_dataviz')
  private chartContainer: ElementRef;
  @ViewChild('no_data_label')
  private informationLabel: ElementRef;

  @Input()
  data2;
  @Input()
  transitionTime = 1000;

  levels:Array<datatypes> = [
      {num: 0, name: "temperature"},
      {num: 1, name: "CO2"},
      {num:-1, name:"ALL"}
  ];
  selectedLevel = this.levels[0];
  
  get getData(): any {
    return this.data2
  }
  set setData(data2Model) {
    this.data2 = this.data2
  }

  constructor() {}

  margin = {top: 30, right: 600, bottom: 30, left: 100};
  firstCall = 1;
  currentData: Array<Data2Model> = [];
  x; // x axis
  y; // y axis
  svg; // top level svg element
  paths; // path element for each density curve

  /*** update chart if new data arrives **/
  ngOnChanges(changes: SimpleChanges): void {  
    this.updateData(false);
  }
  
  /**if user changes selection display new chart**/
  public onSelectionChange(): void {
      this.currentData = [];
      this.updateData(true);
  }
  
  public updateData(refresh):void
  { 
    if (this.data2 == null) {
        this.informationLabel.nativeElement.innerHTML = "No data available";
    } else if (this.data2.length <= 0 || this.data2.entries.length <= 0) {
        this.informationLabel.nativeElement.innerHTML = "received data is empty";
    } else {
        this.informationLabel.nativeElement.innerHTML = "";
        //push all received data to the array
        for (let i = 0; i < this.data2.entries.length; i++) {
            this.currentData.push({type: this.data2.entries[i].name, value: this.data2.entries[i].value});
        }
        if(refresh)
        {  
            this.createDensityChart(this.currentData);
        }else{
            this.updateChart(this.currentData);
        }
    }
  }

/**  onResize(changes: SimpleChanges) {
    this.updateChart(changes.data2.currentValue);
  }**/
  public createDensityChart(data2: Data2Model[]): void {
    d3.select('svg').remove();
    const densityElement = this.chartContainer.nativeElement;
    const data = data2;

    /**
     * select chart from html and append an svg
     */
    this.svg = d3.select(densityElement).append('svg')
      .attr('width', densityElement.offsetWidth)
      .attr('height',  densityElement.offsetHeight);
    /**
     * @ignore
     */
    const width = densityElement.offsetWidth - this.margin.left - this.margin.right;
    const height = densityElement.offsetHeight - this.margin.top - this.margin.bottom;
    const g = this.svg.append('g')
      .attr('transform', 'translate(' + this.margin.left + ',' + this.margin.top + ')');
    /**
     * @ignore
     */
    const maxX = Math.max.apply(Math, data.map((d) => d.value));
    const minX = Math.min.apply(Math, data.map((d) => d.value));

    /**
     * Add the x Axis
     */
    this.x = d3
      .scaleLinear()
      .domain([minX - 10, maxX + 10]) // fixed scale is better for changing data // [minX - 8, maxX + 8])
      .range([0, width]);

    /**
     * Create the horizontal lines
     */
    const makeYLines = () => d3.axisLeft(this.x)
      .scale(this.y);
    g.append('g')
      .attr('class', 'grid')
      .attr('transform', 'translate(0,' + height + ')')
      .call(d3.axisBottom(this.x));
    /**
     * Compute kernel density estimation
     */
    const kde = this.kernelDensityEstimator(this.kernelEpanechnikov(7 as number), this.x.ticks(60));

    /**
     * Gets the city names and gives each one a color
     */
    const color = [];
    const types = [];
    kde(data
      .filter((d) => {
        if (types.indexOf(d.type) === -1 && this.isSelected(d.type)) {
          types.push(d.type);
          color.push('#' + (0xd95d80 + (1 / types.length) * 0xfffff0).toString(16).substr(1, 6));
        }
      }));

    /**
     * iterate through all data and plot it
     */
    const density = [];
    for (let index = 0; index < types.length; index++) {
      density[index] = kde(data.filter((d) => {
        return d.type === types[index];
      })
        .map((d) => {
          return d.value;
        }));
    }

    let maxY = - Number.MAX_SAFE_INTEGER;
    for (let i = 0; i < density.length; i++) {
      // tslint:disable-next-line:prefer-for-of
      for (let j = 0; j < density[i].length; j++) {
        if (density[i][j][1] > maxY) {
          maxY = density[i][j][1];
        }
      }
    }

    /**
     * Adds the y Axis
     */
    this.y = d3
      .scaleLinear()
      .range([height, 0])
      .domain([0, maxY + 0.02]);

    g.append('g')
      .call(d3.axisLeft(this.y))
      .call(makeYLines()
        .tickSize(-width)
      );
    this.paths = [];
    for (let index = 0; index < types.length; index++) {
      /**
       * Plot the area
       */
      this.paths.push( this.svg.append('path')
        .datum(density[index])
        .attr('fill', color[index])
        .attr('opacity', '.8')
        .attr('stroke', '#000')
        .attr('stroke-width', 1)
        .attr('stroke-linejoin', 'round')
        .attr('d', d3.line()
          .curve(d3.curveNatural)
          .x((d) => this.x(d[0]) + this.margin.left)
          .y((d) => this.y(d[1]) + this.margin.top)
        )
      );
      /**
       * Adds the text to both axis
       */
      this.svg.append('text')
        .attr('text-anchor', 'end')
        .attr('x', width / 2 + this.margin.left)
        .attr('y', height + 1.9 * this.margin.top )
        .text('Temperatur');
      this.svg.append('text')
        .attr('text-anchor', 'end')
        .attr('transform', 'rotate(-90)')
        .attr('y', this.margin.left / 2.4)
        .attr('x', (height / 2) - this.margin.left)
        .text('Density');

      /**
       * Creates the legend
       */
      this.svg.append('circle').attr('cx', 750).attr('cy', 30 * index + 40).attr('r', 6).style('fill', color[index]);
      this.svg.append('text').attr('x', 770).attr('y', 30 * index + 40).text(types[index])
        .style('font-size', '14px').attr('alignment-baseline', 'middle');
    }
  }

  /**
   * Function to compute density
   * @param kernel The kernel
   * @param X Number of Ricks
   */
  public kernelDensityEstimator(kernel, X) {
    return (V) => {
      return X.map((x) => {
        return [x, d3.mean(V, (v) => kernel(x - (v as number)))];
      });
    };
  }

  /**
   * Returns the value of the Epanechnikov kernel
   * @param k
   */
  public kernelEpanechnikov(k) {
    return (v) => {
      return Math.abs(v /= k) <= 1 ? 0.75 * (1 - v * v) / k : 0;
    };
  }
  /**
  * Update chart with new values
  */
  public updateChart(data2: Data2Model[]) {
    if (this.firstCall == 1) {
      this.createDensityChart(data2);
      this.firstCall = 0;
    } else {
      const kde = this.kernelDensityEstimator(this.kernelEpanechnikov(7), this.x.ticks(60));
      const types = [];
      kde(data2
        .filter((d) => {
          if (types.indexOf(d.type) === -1 && this.isSelected(d.type)) {
            types.push(d.type);
          }
        }));
      // compute density
      const density = [];
      for (let index = 0; index < types.length; index++) {
        density[index] = kde(data2.filter((d) => {
          return d.type === types[index];
        })
          .map((d) => {
            return d.value;
          }));
      }
      console.log(this.paths);
      // update the chart
      this.paths.forEach((path, index) => {
        path.datum(density[index])
        .transition()
        .duration(1000)
        .attr('d', d3.line()
            .curve(d3.curveBasis)
            .x((d) => this.x(d[0]) + this.margin.left)
            .y((d) => this.y(d[1]) + this.margin.top));
      });
    }
  }
  /*mapping of type to selectionbox - returns true if the type is currently selected*/
  public isSelected(type)
  {   
      if(this.selectedLevel.num == -1)//selected All
          return true;
      if(type == 1 && this.selectedLevel.num == 0)//selected temperature
          return true;
      if(type == 2 && this.selectedLevel.num == 1)//selected CO2
          return true;
      return false;
  }
  
}
