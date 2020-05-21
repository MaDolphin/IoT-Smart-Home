import { Component, ElementRef, Input, OnChanges, ViewChild, ViewEncapsulation, HostListener } from '@angular/core';
import * as d3 from 'd3';

import {Data2Model} from 'src/app/data/data.model';
@Component({
  selector: 'density-chart',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './density-chart.component.html'
})
export class DensityChartComponent implements OnChanges {
  @ViewChild('my_dataviz')
  private chartContainer: ElementRef;

  @Input()
  data2: Data2Model[];

  margin = {top: 30, right: 800, bottom: 30, left: 100};

  constructor() { }

  ngOnChanges(): void {
    if (!this.data2) { return; }

    this.createDensityChart();
  }

  onResize() {
    this.createDensityChart();
  }
  private createDensityChart(): void {

    d3.select('svg').remove();
    const densityElement = this.chartContainer.nativeElement;
    const data = this.data2;

    /**
     * select chart from html and append an svg
     */
    const svg = d3.select(densityElement).append('svg')
      .attr('width', densityElement.offsetWidth)
      .attr('height',  densityElement.offsetHeight);
    /**
     * @ignore
     */
    const width = densityElement.offsetWidth - this.margin.left - this.margin.right;
    const height = densityElement.offsetHeight - this.margin.top - this.margin.bottom;
    const g = svg.append('g')
      .attr('transform', 'translate(' + this.margin.left + ',' + this.margin.top + ')');
    /**
     * @ignore
     */
    const maxX = Math.max.apply(Math, this.data2.map((d) => d.value));
    const minX = Math.min.apply(Math, this.data2.map((d) => d.value));

    /**
     * Add the x Axis
     */
    const x = d3
      .scaleLinear()
      .domain([minX - 8, maxX + 8])
      .range([0, width]);

    /**
     * Create the horizontal lines
     */
    const makeYLines = () => d3.axisLeft(x)
      .scale(y);
    g.append('g')
      .attr('class', 'grid')
      .attr('transform', 'translate(0,' + height + ')')
      .call(d3.axisBottom(x));
    /**
     * Compute kernel density estimation
     */
    const kde = this.kernelDensityEstimator(this.kernelEpanechnikov(7 as number), x.ticks(60));

    /**
     * Gets the city names and gives each one a color
     */
    const color = [];
    const types = [];
    kde(data
      .filter((d) => {
        if (types.indexOf(d.type) === -1)
        {
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
    for (let i = 0; i < 3; i++) {
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
    const y = d3
      .scaleLinear()
      .range([height, 0])
      .domain([0, maxY + 0.02]);

    g.append('g')
      .call(d3.axisLeft(y))
      .call(makeYLines()
        .tickSize(-width)
      );
    for (let index = 0; index < types.length; index++) {
      /**
       * Plot the area
       */
      svg.append('path')
        .datum(density[index])
        .attr('fill', color[index])
        .attr('opacity', '.8')
        .attr('stroke', '#000')
        .attr('stroke-width', 1)
        .attr('stroke-linejoin', 'round')
        .attr('d', d3.line()
          .curve(d3.curveNatural)
          .x((d) => x(d[0]) + this.margin.left)
          .y((d) => y(d[1]) + this.margin.top)
        );

      /**
       * Adds the text to both axis
       */
      svg.append('text')
        .attr('text-anchor', 'end')
        .attr('x', width / 2 + this.margin.left)
        .attr('y', height + 1.9 * this.margin.top )
        .text('Temperatur');
      svg.append('text')
        .attr('text-anchor', 'end')
        .attr('transform', 'rotate(-90)')
        .attr('y', this.margin.left / 2.4)
        .attr('x', (height / 2) - this.margin.left)
        .text('Density');

      /**
       * Creates the legend
       */
      svg.append('circle').attr('cx', 750).attr('cy', 30 * index + 40).attr('r', 6).style('fill', color[index]);
      svg.append('text').attr('x', 770).attr('y', 30 * index + 40).text(types[index])
        .style('font-size', '14px').attr('alignment-baseline', 'middle');
    }
  }

  /**
   * Function to compute density
   * @param kernel The kernel
   * @param X Number of Ricks
   */
  private kernelDensityEstimator(kernel, X) {
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
  private kernelEpanechnikov(k) {
    return (v) => {
      return Math.abs(v /= k) <= 1 ? 0.75 * (1 - v * v) / k : 0;
    };
  }

}
