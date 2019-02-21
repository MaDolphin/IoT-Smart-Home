import { RouterModule, Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'guidsl',
  },
];

export const routing = RouterModule.forChild(routes);
