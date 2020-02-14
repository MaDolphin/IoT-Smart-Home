/* (c) https://github.com/MontiCore/monticore */

import { Component, OnInit } from '@angular/core';
import { HeaderService, IBreadcrumbTitle } from '@shared/layout/header/header.service';
import { NavigationEnd, Router } from '@angular/router';
import { TitleTranslationPipe } from '@shared/pipes/title-translation.pipe';
import { Logger } from '@upe/logger';
import { Location } from '@angular/common';

export interface BreadcrumbEntry {
  title: string;
  route: string;
}

@Component({
  selector: 'macoco-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.scss'],
  providers: [TitleTranslationPipe]
})
export class BreadcrumbComponent implements OnInit {
  public breadcrumb: BreadcrumbEntry[] = [];

  titleOfId: string = null;

  private logger: Logger = new Logger({ name: 'BreadcrumbComponent' });

  constructor(private headerService: HeaderService, private router: Router, private location: Location) {
  }

  public get length() {
    return this.breadcrumb.length;
  }

  ngOnInit() {
    this.headerService.idResolve.subscribe((title: IBreadcrumbTitle) => {
      if (title != null) {
        console.log('ID Resolve');
        console.log(this.router.url);
        this.titleOfId = title.major;
        this.updateBreadcrumb();
        this.replaceKontoId();
      }
    });

    this.location.subscribe(value => {
      if (value.pop) {
        console.log('Location Back');
        console.log(value.url);
        this.router.navigate([value.url]);
        // console.log("lcoation back");
        // console.log(value.url);
        // this.updateBreadcrumb(value.url); // Location back does not trigger any router events. this is a current issue @angular.
        // this.replaceKontoId();
      }
    });


    this.updateBreadcrumb();

    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        if (event.url) {
          // console.log('Router Navigation End');
          // console.log(this.router.url);
          this.updateBreadcrumb();
          this.replaceKontoId();
          this.titleOfId = null;
        } else {
          this.logger.warn('Could not get url', { event });
        }
      }
    });
  }

  private updateBreadcrumb(url: string = this.router.url) {
    let breadcrumb = [];

    const urlSegments: string[] = url.split('/').filter(i => !!i);

    let handled_url = '';
    for (let element of urlSegments) {
      handled_url += '/' + element;
      breadcrumb.push({ title: element, route: handled_url });
    }
    this.breadcrumb = breadcrumb;
  }

  private replaceKontoId() {
    if (this.titleOfId && this.breadcrumb.length > 2) {
      if (!isNaN(Number(this.breadcrumb[2].title))) {
        this.breadcrumb[2].title = this.titleOfId; // This is not very good... Needs way to find position of id in the route
      }
    }
  }
}
