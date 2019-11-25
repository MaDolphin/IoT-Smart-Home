/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CommandRestService} from '@shared/architecture/command/rte/command.rest.service';
import {PaperComponentTOP} from '@targetgui/paper.component/paper.component-top';

@Component({
  templateUrl: '../../../target/generated-sources/gui/paper.component/paper.component.html',
})
export class PaperComponent extends PaperComponentTOP {

  charts: any;

  colorschart1LC = [];
  colorschart2LC = [];
  colorschart3LC = [];

  chart1LC: any = [];
  chart2LC: any = [];
  chart3LC: any = [];

  public constructor(
    protected _router: Router,
    protected _route: ActivatedRoute,
    protected _commandRestService: CommandRestService) {
    super(_router, _route, _commandRestService);
  }

  ngOnInit() {
    this.chart1LC = dummy1;
    this.chart2LC = dummy2;
    this.chart3LC = dummy3;
    this.charts = {
      chart1BB: 0,
      chart2BB: 0,
      chart3BB: 0,
      chart4BB: 35,
      chart5BB: 65,
      chart6BB: 100,
    };
  }

}

const dummy1 = [
  {
    label: 'Hydraulic',
    data: [{
      x: 0,
      y: 0
    },
      {
        x: 3,
        y: 600
      },
      {
        x: 5,
        y: 600
      },
      {
        x: 5.1,
        y: 300
      },
      {
        x: 18,
        y: 300
      },
      {
        x: 20,
        y: 0
      }]
  }
]

const dummy2 = [
  {
    label: 'Screw Tip',
    data: [{
      x: 0,
      y: 0
    },
      {
        x: 0.1,
        y: 250
      },
      {
        x: 5,
        y: 590
      },
      {
        x: 5.1,
        y: 300
      },
      {
        x: 18,
        y: 300
      },
      {
        x: 20,
        y: 0
      }]
  }
]

const dummy3 = [
  {
    label: 'Pressure close to Sprue',
    data: [{
      x: 0,
      y: 0
    },
    {
      x: 5,
      y: 400
    },
    {
      x: 20,
      y: 0
    }]
  },
  {
    label: 'Pressure far from Sprue',
    data: [{
      x: 2,
      y: 0
    },
    {
      x: 3,
      y: 300
    },
    {
      x: 10,
      y: 0
    }]
  }
]