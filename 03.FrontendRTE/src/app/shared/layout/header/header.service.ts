import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Subject } from 'rxjs/Subject';

export interface IBreadcrumbTitle {
  major: string,
  minor?: string
}

@Injectable()
export class HeaderService {
  rewriteBreadCrumb: Subject<string[]> = new Subject();
  onRouteUpdate: BehaviorSubject<string[]> = new BehaviorSubject([]);
  idResolve: BehaviorSubject<IBreadcrumbTitle | null> = new BehaviorSubject(null);
  pspElement: BehaviorSubject<string | null> = new BehaviorSubject(null);

}