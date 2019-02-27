/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Injectable } from '@angular/core';

@Injectable()
export class RouterLocalService {
  private route;
  private routeParams = {};

  public setRoute(activeRoute: any) {

    if (this.route)
      this.route.params.unsubscribe();

    this.route = activeRoute;

    this.route.params.subscribe( params => {
      this.routeParams = params;
    });
  }

  public getRouteParams(attribute: string) {
    if (this.routeParams.hasOwnProperty(attribute))
      return this.routeParams[attribute];

    return undefined;
  }

}
