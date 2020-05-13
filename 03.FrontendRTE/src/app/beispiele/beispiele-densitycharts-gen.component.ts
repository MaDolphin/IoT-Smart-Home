 (c) https://github.com/MontiCore/monticore */
import {Component} from '@angular/core';
import {WebSocketService} from '@shared/architecture/services/websocket.service';
import {ActivatedRoute, Router} from '@angular/router';
import {CommandRestService} from '@shared/architecture/command/rte/command.rest.service';
import {BeispieleDensityGenComponentTOP} from '@targetgui/beispiele-gaugecharts-gen.component/beispiele-densitycharts-gen.component-top';

@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-densitycharts-gen.component/beispiele-densitycharts-gen.component.html',
})
export class BeispieleDensitychartsGenComponent extends BeispieleDensityGenComponentTOP {

  public constructor(
    protected _webSocketService: WebSocketService,
    protected _router: Router,
    protected _route: ActivatedRoute,
    protected _commandRestService: CommandRestService) {
    super(_commandRestService, _route, _router/*, _webSocketService*/);
  }

}
