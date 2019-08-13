/* (c) https://github.com/MontiCore/monticore */

import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

@Injectable()
export class MainLayoutService {

  public disableLoading: BehaviorSubject<boolean> = new BehaviorSubject(false);

}
