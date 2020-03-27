/* (c) https://github.com/MontiCore/monticore */
import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleLayoutGenComponentTOP } from '@targetgui/beispiele-layout-gen.component/beispiele-layout-gen.component-top';

@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-layout-gen.component/beispiele-layout-gen.component.html',
})
export class BeispieleLayoutGenComponent extends BeispieleLayoutGenComponentTOP {

  constructor(
    _router: Router,
    _route: ActivatedRoute,
    _commandRestService: CommandRestService,
  ) {
    super(_commandRestService, _route, _router);
  }

}
