/* (c) https://github.com/MontiCore/monticore */

import { Component, OnInit } from "@angular/core";
import { DashboardComponentTOP } from "@targetgui/dashboard.component/dashboard.component-top";
import { ActivatedRoute, Router } from "@angular/router";
import { CommandRestService } from "@shared/architecture/command/rte/command.rest.service";
import { WebSocketService } from "@services/websocket.service";

@Component({
  templateUrl: '../../../target/generated-sources/gui/dashboard.component/dashboard.component.html',
})

export class DashboardComponent extends DashboardComponentTOP implements OnInit {
  protected socket;

  public constructor(
    _router: Router,
    _route: ActivatedRoute,
    _commandRestService: CommandRestService,
    protected socketService: WebSocketService) {
    super(_commandRestService, _route, _router);

    this.socket = socketService.open('None', ['Info']);
  }

  public ngOnInit(): void {
    super.ngOnInit();

    this.socket.subscribe(message => {
        console.log('received a message: ', message.data);
      }, err =>
        console.error(err)
    );
    this.socket.next("Hello World!");
  }
}
