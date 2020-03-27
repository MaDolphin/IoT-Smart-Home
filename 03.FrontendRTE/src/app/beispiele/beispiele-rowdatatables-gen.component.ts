/* (c) https://github.com/MontiCore/monticore */
import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleRowdatatablesGenComponentTOP } from "@targetgui/beispiele-rowdatatables-gen.component/beispiele-rowdatatables-gen.component-top";
import { Logger } from "@upe/logger";

@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-rowdatatables-gen.component/beispiele-rowdatatables-gen.component.html',
})
export class BeispieleRowdatatablesGenComponent extends BeispieleRowdatatablesGenComponentTOP {

  private logger: Logger = new Logger({ name: 'BeispieleRowdatatablesGenComponent' });

  constructor(
    _router: Router,
    _route: ActivatedRoute,
    _commandRestService: CommandRestService,
  ) {
    super(_commandRestService, _route, _router);
  }

  cancelSave_username() {
    this.edit_usernameMode = false;
  }

  edit_username() {
    this.edit_usernameMode = true;
    this._usernameControl.setValue(this._uit_username);
  }

  save_username(): void {
    this.edit_usernameMode = false;
    this.logger.info("Communication with backend must be written by hand currently.")
  }

}
