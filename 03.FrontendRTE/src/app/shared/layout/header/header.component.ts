/* (c) https://github.com/MontiCore/monticore */

import { Component, EventEmitter, OnInit, Output, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { Logger } from '@upe/logger';
import { AuthService } from '@shared/auth/auth.service';
import { NotificationService } from '@shared/notification/notification.service';
import { User } from '@shared/user/user';
import { DialogCallbackOne } from '@shared/utils/dialog/dialog.callback';
import { Fullscreen } from '@shared/layout/main/fullscreen';
import { HeaderService, IBreadcrumbTitle } from './header.service';
import { BreadcrumbComponent } from '@shared/layout/breadcrumb/breadcrumb.component';

@Component({
  selector: 'macoco-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})

export class HeaderComponent implements OnInit {
  public majorTitle: string;
  public minorTitle: string;
  public pspElement: string;

  @ViewChild('breadcrumb')
  breadcrumb: BreadcrumbComponent;

  public get user(): User {
    return this.auth.user;
  }

  public get exp(): string {
    return this.auth.exp;
  }

  public get instanceName(): string {
    return this.auth.instanceName;
  }

  public get title(): string {

    let url = this.router.url.split('/');
    return url[url.length - 1];
  }

  @Output() menu: EventEmitter<void> = new EventEmitter<void>();
  public curDate: Date = new Date();
  private fullscreen: Fullscreen = new Fullscreen();

  private logger: Logger = new Logger({ name: 'HeaderComponent' });

  constructor(
    private headerService: HeaderService,
    private auth: AuthService,
    private router: Router,
    private notification: NotificationService,
  ) {
  }

  public async logout() {
    await this.notification.notificationYesNo(
      'Abmelden?',
      'Soll der Benutzer abgemeldet werden? Sie können Ihre Sicherheit nach dem Abmelden weiter verbessern, indem Sie diesen geöffneten Browser schließen',
      new DialogCallbackOne(() => {
        this.auth.logout();
        this.router.navigateByUrl('/auth/login');
      })
    );
  }

  public ngOnInit() {
    this.headerService.idResolve.subscribe((title: IBreadcrumbTitle) => {
      if (title != null) {
        this.majorTitle = title.major;
      }
    });

    this.headerService.pspElement.subscribe((e) => {
      if (e != null) {
        this.pspElement = e;
      } else {
        this.pspElement = '';
      }
    });

  }

  public toggleFullscreen() {
    this.fullscreen.toggle();
  }
}
