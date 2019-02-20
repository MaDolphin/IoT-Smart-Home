import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material';
import { noop } from 'rxjs/util/noop';
import { DialogCallback } from '@shared/utils/dialog/dialog.callback';
import { GeneralDialogComponent } from '@shared/utils/dialog/general-dialog/general-dialog.component';
import { GeneralError } from '@shared/utils/general.error';

export enum CloseReason {
  Confirm = 'confirm',
  Option = 'option',
  Deny = 'deny',
}

export enum NotificationActionName {
  Yes = 'Ja',
  No = 'Nein',
  Ok = 'Ok',
  Delete = 'Löschen',
  Deactivate = 'Deaktivieren',
  Cancel = 'Abbrechen',
}

@Injectable()
export class NotificationService {
  constructor(public dialog: MatDialog) {
  }

  private openDialog<T>(data: any, width: number = 500): Promise<string> {
    const ref = this.dialog.open(GeneralDialogComponent, {width: `${width}px`, data: data});

    return new Promise<string>((resolve) => {
      ref.afterClosed().subscribe((result) => resolve(result));
    });
  }

  public async notificationYesNo(title: string, msg: string, callback: DialogCallback | undefined = undefined, errorCode: string | undefined = undefined, width: number = 500): Promise<boolean> {
    const result = await this.openDialog({
      title,
      msg,
      confirmName: NotificationActionName.Yes,
      denyName: NotificationActionName.No,
      errorCode: errorCode,
    }, width);
    if (result === CloseReason.Confirm) {
      callback ? await callback.onConfirm() : noop;
    } else {
      callback ? await callback.onDeny() : noop;
    }

    return result !== CloseReason.Deny;
  }

  public async notificationOkOnly(title: string, msg: string, callback: DialogCallback | undefined = undefined, errorCode: string | undefined = undefined, width: number = 500): Promise<boolean> {
    const result = await this.openDialog({
      title,
      msg,
      confirmName: NotificationActionName.Ok,
      errorCode: errorCode,
    }, width);

    if (result === CloseReason.Confirm) {
      callback ? await callback.onConfirm() : noop;
    } else {
      callback ? await callback.onDeny() : noop;
    }

    return result !== CloseReason.Deny;
  }

  public async notificationOkOnlyMultilines(title: string, msgs: string[], callback: DialogCallback | undefined = undefined, errorCode: string | undefined = undefined, width: number = 500): Promise<boolean> {
    const result = await this.openDialog({
      title,
      msgs,
      confirmName: NotificationActionName.Ok,
      errorCode: errorCode,
    }, width);

    if (result === CloseReason.Confirm) {
      callback ? await callback.onConfirm() : noop;
    } else {
      callback ? await callback.onDeny() : noop;
    }

    return result !== CloseReason.Deny;
  }

  public async notificationOkCancel(title: string, msg: string, callback: DialogCallback | undefined = undefined, errorCode: string | undefined = undefined, width: number = 500): Promise<boolean> {
    const result = await this.openDialog({
      title,
      msg,
      confirmName: NotificationActionName.Ok,
      denyName: NotificationActionName.Cancel,
      errorCode: errorCode,
    }, width);

    if (result === CloseReason.Confirm) {
      callback ? await callback.onConfirm() : noop;
    } else {
      callback ? await callback.onDeny() : noop;
    }

    return result !== CloseReason.Deny;
  }

  public async notificationOptions(title: string, msg: string, confirmName: string, optionName: string, denyName: string, callback: DialogCallback | undefined = undefined, errorCode: string | undefined = undefined, width: number = 500): Promise<boolean> {
    const result = await this.openDialog({
      title,
      msg,
      confirmName: confirmName,
      optionName: optionName,
      denyName: denyName,
      errorCode: errorCode,
    }, width);

    if (result === CloseReason.Confirm) {
      callback ? await callback.onConfirm() : noop;
    } else if (result === CloseReason.Option) {
      callback ? await callback.onOption() : noop;
    } else {
      callback ? await callback.onDeny() : noop;
    }

    return result !== CloseReason.Deny;
  }

  public notYetAvailable() {
    this.notificationOkOnly('Information', 'Diese Funktion ist noch nicht verfügbar.', undefined, 'MAF0xDEAD');
  }

  public async error(error: GeneralError): Promise<boolean> {
    return this.notificationOkOnly(error.title(),
      'Ein Systemfehler ist aufgetreten, bitte versuchen Sie es erneut.', undefined, error.code());
  }

  public async errorWithMessage(error: GeneralError, msg?: string): Promise<boolean> {
    return this.notificationOkOnly(error.title(),
      msg ? msg : error.messageOnly(), undefined, error.code());
  }

}
