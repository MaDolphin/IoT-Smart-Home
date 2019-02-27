/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { OnDestroy, OnInit } from '@angular/core';
import { LoadingService } from './loading.service';

export class Loadable implements OnInit, OnDestroy {

  constructor(protected _loading: LoadingService) {
  }

  public ngOnInit(): void {
    this._loading.stop();
  }

  public ngOnDestroy(): void {
    this._loading.start();
  }
}
