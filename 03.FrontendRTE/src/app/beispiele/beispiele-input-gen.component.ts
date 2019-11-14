import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleInputGenComponentTOP } from '@targetgui/beispiele-input-gen.component/beispiele-input-gen.component-top';

@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-input-gen.component/beispiele-input-gen.component.html',
})
export class BeispieleInputGenComponent extends BeispieleInputGenComponentTOP implements OnInit {

  constructor(
    _router: Router,
    _route: ActivatedRoute,
    _commandRestService: CommandRestService,
  ) {
    super(_router, _route, _commandRestService);
  }

  ngOnInit() {
  }

}
