/*(c) https://github.com/MontiCore/monticore */
import {Component, OnInit, QueryList, ViewChildren} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { DensityChartComponent } from "@components/charts/density-chart/density-chart.component";
import {CommandRestService} from '@shared/architecture/command/rte/command.rest.service';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import {BeispieleDensitychartGenComponent} from '@targetgui/beispiele-densitychart-gen.component/beispiele-densitychart-gen.component';
import {WebSocketService} from "@services/websocket.service";
import { DensitySensorDataDTO } from '@densitysensordata-dto/densitysensordata.dto';

@Component({
    templateUrl: '../../../target/generated-sources/gui/beispiele-densitychart-gen.component/beispiele-densitychart-gen.component.html',
})
export class BeispieleDensitychartsGenComponent extends BeispieleDensitychartGenComponent {
    constructor(private http: HttpClient,
                protected _webSocketService: WebSocketService,
                protected _router: Router,
                protected _route: ActivatedRoute,
                protected _commandRestService: CommandRestService) {

        super(_commandRestService, _route, _router, _webSocketService);
    }
    title = 'Densitychart';
    public densitysensordata;
    
    /**
    public subscribedataSocket(): void {
        if(this.dataSocket) {
            this.subscriptions.push(this.dataSocket.subscribe(message => {
                this.densitysensordata = this.mockData();
            }, err => console.error(err)));
        } else {
            console.error('Socket is not initialied. initialize socket in component constructor');
        }
    }

    public mockData(): any[] {
        return [{"type": "Temperatur A", "value": 10+ Math.random() * 10},{"type": "Temperatur B", "value": 15 + Math.random() * 10}];
    }

**/

}
