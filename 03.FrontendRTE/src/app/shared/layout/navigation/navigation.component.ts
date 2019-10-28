/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {JsonApiService} from '@jsonapiservice/json-api.service';
import {DialogCallbackOne} from '@shared/utils/dialog/dialog.callback';
import {AuthService} from '@shared/auth/auth.service';
import {NotificationService} from '@shared/notification/notification.service';
import {Router} from '@angular/router';
import {PermissionFlags, Token} from "@shared/auth/token";
import {MatSnackBar} from '@angular/material';


export interface INavigation {
  label: string;
  icon: string;
  link?: string[];
  expanded?: boolean;
  children?: INavigation[];
  enabled?: () => boolean;
}

@Component(
  {
    selector: 'sa-navigation',
    templateUrl: './navigation.component.html',
    styleUrls: ['./navigation.component.scss']
  }
)
export class NavigationComponent {

  constructor(private jsonApi: JsonApiService,
    private auth: AuthService,
    private router: Router,
    private notification: NotificationService,
    private snackBar: MatSnackBar
  ) {

  }

  public navigation: INavigation[] = [
    {label: 'Dashboard', link: ['/', 'dashboard'], icon: 'dashboard'},
    {label: 'Chart', link: ['/', 'guidsl', 'linechart'], icon: 'multiline_chart'},
    {
      label: 'Einstellungen', link: ['/', 'einstellungen', 'profil'], icon: 'settings', expanded: true, children: [
        {
          label: 'Mein Profil',
          link: ['/', 'einstellungen', 'profil'],
          icon: 'supervisor_account',
        },
        {
          label: 'Benutzer',
          link: ['/', 'einstellungen', 'benutzer'],
          icon: 'supervisor_account',
          enabled: (): boolean => {
            return Token.hasPermissionFor(PermissionFlags.USER)
          },
        },
        {
          label: 'Rollen',
          link: ['/', 'einstellungen', 'rollen'],
          icon: 'supervisor_account',
          enabled: (): boolean => {
            return Token.hasPermissionFor(PermissionFlags.USER)
          },
        },
      ],
    },
  ];

  private version(): string {
    let v: string = '${app.version}';
    if (v === '${app.version}') {
      v = '0.0.1-SNAPSHOT';
    }
    return v;
  }

  private buildTime(): string | undefined {
    let t = '${app.buildTime}';
    if (t === '${app.buildTime}') {
      t = undefined;
    }
    return t;
  }

  public versionInfo(): string {
    return 'V' + this.version() + (this.buildTime() !== undefined ? ', ' + this.buildTime() : '');
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

  public createDummy() {
    this.snackBar.open('Creating dummy data...', '', {
      duration: 3000
    });
    this.jsonApi.post('/develop/createDummy', {}).subscribe();
    return true;
  }

  public resetDB() {
    this.snackBar.open('Resetting database...', '', {
      duration: 3000
    });
    this.jsonApi.get('/develop/resetDB').subscribe();
    return true;
  }
}
