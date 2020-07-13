/*(c) https://github.com/MontiCore/monticore */
import {Component, OnInit, QueryList, ViewChildren} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CommandRestService} from '@shared/architecture/command/rte/command.rest.service';
import {WebSocketService} from "@services/websocket.service";
import {BeispieleDensityGaugeDashboardGenComponentTOP} from "@targetgui/beispiele-density-gauge-dashboard-gen.component/beispiele-density-gauge-dashboard-gen.component-top";

@Component({
    templateUrl: '../../../target/generated-sources/gui/beispiele-density-gauge-dashboard-gen.component/beispiele-density-gauge-dashboard-gen.component.html',
})
export class BeispieleDensityGaugeDashboardGenComponent extends BeispieleDensityGaugeDashboardGenComponentTOP {
    constructor(protected _webSocketService: WebSocketService,
                protected _router: Router,
                protected _route: ActivatedRoute,
                protected _commandRestService: CommandRestService) {

        super(_commandRestService, _route, _router, _webSocketService);
    }

    title = 'Density & Gaugechart';
    public densitysensordata;
    public sensorType;
    public _sensorTypeControl;

    ngOnInit() {
        super.ngOnInit();
        this.initDropDown();
    }

    private initDropDown() {
        this._sensorTypeControl.setOptions([
            'All',
            "Temperature",
            "CO2"
        ]);
        this._sensorTypeControl.defaultValue = 'Temperature';
        this.sensorType = this._sensorTypeControl.defaultValue;

        this._sensorTypeControl.valueChanges.subscribe(value => {
            //console.log(v);
            this.sensorType = value;
        })
    }
}
