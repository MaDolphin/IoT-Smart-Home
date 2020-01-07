/* (c) https://github.com/MontiCore/monticore */
import {Component} from '@angular/core';
import {JsonApiService} from '@services/json-api.service';
import {DialogCallbackOne} from '@shared/utils/dialog/dialog.callback';
import {AuthService} from '@shared/auth/auth.service';
import {NotificationService} from '@shared/notification/notification.service';
import {Router} from '@angular/router';
import {MatSnackBar} from '@angular/material';
import { INavigation } from "@shared/layout/navigation/side-navigation/navigation.interface";
import { MainNavigation } from "@navigation/main.navigation";

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

  public navigation: INavigation[] = MainNavigation.ITEMS;

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
