import { Component } from '@angular/core';
import { JsonApiService } from '@shared/architecture/services/json-api.service';
import { DialogCallbackOne } from '@shared/utils/dialog/dialog.callback';
import { AuthService } from '@shared/auth/auth.service';
import { NotificationService } from '@shared/notification/notification.service';
import { Router } from '@angular/router';
import { PermissionFlags, Token } from '@shared/auth/token';


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
    private notification: NotificationService, ) {

  }

  public navigation: INavigation[] = [
    { label: 'Dashboard', link: ['/', 'dashboard'], icon: 'dashboard' },
    {
      label: 'Finanzen', link: ['/',  'finanzen', 'dashboard'], icon: 'euro_symbol', expanded: true, children: [
        { label: 'Konten', link: ['/', 'finanzen', 'konten', 'overview'], icon: 'euro_symbol' },
        { label: 'Zahlungen', link: ['/', 'finanzen', 'zahlungen', 'buchungen'], icon: 'euro_symbol' },
      ],
    },
    {
      label: 'Fakultät', link: ['/', 'fakultaet', 'dashboard'], icon: 'account_balance', enabled: (): boolean => {
        return Token.hasPermissionFor(PermissionFlags.FAKULTAET_INSTANZ);
      }, expanded: true, children: [
        { label: 'Übersicht', link: ['/', 'fakultaet', 'overview'], icon: 'euro_symbol' },
        { label: 'Begründungen', link: ['/', 'fakultaet', 'begruendung'], icon: 'euro_symbol' }
      ]
    },
    {
      label: 'Personal',
      link: ['/', 'personal', 'dashboard'],
      icon: 'supervisor_account',
      expanded: true,
      enabled: (): boolean => {
        return Token.hasPermissionFor(PermissionFlags.PERSONAL)
      },
      children: [
        { label: 'Mitarbeiter', link: ['/', 'personal', 'mitarbeiter', 'overview'], icon: 'account_circle' },
        { label: 'Planstellen', link: ['/', 'personal', 'planstellen', 'overview'], icon: 'account_box' },
        { label: 'Gehaltszahlungen', link: ['/', 'personal', 'gehaltszahlungen', 'overview'], icon: 'euro_symbol' },
        { label: 'Finanzierung', link: ['/', 'personal', 'finanzierung', 'overview'], icon: 'euro_symbol' }
      ]
    },
    {
      label: 'Einstellungen', link: ['/', 'einstellungen', 'profil'], icon: 'settings', expanded: true, children: [
        {
          label: 'Mein Profil',
          link: ['/', 'einstellungen', 'profil'],
          icon: 'supervisor_account',
        },
        {
          label: 'Benutzer/Rollen',
          link: ['/', 'einstellungen', 'benutzer'],
          icon: 'supervisor_account',
          enabled: (): boolean => {
            return Token.hasPermissionFor(PermissionFlags.USER)
          },
        },
        {
          label: 'Daten Import',
          link: ['/', 'guidsl', 'einstellungen', 'import'],
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
      v = '1.9.6-SNAPSHOT';
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
    this.jsonApi.post('/develop/createDummy', {}).subscribe();
    return true;
  }

  public resetDB() {
    this.jsonApi.get('/develop/resetDB').subscribe();
    return true;
  }
}
