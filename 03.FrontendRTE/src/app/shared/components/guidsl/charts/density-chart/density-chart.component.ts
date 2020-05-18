import { Component, ElementRef, Input, OnChanges, ViewChild, ViewEncapsulation, HostListener } from '@angular/core';
import * as d3 from 'd3';

import {Data2Model, DataModel} from 'src/app/data/data.model';
@Component({
  selector: 'density-chart',
  templateUrl: './density-chart.component.html'})
export class DensityChartComponent implements OnChanges {
  @ViewChild('my_dataviz')
  private chartContainer: ElementRef;

  @Input()
  data2: Data2Model[];
  @Input() data;

  margin = {top: 40, right: 700, bottom: 30, left: 100};

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

   // select chart from html and append an svg
    const svg = d3.select(densityElement).append('svg')
      .attr('width', densityElement.offsetWidth)
      .attr('height',  densityElement.offsetHeight);

    const width = densityElement.offsetWidth - this.margin.left - this.margin.right;
    const height = densityElement.offsetHeight - this.margin.top - this.margin.bottom;

    const g = svg.append('g')
      .attr('transform', 'translate(' + this.margin.left + ',' + this.margin.top + ')');

    const x = d3
      .scaleLinear()
      .domain([-10, 45])
      .range([0, width]);

    g.append('g')
      .attr('transform', 'translate(0,' + height + ')')
      .call(d3.axisBottom(x));

    const y = d3
      .scaleLinear()
      .range([height, 0])
      .domain([0, 0.12]);

    g.append('g')
      .call(d3.axisLeft(y));
    // Compute kernel density estimation
    const kde = this.kernelDensityEstimator(this.kernelEpanechnikov(7 as number), x.ticks(60));
    // get types
    const color = [];
    const types = [];
    kde(data
      .filter((d) => {
        if (types.indexOf(d.type) === -1) {
          types.push(d.type);
          color.push('#' + (0xd95d80 + (1 / types.length) * 0xfffff0).toString(16).substr(1, 6));
        }
      }));
    // iterate through all data and plot it
    const density = [];
    for (let index = 0; index < types.length; index++) {
      density[index] = kde(data.filter((d) => {
        return d.type === types[index];
      })
        .map((d) => {
          return d.value;
        }));

      // Plot the area
      svg.append('path')
        .datum(density[index])
        .attr('fill', color[index])
        .attr('opacity', '.6')
        .attr('stroke', '#000')
        .attr('stroke-width', 1)
        .attr('stroke-linejoin', 'round')
        .attr('d', d3.line()
          .curve(d3.curveNatural)
          .x((d) => x(d[0]) + this.margin.left)
          .y((d) => y(d[1]) + this.margin.top)
        );
      // legend
      svg.append('circle').attr('cx', 700).attr('cy', 30 * index + 20).attr('r', 6).style('fill', color[index]);
      svg.append('text').attr('x', 720).attr('y', 30 * index + 20).text(types[index])
        .style('font-size', '14px').attr('alignment-baseline', 'middle');
    }
    svg.append('text')
      .attr('text-anchor', 'end')
      .attr('x', width + this.margin.left - 330)
      .attr('y', height + this.margin.top + 25)
      .text('Temperatur');
    svg.append('text')
      .attr('text-anchor', 'end')
      .attr('transform', 'rotate(-90)')
      .attr('y', this.margin.left - 50)
      .attr('x', this.margin.top - 80)
      .text('Epanechnikov');
  }
  private kernelDensityEstimator(kernel, X) {
    return (V) => {
      return X.map((x) => {
        return [x, d3.mean(V, (v) => kernel(x - (v as number))) ];
      });
    };
  }

  private kernelEpanechnikov(k) {
    return (v) => {
      return Math.abs(v /= k) <= 1 ? 0.75 * (1 - v * v) / k : 0;
    };
  }
}
