/* (c) https://github.com/MontiCore/monticore */

import { Component, ViewContainerRef } from '@angular/core';
import { Logger } from '@upe/logger';
import { LoggerService } from '@loggerservice/logger.service';

@Component({
  selector: 'app-root',
  template: '<router-outlet></router-outlet>'
})
export class AppComponent {
  title = 'app works!';

  private viewContainerRef: ViewContainerRef;
  private logger = new Logger();

  public constructor(viewContainerRef: ViewContainerRef, httpLogger: LoggerService) {
    Logger.HTTP_LOGGER = httpLogger;
    // You need this small hack in order to catch application root view container ref
    this.viewContainerRef = viewContainerRef;
  }

}
