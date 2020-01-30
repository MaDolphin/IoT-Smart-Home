/* (c) https://github.com/MontiCore/monticore */
import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CommandRestService} from '@shared/architecture/command/rte/command.rest.service';
import {CommandManager} from '@shared/architecture/command/rte/command.manager';
import {RoleForm} from '@targetgui/einstellungen-rollen-gen.component/role.form';
import {IdDTO} from "@shared/architecture/command/aggregate/id.dto";
import {ErrorDTO} from "@shared/architecture/command/aggregate/error.dto";
import {EinstellungenRollenTabelleEntryDTO_delete} from "@commands/einstellungenrollentabelleentrydto_delete";
import {DTO} from "@shared/architecture";
import {EinstellungenRollenTabelleEntryDTO_update} from "@commands/einstellungenrollentabelleentrydto_update";
import {EinstellungenRollenTabelleEntryDTO_create} from "@commands/einstellungenrollentabelleentrydto_create";
import {EinstellungenRollenTabelleEntryDTO} from "@targetdtos/einstellungenrollentabelleentry.dto";
import {ObjectListEntryDTO} from "@targetdtos/objectlistentry.dto";
import {UserListEntryDTO} from "@targetdtos/userlistentry.dto";
import {RoleListEntryDTO} from "@targetdtos/rolelistentry.dto";
import { EinstellungenRollenGenComponentTOP } from "@targetgui/einstellungen-rollen-gen.component/einstellungen-rollen-gen.component-top";

@Component({
  templateUrl: '../../../target/generated-sources/gui/einstellungen-rollen-gen.component/einstellungen-rollen-gen.component.html',
  providers: [RoleForm]
})

export class EinstellungenRollenGenComponent extends EinstellungenRollenGenComponentTOP {
  public constructor(
    form: RoleForm,
    protected _router: Router,
    protected _route: ActivatedRoute,
    protected _commandRestService: CommandRestService) {
    super(_commandRestService, _route, _router);
    this.commandManager = new CommandManager(this._commandRestService);
    this.form = form;
  }

  protected init_rt_usersTable() {
    this._rt_users = this.rt.users;


    this.form_user_usernameOptions = this.ul.users;
    this.form_role_nameOptions = this.rl.roles;

    this.form._user_username.placeholder = "Auswahl";

    this.form._role_name.placeholder = "Auswahl";

  }

  public onDeleteRollenTabelle(event) {
    console.log(event);
    this.commandManager.addCommand(new EinstellungenRollenTabelleEntryDTO_delete(event.id), (dto: DTO<any>) => {
      this.form.reset();
      this.commandManager = new CommandManager(this._commandRestService);
      this.initAllCommands();
    });

    this.sendCommands((err: ErrorDTO) => {
      console.log('An error occured on update EinstellungenBenutzerTabelleEntryDTO:', err);
    });
  }

  public onEditRollenTabelle(event) {
    let model = event;
    this.form.setModel(model);
    this.form.setValues(model);
    this.form._role_name.setValue(event.role.id);
    this.form._user_username.setValue(event.user.id);
  }

  public onSave(event) {
    let fullDto = this.getFormFullDTO(event.entry);

    this.commandManager.addCommand(new EinstellungenRollenTabelleEntryDTO_update(fullDto), (dto: IdDTO) => {
      this.form.reset();
      this.commandManager = new CommandManager(this._commandRestService);
      this.initAllCommands();
    });

    this.sendCommands((err: ErrorDTO) => {
      console.log('An error occured on update EinstellungenBenutzerTabelleEntryDTO:', err);
    });

  }

  public onCreate(event) {


    let fullDto = this.getFormFullDTO(this.form.getModelValue());

    this.commandManager.addCommand(new EinstellungenRollenTabelleEntryDTO_create(fullDto), (dto: IdDTO) => {
      console.debug('updating table', fullDto);

      fullDto.id = dto.id;

      this.form.reset();

      this.commandManager = new CommandManager(this._commandRestService);
      this.initAllCommands();
    });

    this.sendCommands();
  }

  public getFormFullDTO(data): EinstellungenRollenTabelleEntryDTO {
    let fullDto = new EinstellungenRollenTabelleEntryDTO(data);
    fullDto.object = new ObjectListEntryDTO();
    if (fullDto.object.id == null || fullDto.object.id.toString() == "") {
      fullDto.object.id = -1;
    }
    fullDto.user = new UserListEntryDTO();
    fullDto.user.id = this.form._user_username.getModelValue();
    fullDto.role = new RoleListEntryDTO();
    fullDto.role.id = this.form._role_name.getModelValue();
    return fullDto;
  }

}
