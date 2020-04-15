
import {Component, OnDestroy, OnInit, ViewChild, HostListener} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CommandRestService} from '@shared/architecture/command/rte/command.rest.service';
import {CommandManager} from '@shared/architecture/command/rte/command.manager';
import {ErrorDTO} from '@shared/architecture/command/aggregate/error.dto';
import {PermissionFlags, Token} from "@shared/auth/token";
import {DownloadFileService} from '@shared/utils/download-file.service';
import {TableColumn} from '@shared/components/guidsl/data-table/data-table.component.ts';
import {CopyToClipboardService, getDisplayedRow} from '@shared/utils/copy-to-clipboard.service';
import {NotificationService} from '@shared/utils';
import {TextFormControl} from '@shared/generic-form/controls';
import {InfoComponentTOP} from '@targetgui/info.component/info.component-top';
import {AlarmCtrlForm} from '@targetgui/info.component/alarmctrl.form';
import {EinstellungenRollenTabelleEntryDTO_delete} from "@commands/einstellungenrollentabelleentrydto_delete";
import {DTO} from "@shared/architecture";
import {EinstellungenRollenTabelleEntryDTO_update} from "@commands/einstellungenrollentabelleentrydto_update";
import {IdDTO} from "@shared/architecture/command/aggregate/id.dto";
import {EinstellungenRollenTabelleEntryDTO_create} from "@commands/einstellungenrollentabelleentrydto_create";
import {EinstellungenRollenTabelleEntryDTO} from "@targetdtos/einstellungenrollentabelleentry.dto";
import {ObjectListEntryDTO} from "@targetdtos/objectlistentry.dto";
import {UserListEntryDTO} from "@targetdtos/userlistentry.dto";
import {RoleListEntryDTO} from "@targetdtos/rolelistentry.dto";
import {SirenCtrlDTO} from "@targetdtos/sirenctrl.dto";
import {SirenCtrl_update} from "@commands/sirenctrl.update";
import {SirenCtrlFullDTO} from "@targetdtos/sirenctrl.fulldto";

@Component({
  templateUrl: '../../../target/generated-sources/gui/info.component/info.component.html',
  providers: [AlarmCtrlForm]
})
export class InfoComponent extends InfoComponentTOP {

  public constructor(
    protected _notificationService: NotificationService,
    protected _dfs: DownloadFileService,
    protected _ctc: CopyToClipboardService,
    protected _router: Router,
    protected _route: ActivatedRoute,
    protected _commandRestService: CommandRestService,
    form: AlarmCtrlForm) {
    super(_notificationService, _dfs, _ctc, _router, _route, _commandRestService);
    this.form = form
  }

  public onSaveSiren(event) {
    let fullDto = this.getFormFullDTO(event.entry);
    console.log('submit', JSON.stringify(fullDto));


    this.commandManager.addCommand(new SirenCtrl_update(fullDto), (dto: IdDTO) => {
      console.log('model: ', fullDto);
      this.form.reset();
      this.commandManager = new CommandManager(this._commandRestService);
      this.initAllCommands();
    });

    this.sendCommands((err: ErrorDTO) => {
      console.log('An error occured on update EinstellungenBenutzerTabelleEntryDTO:', err);
    });
  }

  public onEditSirenenSteuerungTable(event) {
    let model = event;
    this.form.setModel(model);
    this.form.setValues(model);
    this.form._enabled.setValue(event.enabled);
    this.form._sirenOverride.setValue(event.sirenOverride);
  }

  public activeSirenRow(row) {
    return true;
  }

  public getFormFullDTO(data): SirenCtrlFullDTO {
    let fullDto = new SirenCtrlFullDTO(data);
    return fullDto;
  }

  public log(event: any): void {
    console.log(event);
  }

  public copy(event: any): void {
    this._ctc.copyToClipboard(getDisplayedRow(event));
  }
}
