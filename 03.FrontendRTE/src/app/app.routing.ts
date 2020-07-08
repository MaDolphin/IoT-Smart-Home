/* (c) https://github.com/MontiCore/monticore */

import {RouterModule, Routes} from '@angular/router';
import {ActivationLinkComponent} from '@shared/auth/activation-link/activation-link.component';
import {AuthGuard} from '@shared/auth/auth-guard';
import {AuthComponent} from '@shared/auth/auth.component';
import {ForgotComponent} from '@shared/auth/forgot/forgot.component';
import {LoginComponent} from '@shared/auth/login/login.component';
import {MainLayoutComponent} from '@shared/layout/main/main-layout.component';
import {DashboardComponent} from "@dashboard/dashboard.component";
import {InfoComponent} from './info/info.component';
import { DashboardExampleRlHmComponent } from '@dashboard/dashboard-ExampleRlHm.component';
import {DashboardTemplateComponent} from '@dashboard/dashboard-template.component';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    canActivateChild: [AuthGuard],
    children: [
      {
        path: '',
        redirectTo: 'dashboard/dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard/dashboard',
        component: DashboardComponent,
        data: {pageTitle: 'Dashboard'}
      },
      {
        path: 'dashboard/Example-RlHm',
        component: DashboardExampleRlHmComponent,
        data: {pageTitle: 'Example-RlHm'}
      },
      {
        path: 'dashboard/template',
        component: DashboardTemplateComponent,
      },
      {
        path: 'beispiele',
        loadChildren: './beispiele/beispiele.module#BeispieleModule',
      },
      {
        path: 'einstellungen',
        loadChildren: './einstellungen/einstellungen.module#EinstellungenModule',
      },
      {
        path: 'info',
        component: InfoComponent
      },
    ]
  },

  {
    path: 'auth',
    component: AuthComponent,
    children: [
      {
        path: 'login',
        component: LoginComponent,
      },
      {
        path: 'forgot-password',
        component: ForgotComponent,
      },
      {
        path: 'activations',
        component: ActivationLinkComponent,
      },
      {
        path: 'forgotPwd',
        component: ActivationLinkComponent,
      },
    ],
  },
  {
    path: '**', redirectTo: 'dashboard/dashboard'
  }
];

export const routing = RouterModule.forRoot(routes);
