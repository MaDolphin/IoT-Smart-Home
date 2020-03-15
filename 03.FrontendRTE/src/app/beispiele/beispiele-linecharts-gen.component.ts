import {Component} from '@angular/core';
import {WebSocketService} from '@shared/architecture/services/websocket.service';
import {ActivatedRoute, Router} from '@angular/router';
import {CommandRestService} from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleLinechartsGenComponentTOP } from "@targetgui/beispiele-linecharts-gen.component/beispiele-linecharts-gen.component-top";

@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-linecharts-gen.component/beispiele-linecharts-gen.component.html',
})
export class BeispieleLinechartsGenComponent extends BeispieleLinechartsGenComponentTOP {

  public constructor(
    protected _webSocketService: WebSocketService,
    protected _router: Router,
    protected _route: ActivatedRoute,
    protected _commandRestService: CommandRestService) {
    super(_commandRestService, _route, _router, _webSocketService);
  }

  public isImageVisible(): boolean {
    // show image if there is data about some object
    return this.lineChartData.some(group => group.label !== undefined);
  }

  public getImageSource() {
    // return image url
    return 'assets/img/rwth_se_rgb.png';
  }
}