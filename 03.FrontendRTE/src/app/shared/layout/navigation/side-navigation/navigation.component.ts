/* (c) https://github.com/MontiCore/monticore */
import { Component } from '@angular/core';
import { JsonApiService } from '@shared/architecture/services/json-api.service';
import { DialogCallbackOne } from '@shared/utils/dialog/dialog.callback';
import { AuthService } from '@shared/auth/auth.service';
import { NotificationService } from '@shared/notification/notification.service';
import { ActivatedRoute, Router } from '@angular/router';
import { IActionButton } from "@shared/layout/navigation/side-navigation/action-buttons/action-buttons.component";
import { MatSnackBar } from "@angular/material/snack-bar";
import { LoadingService } from "@shared/layout/loading/loading.service";
import { MainNavigation } from "@navigation/main.navigation";
import { INavigation } from "@shared/layout/navigation/side-navigation/navigation.interface";

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
              private route: ActivatedRoute,
              private notification: NotificationService,
              private loadingService: LoadingService,
              private snackBar: MatSnackBar) {
  }

  public navBarExpanded: boolean = true;

  //region Action buttons
  public actionButtons: IActionButton[] = [
    // {
    //   id: 'hilfe',
    //   tooltip: 'Hilfe',
    //   icon: 'help_outline'
    // },
    {
      id: 'abmelden',
      tooltip: 'Abmelden',
      icon: 'power_settings_new'
    },
    {
      id: 'resetDB',
      tooltip: 'Datenbank zurücksetzen',
      icon: 'delete_forever'
    },
    {
      id: 'createDummy',
      tooltip: 'Dummy-Daten erstellen',
      icon: 'assessment'
    }
  ];

  onClick(id: string) {
    console.log(`Action Button with id ${id} clicked.`);

    switch (id) {
      case "hilfe":
        window.open("https://www.se-rwth.de/", "_blank");
        break;
      case "abmelden":
        this.logout();
        break;
      case "resetDB":
        this.resetDB();
        break;
      case "createDummy":
        this.createDummy();
        break;
      case "createManyDummies":
        this.createManyDummy();
        break;

      default:
        console.log("No action found for action button with id " + id);
    }
  }
  //endregion

  public navigation: INavigation[] = MainNavigation.ITEMS;

  private version(): string {
    let v: string = '${macoco.version}';
    if (v === '${macoco.version}') {
      v = '1.14-SNAPSHOT';
    }
    return v;
  }

  private buildTime(): string | undefined {
    let t = '${macoco.buildTime}';
    if (t === '${macoco.buildTime}') {
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
    this.snackBar.open("Dummy-Daten hinzufügen..", "",  {
      duration: 3000
    });
    this.jsonApi.post('/develop/createDummy', {}).subscribe(_ => {
      window.location.reload(true);
    });
    return true;
  }

  public createManyDummy() {
    this.snackBar.open("Many Dummy-Daten hinzufügen..", "",  {
      duration: 3000
    });
    this.jsonApi.post('/develop/createDummyF1', {}).subscribe(_ => {
      window.location.reload(true);
    });
    return true;
  }

  public resetDB() {
    this.snackBar.open("Datenbank zurücksetzen..", "",  {
      duration: 3000
    });
    this.jsonApi.get('/develop/resetDB').subscribe(_ => {
      window.location.reload(true);
    });
    return true;
  }

  public toggleNav(nav) {
    nav.expanded = !nav.expanded;
  }

  public isExpanded(nav): boolean {
    return this.isInRoute(nav) || nav.expanded;

  }

  private isInRoute(nav): boolean {

    let currentUrl = this.router.url;

    let index = currentUrl.indexOf('/', 1);

    if (index < 0)
      return false;

    let prefix = currentUrl.substr(0, index);

    if (nav.link.length < 2)
      return false;

    let navUrl = nav.link[0] + nav.link[1];

    return prefix === navUrl;
  }

  public navClass(nav) {
    let result = '';

    if ( !nav.children) {
      result += ' rectangle';
      return result;
    }


    if ( this.isInRoute(nav) ) {
      result += ' dot';
    } else {
      if (nav.expanded) {
        result += ' collapsed';
      }


      if (!nav.expanded) {
        result += ' expanded';
      }
    }

    return result;
  }

}
