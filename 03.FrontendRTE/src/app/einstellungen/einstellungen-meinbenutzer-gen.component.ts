import { Component, OnInit } from '@angular/core';
import { TextFormControl } from '@shared/generic-form/controls';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { ActivatedRoute, Router } from '@angular/router';
import { UsernameReset } from 'target/generated-sources/commands/usernamereset';
import { IDTO } from '@shared/architecture';
import { AuthService } from '@shared/auth/auth.service';
import { OkDTO } from '@shared/architecture/command/aggregate/ok.dto';
import { NotificationService } from '@shared/utils';
import { DialogCallbackTwo } from '@shared/utils/dialog/dialog.callback';
import { AbstractControl, ValidationErrors } from '@angular/forms';
import {SetNewPassword} from "@commands/setnewpassword";
import {MeinBenutzerPasswortZuruecksetzenDTO} from "@targetdtos/meinbenutzerpasswortzuruecksetzen.dto";
import { EinstellungenMeinbenutzerGenComponentTOP } from "@targetgui/einstellungen-meinbenutzer-gen.component/einstellungen-meinbenutzer-gen.component-top";

@Component({
  templateUrl: '../../../target/generated-sources/gui/einstellungen-meinbenutzer-gen.component/einstellungen-meinbenutzer-gen.component.html',
  styleUrls: ['../shared/components/guidsl/data-table/data-table.component.scss']
})
export class EinstellungenMeinbenutzerGenComponent extends EinstellungenMeinbenutzerGenComponentTOP implements OnInit {

  public constructor(
    protected _router: Router,
    protected _route: ActivatedRoute,
    protected _commandRestService: CommandRestService,
    private auth: AuthService,
    private notification: NotificationService) {
    super(_router, _route, _commandRestService)
  }

  cancelSave_username() {
    this.edit_usernameMode = false;
  }

  edit_username() {
    this.edit_usernameMode = true;
    this._usernameControl.setValue(this._uit_username);
  }

  public save_username() {
    let oldUsername = this._uit_username;
    let newUsername = this._usernameControl.value;
    if (this._usernameControl.invalid) {
      return;
    }
    if (newUsername === oldUsername) {
      this.cancelSave_username();
      return;
    }
    this.edit_usernameMode = false;
    this._uit_username = newUsername;
    this.notification.notificationYesNo(
      'Anmeldung erforderlich',
      'Möchten sie wirklich ihren _usernamen zu "'
      + newUsername
      + '" ändern? Bei der Änderung werden sie abgemeldet'
      + ' und müssen sich mit dem neuen _usernamen wieder anmelden.',
      new DialogCallbackTwo(() => {
        new Promise((resolve, reject) => {
          this.commandManager.addCommand(new UsernameReset(oldUsername, newUsername), (dto: IDTO) => {
            if (dto instanceof OkDTO) {
              resolve(dto);
            } else {
              reject('Should be of type IdDTO, but was ' + dto.constructor.name);
            }
          });
        }).then(_ => {
          this.auth.logout();
          this._router.navigateByUrl('/auth/login');
        }).catch(err => {
          console.error(err);
        });
        this.commandManager.sendCommands();
      }, () => {
        this._uit_username = oldUsername;
      })
    );
  }

  ngOnInit() {
    this._usernameControl = new TextFormControl();
    this._usernameControl.setValidators(
      (control: AbstractControl): ValidationErrors | null => {
        if (control.value !== null && control.value.toString().length > 1) {
          if (!control.value || this._uit_username === control.value) {
            return null;
          }
          if (!this.uit.existingUsernames.includes(control.value)) {
            return null;
          }
          return { 'error': 'Der angegebene Name wird bereits verwendet' };
        }
        return { 'error': 'Min. 2 Zeichen' };
      });

    this._upwd_neuesPasswortControl.setValidators((control: AbstractControl): ValidationErrors | null => {
      if (control.value.length < 5)
        return { 'error': 'Min. 5 Zeichen'};

      return null;
    });

    this._upwd_neuesPasswortZweiControl.setValidators((control: AbstractControl): ValidationErrors | null => {
      if (control.value.length < 5)
        return { 'error': 'Min 5 Zeichen'};

      if (control.value !== this._upwd_neuesPasswortControl.value)
        return { 'error': 'Beide Passwörter müssen übereinstimmen.'};

      return null;
    });

    super.ngOnInit();
  }
  public passwordReset() {
    this._upwd_neuesPasswortControl.validate();
    this._upwd_neuesPasswortControl.markAsDirty();
    this._upwd_neuesPasswortZweiControl.validate();
    this._upwd_neuesPasswortZweiControl.markAsDirty();

    if (this._upwd_neuesPasswortControl.valid  && this._upwd_neuesPasswortZweiControl.valid) {
      this.commandManager.addCommand(new SetNewPassword(this._upwd_altesPasswortControl.value, this._upwd_neuesPasswortControl.value), (dto: IDTO) => {
        if (dto instanceof OkDTO) {
          this.notification.notificationOkOnly('Passwort zurücksetzen', 'Ihr Passwort wurde zurückgesetzt')
          this._upwd_altesPasswortControl.reset();
          this._upwd_neuesPasswortControl.reset();
          this._upwd_neuesPasswortZweiControl.reset();
        } else {
          this.notification.notificationOkOnly('Passwort zurücksetzen', 'Es ist ein Fehler aufgetreten, bitte überprüfen sie ihre Eingaben.')
        }
      })
      this.commandManager.sendCommands();
    }
    else{
      this.notification.notificationOkOnly('Passwort zurücksetzen', 'Das beiden neuen Passwörter waren nicht gleich oder zu kurz (5 Zeichen min.)')
    }
  }

  public initMeinBenutzerPasswortZuruecksetzenDTOupwd() {
    this.upwd = new MeinBenutzerPasswortZuruecksetzenDTO();
    this.init_upwd_altesPasswortControl();
    this.init_upwd_neuesPasswortControl();
    this.init_upwd_neuesPasswortZweiControl();
  }

}
