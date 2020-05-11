/* (c) https://github.com/MontiCore/monticore */
import {Component} from '@angular/core';
import {WebSocketService} from '@shared/architecture/services/websocket.service';
import {ActivatedRoute, Router} from '@angular/router';
import {CommandRestService} from '@shared/architecture/command/rte/command.rest.service';
import {BeispieleGaugechartsGenComponentTOP} from '@targetgui/beispiele-gaugecharts-gen.component/beispiele-gaugecharts-gen.component-top';

@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-gaugecharts-gen.component/beispiele-gaugecharts-gen.component.html',
})
export class BeispieleGaugechartsGenComponent extends BeispieleGaugechartsGenComponentTOP {

  public constructor(
    protected _webSocketService: WebSocketService,
    protected _router: Router,
    protected _route: ActivatedRoute,
    protected _commandRestService: CommandRestService) {
    super(_commandRestService, _route, _router/*, _webSocketService*/);
  }

}
