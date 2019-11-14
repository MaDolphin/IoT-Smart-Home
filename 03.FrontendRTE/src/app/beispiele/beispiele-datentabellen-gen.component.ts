import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleDatentabellenGenComponentTOP } from '@targetgui/beispiele-datentabellen-gen.component/beispiele-datentabellen-gen.component-top';
import { CommandManager } from "@shared/architecture/command/rte/command.manager";
import { RoleForm } from "@targetgui/beispiele-datentabellen-gen.component/role.form";

@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-datentabellen-gen.component/beispiele-datentabellen-gen.component.html',
  providers: [RoleForm] // necessary for 'editierbareTabelle'
})
export class BeispieleDatentabellenGenComponent extends BeispieleDatentabellenGenComponentTOP implements OnInit {

  constructor(
    form: RoleForm,
    _router: Router,
    _route: ActivatedRoute,
    _commandRestService: CommandRestService,
  ) {
    super(_router, _route, _commandRestService);
    this.commandManager = new CommandManager(this._commandRestService);
    this.form = form;
  }

  //region Editierbare Tabelle
  protected init_rt_usersTable() {
    this._rt_users = this.rt.users;

    // setting options
    this.form_user_usernameOptions = this.ul.users;
    this.form_role_nameOptions = this.rl.roles;
  }

  public onEditEditierbareTabelle(event) {
    let model = event;

    this.form.setModel(model);
    this.form.setValues(model);

    this.form._role_name.setValue(event.role.id);
    this.form._user_username.setValue(event.user.id);
  }
  //endregion

}
