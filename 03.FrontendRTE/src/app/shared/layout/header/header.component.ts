import { Component, EventEmitter, OnInit, Output, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { NotificationService } from '../../notification/notification.service';
import { User } from '../../user/user';
import { DialogCallbackOne } from '../../utils/dialog/dialog.callback';
import { HeaderService, IBreadcrumbTitle } from './header.service';
import { BreadcrumbComponent } from "@shared/layout/breadcrumb/breadcrumb.component";
import * as moment from 'moment';

@Component({
  selector: 'macoco-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
  public majorTitle: string;
  public pspElement: string;

  @ViewChild('breadcrumb')
  breadcrumb: BreadcrumbComponent;

  public get user(): User {
    return this.auth.user;
  }

  public get exp(): string {
    return "Sie bleiben angemeldet bis " + moment(this.auth.exp).locale("de").format('LT') + " Uhr";
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

  constructor(
    private headerService: HeaderService,
    private auth: AuthService,
    private router: Router,
    private notification: NotificationService
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

}
