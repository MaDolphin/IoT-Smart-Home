/* (c) https://github.com/MontiCore/monticore */

import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router } from '@angular/router';
import { Logger } from '@upe/logger';
import { PerfectScrollbarComponent } from 'ngx-perfect-scrollbar';
import { LoadingService } from '../loading/loading.service';
import { MainLayoutService } from './main-layout.service';

@Component(
    {
      templateUrl: './main-layout.component.html',
      styleUrls:   ['./main-layout.component.scss'],
    }
)
export class MainLayoutComponent implements OnInit {

  @ViewChild(PerfectScrollbarComponent) perfectScrollbar: PerfectScrollbarComponent;

  // public historyBarActive: boolean = false;
  public menuActive: boolean        = true;
  public disableLoading: boolean    = false;

  public onActivate(): void {
    // this.perfectScrollbar.directiveRef.scrollTo(0);
  }

  private logger: Logger = new Logger({name: 'MainLayoutComponent'});

  constructor(
    private router: Router,
    private loadingService: LoadingService,
    private mainLayoutService: MainLayoutService,
    private cdf: ChangeDetectorRef,
  ) {

  }

  public ngOnInit() {
    this.mainLayoutService.disableLoading.subscribe((status) => {
      this.disableLoading = status;
      try {
        this.cdf.detectChanges();
      } catch (e) {
        // TODO : hack
        this.logger.error('MAF0x0092: detectChanges', e);
        location.reload();
      }
    });
    this.router.events.subscribe((event: any) => {
      if (event instanceof NavigationStart) {
        this.loadingService.start();
      }
      if (event instanceof NavigationEnd) {
        this.loadingService.stop();
      }

      // Set loading state to false in both of the below events to hide the spinner in case a request fails
      if (event instanceof NavigationCancel) {
        this.loadingService.stop();
      }
      if (event instanceof NavigationError) {
        this.loadingService.stop();
      }
    });
  }

}
