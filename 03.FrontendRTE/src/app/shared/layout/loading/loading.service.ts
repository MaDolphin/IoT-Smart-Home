/* (c) https://github.com/MontiCore/monticore */

import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable()
export class LoadingService {

  public status: Subject<boolean> = new Subject<boolean>();
  private _active: boolean = false;

  public get active(): boolean {
    return this._active;
  }

  public set active(v: boolean) {
    this._active = v;
    this.status.next(v);
  }

  public start(): void {
    this.active = true;
  }

  public stop(): void {
    this.active = false;
  }

}
