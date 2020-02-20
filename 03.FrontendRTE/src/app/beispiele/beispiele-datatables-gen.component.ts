import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { CommandManager } from "@shared/architecture/command/rte/command.manager";
import { RoleForm } from "@targetgui/beispiele-datatables-gen.component/role.form";
import { NotificationService } from "@shared/notification/notification.service";
import { DownloadFileService } from "@shared/utils/download-file.service";
import { CopyToClipboardService } from "@shared/utils/copy-to-clipboard.service";
import { BeispieleDatatablesGenComponentTOP } from "@targetgui/beispiele-datatables-gen.component/beispiele-datatables-gen.component-top";

@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-datatables-gen.component/beispiele-datatables-gen.component.html',
  providers: [RoleForm] // necessary for 'editierbareTabelle'
})
export class BeispieleDatatablesGenComponent extends BeispieleDatatablesGenComponentTOP implements OnInit {

  constructor(
    _notificationService: NotificationService, // necessary for context menu
    _dfs: DownloadFileService, // necessary for context menu
    _ctc: CopyToClipboardService, // necessary for context menu
    form: RoleForm,
    _router: Router,
    _route: ActivatedRoute,
    _commandRestService: CommandRestService,
  ) {
    super(_commandRestService, _ctc, _dfs, _notificationService, _route, _router);
    this.commandManager = new CommandManager(this._commandRestService);
    this.form = form;
  }

  // you can override the table columns initialization
  public initeinfacheTabelleColumns(): void {
    super.initeinfacheTabelleColumns();
    this.einfacheTabelleColumns[0].name = "Benutzername (HWC)";
  }

  //region Editierbare Tabelle
  protected init_rt_usersTable() {
    super.init_rt_usersTable();

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

  //region Tabelle mit Context Menu
  public getString() {
    return "Anderer Methodenname ";
  }

  public isSomething() {
    return true; // hwc logic belongs here...
  }
  //endregion

}
