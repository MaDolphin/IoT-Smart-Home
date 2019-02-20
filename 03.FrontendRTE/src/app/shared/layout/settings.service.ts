import { Injectable } from '@angular/core';
import 'rxjs/add/operator/debounceTime';
import { Subject } from 'rxjs/Rx';

@Injectable()
export class SettingsService {

  store: any;
  private subject: Subject<any>;
  private _autoLock: boolean;

  constructor() {
    this.subject  = new Subject();
    this.store    = {};
    this.autoLock = false;
  }

  public set autoLock(value: boolean) {
    this._autoLock = value;
  }

  public get autoLock(): boolean {
    return this._autoLock;
  }

  subscribe(next: (value: any) => void, err?: (error: any) => void, complete?: () => void) {
    return this.subject.subscribe(next, err, complete);
  }
}
