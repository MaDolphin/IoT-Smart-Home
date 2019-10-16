/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';

@Component(
  {
    templateUrl: './auth.component.html',
    styleUrls:   ['./auth.component.scss'],
  }
)
export class AuthComponent {

  constructor() {
  }

  public isNotSupportedBrowser() {
    return !(!window['opr'] && navigator['vendor'] === 'Google Inc.') && !(typeof window['InstallTrigger'] !== 'undefined');
  }

}
