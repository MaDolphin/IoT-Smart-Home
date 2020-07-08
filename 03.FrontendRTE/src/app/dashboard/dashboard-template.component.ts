import {AfterViewInit, OnDestroy, OnInit, Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CommandRestService} from '@shared/architecture/command/rte/command.rest.service';
import {DashboardTemplateComponentTOP} from '@targetgui/dashboard-template.component/dashboard-template.component-top';

@Component({
  templateUrl: '../../../target/generated-sources/gui/dashboard-template.component/dashboard-template.component.html'
})
export class DashboardTemplateComponent extends DashboardTemplateComponentTOP implements OnInit, OnDestroy, AfterViewInit {

  public constructor(
    protected _commandRestService: CommandRestService,
    protected _route: ActivatedRoute,
    protected _router: Router
  ) {
    super(_commandRestService, _route, _router);
  }

  public ngOnInit(): void {
    super.ngOnInit();
    this.initDropDown();
  }

  private initDropDown() {
    this._sensorTypeControl.setOptions([
      'Temperature',
      'Movement'
    ]);
    this._sensorTypeControl.defaultValue = 'Temperature';
    this._sensorTypeControl.valueChanges.subscribe(v => {
      if (v === 'Temperature') {
        console.log('temperature');
        // get data for temperature sensor, update your chart, something like
        // this.initTemperatureDataDTOtemperaturedata();
        // this.sendCommands();
      } else {
        console.log('movement');
        // get data for movement sensor, ...
      }
    })
  }

}


