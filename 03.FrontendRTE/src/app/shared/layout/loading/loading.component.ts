/* (c) https://github.com/MontiCore/monticore */

import { Input, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { LoadingService } from './loading.service';

export abstract class LoadingComponent implements OnDestroy {

  @Input() set disabled(value: boolean) {
    this.updateState(this._active, value);
  }

  public get active() {
    return this._active && !this._disabled;
  }

  public state: string = 'inactive';

  private _loadingStatusSubscription: Subscription;
  private _disabled = false;
  private _active   = false;

  protected constructor(private _loading: LoadingService) {
    this._loadingStatusSubscription =
      this._loading.status.subscribe((status: boolean) => this.updateState(status, this._disabled));
  }

  public ngOnDestroy(): void {
    this._loadingStatusSubscription.unsubscribe();
  }

  private updateState(active, disabled) {
    this._active   = active;
    this._disabled = disabled;
    this.state     = this._active && !this._disabled ? 'active' : 'inactive';
  }
}
