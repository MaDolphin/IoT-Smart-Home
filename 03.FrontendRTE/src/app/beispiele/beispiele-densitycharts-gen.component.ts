 /*(c) https://github.com/MontiCore/monticore */
import {Component, OnInit, QueryList, ViewChildren} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { DensityChartComponent } from "@components/charts/density-chart/density-chart.component";
import {CommandRestService} from '@shared/architecture/command/rte/command.rest.service';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Data2Model } from '../data/data.model';
import {BeispieleDensitychartGenComponent} from '@targetgui/beispiele-densitychart-gen.component/beispiele-densitychart-gen.component-top';
 import {WebSocketService} from "@services/websocket.service";
@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-densitychart-gen.component/beispiele-densitychart-gen.component.html',
})
export class BeispieleDensitychartsGenComponent extends BeispieleDensitychartGenComponent {
  data2: Observable<Data2Model>;

  constructor(private http: HttpClient,
        protected _webSocketService: WebSocketService,
        protected _router: Router,
        protected _route: ActivatedRoute,
        protected _commandRestService: CommandRestService) {
        
	super(_commandRestService, _route, _router, _webSocketService);
  }
    title = 'Densitychart';

  public subscribedataSocket(): void {
	if(this.dataSocket) {
		this.subscriptions.push(this.dataSocket.subscribe(message => {
			this.data2 = this.http.get<Data2Model>('./assets/data2.json');
		},err => console.error(err))); 
	}else{
		console.error('Socket is not initialied. initialize socket in component constructor');
	}
  }

}
