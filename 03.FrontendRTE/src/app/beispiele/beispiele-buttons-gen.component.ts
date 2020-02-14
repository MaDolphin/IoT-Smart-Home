import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleButtonsGenComponentTOP } from "@targetgui/beispiele-buttons-gen.component/beispiele-buttons-gen.component-top";

@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-buttons-gen.component/beispiele-buttons-gen.component.html',
})
export class BeispieleButtonsGenComponent extends BeispieleButtonsGenComponentTOP {

  constructor(
    _router: Router,
    _route: ActivatedRoute,
    _commandRestService: CommandRestService,
  ) {
    super(_commandRestService, _route, _router);
  }

}
