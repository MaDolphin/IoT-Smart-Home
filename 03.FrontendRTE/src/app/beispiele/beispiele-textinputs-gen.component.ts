import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleTextinputsGenComponentTOP } from "@targetgui/beispiele-textinputs-gen.component/beispiele-textinputs-gen.component-top";

@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-textinputs-gen.component/beispiele-textinputs-gen.component.html',
})
export class BeispieleTextinputsGenComponent extends BeispieleTextinputsGenComponentTOP implements OnInit {

  constructor(
    _router: Router,
    _route: ActivatedRoute,
    _commandRestService: CommandRestService,
  ) {
    super(_commandRestService, _route, _router);
  }

}
