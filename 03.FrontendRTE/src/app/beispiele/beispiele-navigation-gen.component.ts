import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleNavigationGenComponentTOP } from '@targetgui/beispiele-navigation-gen.component/beispiele-navigation-gen.component-top';

@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-navigation-gen.component/beispiele-navigation-gen.component.html',
})
export class BeispieleNavigationGenComponent extends BeispieleNavigationGenComponentTOP implements OnInit {

  constructor(
    _router: Router,
    _route: ActivatedRoute,
    _commandRestService: CommandRestService,
  ) {
    super(_commandRestService, _route, _router);
  }

  ngOnInit() {
  }

}
