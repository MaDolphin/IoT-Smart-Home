import { RouterModule, Routes } from '@angular/router';
import { ActivationLinkComponent } from '@shared/auth/activation-link/activation-link.component';
import { AuthGuard } from '@shared/auth/auth-guard';
import { AuthComponent } from '@shared/auth/auth.component';
import { ForgotComponent } from '@shared/auth/forgot/forgot.component';
import { LoginComponent } from '@shared/auth/login/login.component';
import { MainLayoutComponent } from '@shared/layout/main/main-layout.component';


export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    canActivateChild: [AuthGuard],
    children: [
      {
        path: '',
        redirectTo: 'guidsl',
        pathMatch: 'full'
      },
      {
        path:       'guidsl',
        loadChildren: './guidsl/guidsl.module#GuidslModule',
      },
    ]
  },

  {
    path:      'auth',
    component: AuthComponent,
    children:  [
      {
        path:      'login',
        component: LoginComponent,
      },
      {
        path:      'forgot-password',
        component: ForgotComponent,
      },
      {
        path:      'activations',
        component: ActivationLinkComponent,
      },
      {
        path:      'forgotPwd',
        component: ActivationLinkComponent,
      },
    ],
  },
  {
    path: '**', redirectTo: 'guidsl'
  }
];

export const routing = RouterModule.forRoot(routes);
