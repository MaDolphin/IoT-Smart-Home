import { RouterModule, Routes } from '@angular/router';
import { AnstellungsformFormComponent } from './examples/anstellungsform-form/anstellungsform.form.component';
import { MailAlertDetailsGenComponent } from '@targetgui/mailAlert-details-gen.component/mailAlert-details-gen.component';
import { TableInTableComponent } from '@targetgui/table-in-table.component/table-in-table.component';
import { ImportGenComponent } from '@targetgui/import-gen.component/import-gen.component';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'dashboard',
  },
  {
    path: 'personal/mitarbeiter/:personalId',
    component: AnstellungsformFormComponent,
  },
  {
    path: 'gen/mailAlert/:mailAlertId',
    component: MailAlertDetailsGenComponent,
  },
  {
    path: 'table',
    component: TableInTableComponent,
  },
  {
    path: 'einstellungen/import',
    component: ImportGenComponent,
  },
];

export const routing = RouterModule.forChild(routes);
