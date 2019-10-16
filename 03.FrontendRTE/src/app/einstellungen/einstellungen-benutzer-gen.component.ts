/* (c) https://github.com/MontiCore/monticore */
import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ErrorDTO } from '@shared/architecture/command/aggregate/error.dto';
import { EinstellungenBenutzerTabelleEntryDTO } from '@targetdtos/einstellungenbenutzertabelleentry.dto';
import { EinstellungenBenutzerForm } from './einstellungenbenutzer.form';
import { CommandManager } from '@shared/architecture/command/rte/command.manager';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { IdDTO } from "@shared/architecture/command/aggregate/id.dto";
import { EinstellungenBenutzerTabelleEntryDTO_create } from "@commands/einstellungenbenutzertabelleentrydto_create";
import { EinstellungenBenutzerTabelleEntryDTO_update } from "@commands/einstellungenbenutzertabelleentrydto_update";
import { EinstellungenBenutzerTabelleDTO } from "@targetdtos/einstellungenbenutzertabelle.dto";
import {DownloadFileService} from "@shared/utils/download-file.service";
import {CopyToClipboardService} from "@shared/utils/copy-to-clipboard.service";
import {NotificationService} from "@shared/notification/notification.service";
import {DomainUserAktivierungsmailSenden_byIds} from "@commands/domainuseraktivierungsmailsenden_byids";
import {DomainUserAktivierungsmailSenden_byStatus} from "@commands/domainuseraktivierungsmailsenden_bystatus";
import {OkDTO} from "@shared/architecture/command/aggregate/ok.dto";
import {IDTO} from "@shared/architecture";
import {DialogCallbackTwo} from "@shared/utils/dialog/dialog.callback";
import {ISelectOptions} from "@shared/generic-form/generic-form/decoretors/options";
import {StatusListDTO} from '@targetdtos/statuslist.dto';
import { EinstellungenBenutzerGenComponentTOP } from "@targetgui/einstellungen-benutzer-gen.component/einstellungen-benutzer-gen.component-top";

@Component({
  templateUrl: '../../../target/generated-sources/gui/einstellungen-benutzer-gen.component/einstellungen-benutzer-gen.component.html',
  providers: [EinstellungenBenutzerForm]
})

export class EinstellungenBenutzerGenComponent extends EinstellungenBenutzerGenComponentTOP {

  public form: EinstellungenBenutzerForm;

  public constructor(
    form: EinstellungenBenutzerForm,
    protected _notificationService: NotificationService,
    protected _router: Router,
    protected _route: ActivatedRoute,
    protected _commandRestService: CommandRestService,
    protected _dfs: DownloadFileService,
    protected _ctc: CopyToClipboardService) {
    super(_notificationService,_dfs,_ctc,_router, _route, _commandRestService);
    this.commandManager = new CommandManager(this._commandRestService);
    this.form = form;
  }

  public ngOnInit(): void {
    super.ngOnInit();
    this.form.initializeValues();
  }

  public initStatusListDTOsl() {
      StatusListDTO.getAll( this.commandManager)
          .then((model: StatusListDTO) => {
              this.sl = model;
              this._sl_nameControl.setOptions(this.sl.status.map((r) =>{
                  return{
                      value: r.id+'',
                      option: r.name
                  } as ISelectOptions;
              }))
          })

  }

    public initEinstellungenBenutzerTabelleDTOut() {
    EinstellungenBenutzerTabelleDTO.getAll(this.commandManager)
      .then((model: EinstellungenBenutzerTabelleDTO) => {
        this.ut = model;
        this.init_ut_alleBenutzerTable();
      })
  }

  public onEditBenutzerTabelle(event) {
    let model = event;
    this.form.setModel(model);
    this.form.setValues(model);
  }

  public onSave(event) {

    let fullDto = this.getFormFullDTO(event.entry);
    console.log('submit', JSON.stringify(fullDto));


    this.commandManager.addCommand(new EinstellungenBenutzerTabelleEntryDTO_update(fullDto), (dto: IdDTO) => {
      console.log('model: ', fullDto);
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

    this.commandManager.addCommand(new EinstellungenBenutzerTabelleEntryDTO_create(fullDto), (dto: IdDTO) => {
      console.debug('updating table', fullDto);

      fullDto.id = dto.id;

      this.form.reset();

      this._ut_alleBenutzer.splice(1, 0, fullDto);
      this.commandManager = new CommandManager(this._commandRestService);
      this.initAllCommands();
    });

    this.sendCommands();
  }

  public getFormFullDTO(data): EinstellungenBenutzerTabelleEntryDTO {
    let fullDto = new EinstellungenBenutzerTabelleEntryDTO(data);

    return fullDto;
  }

  public sendActivationMail($event) {
   const id= $event.item.model.id;
      this._notificationService.notificationYesNo('Aktivierungsmail Senden','Möchten sie die Mail wirklich verschicken?', new DialogCallbackTwo(() =>{
          this.commandManager.addCommand(new DomainUserAktivierungsmailSenden_byIds( [id]), (dto:IDTO) => {

              if(dto instanceof OkDTO) {
                  this.form.reset();
                  this.initAllCommands();
                  //TODO @PS die Tabelle bekommt die neunen Daten  soll diese auch laden
              }
              else{

              }
          });
          this.sendCommands();
          },() =>{}
      ));


  }

  public sendActivationMailbyStatus() {
      console.log(this._sl_nameControl);
      let amountMails = this.ut.alleBenutzer.filter( a => a.aktivierungsstatus === this._sl_nameControl.options[this._sl_nameControl.value].option).length;
      this._notificationService.notificationYesNo('Aktivierungsmail Senden','Möchten sie die ' + amountMails +' Mails wirklich verschicken?', new DialogCallbackTwo(() =>{
          this.commandManager.addCommand(new DomainUserAktivierungsmailSenden_byStatus (this._sl_nameControl.options[this._sl_nameControl.value].option), (dto:IDTO) => {

              if (dto instanceof OkDTO) {
                  this.form.reset();
                  this.initAllCommands();
                  //TODO @PS die Tabelle bekommt die neunen Daten  soll diese auch laden
              } else {

              }
          });
          this.sendCommands();
      },() =>{}
      ));

  }


  protected init_ut_alleBenutzerTable() {
    this._ut_alleBenutzer = this.ut.alleBenutzer;

    this.form._username.placeholder = "Benutzername";
    this.form._initials.placeholder = "Kürzel";
    this.form._email.placeholder = "E-mail Adresse";
    this.form._istAktiv.placeholder = "Account aktiv";

    this.form.elements = this._ut_alleBenutzer;

  }

  public activeRow(row: EinstellungenBenutzerTabelleEntryDTO) {
    return row.istAktiv
  }

}
