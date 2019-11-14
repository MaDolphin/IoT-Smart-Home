import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleDatentabellenGenComponentTOP } from '@targetgui/beispiele-datentabellen-gen.component/beispiele-datentabellen-gen.component-top';

@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-datentabellen-gen.component/beispiele-datentabellen-gen.component.html',
})
export class BeispieleDatentabellenGenComponent extends BeispieleDatentabellenGenComponentTOP implements OnInit {

  constructor(
    _router: Router,
    _route: ActivatedRoute,
    _commandRestService: CommandRestService,
  ) {
    super(_router, _route, _commandRestService);
  }

}
