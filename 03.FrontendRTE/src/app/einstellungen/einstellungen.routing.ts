import { RouterModule, Routes } from '@angular/router';
import { EinstellungenBenutzerGenComponent } from './einstellungen-benutzer-gen.component';
import { EinstellungenMeinbenutzerGenComponent } from './einstellungen-meinbenutzer-gen.component';
import { EinstellungenRollenGenComponent } from './einstellungen-rollen-gen.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'benutzer',
    pathMatch: 'full',
  },
  {
    path: 'profil',
    component: EinstellungenMeinbenutzerGenComponent,
  },
  {
    path: 'benutzer',
    component: EinstellungenBenutzerGenComponent,
  },
  {
    path: 'rollen',
    component: EinstellungenRollenGenComponent,
  }
];

export const routing = RouterModule.forChild(routes);
