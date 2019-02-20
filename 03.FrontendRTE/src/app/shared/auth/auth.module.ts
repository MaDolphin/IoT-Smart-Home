import { CommonModule } from '@angular/common';
import { ModuleWithProviders, NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule } from '@angular/forms';
import { MatButtonModule, MatCheckboxModule, MatIconModule, MatSelectModule } from '@angular/material';
import { RouterModule } from '@angular/router';
import { ButtonDirectivesModule, FormDirectivesModule, InputGroupDirectivesModule, } from '@upe/ngx-bootstrap-directives';
import { PasswordStrengthBarModule } from 'ng2-password-strength-bar';
import { ActivationLinkComponent } from './activation-link/activation-link.component';
import { AuthGuard } from './auth-guard';
import { AuthComponent } from './auth.component';
import { AuthService } from './auth.service';
import { ForgotComponent } from './forgot/forgot.component';
import { LoginComponent } from './login/login.component';
import { RegService } from './reg.service';
import { RegisterComponent } from './register/register.component';
import { InputModule } from '@shared/components/guidsl/input/input.module';


@NgModule(
    {
      imports: [
        MatButtonModule,
        MatCheckboxModule,
        MatSelectModule,
        CommonModule,
        FormsModule,
        RouterModule,
        FlexLayoutModule,
        MatIconModule,
        PasswordStrengthBarModule,
        ButtonDirectivesModule,
        FormDirectivesModule,
        InputGroupDirectivesModule,
        InputModule,
      ],
      declarations: [
        LoginComponent,
        RegisterComponent,
        ForgotComponent,
        AuthComponent,
        ActivationLinkComponent
      ],
      exports: [
        LoginComponent,
        RegisterComponent,
        ForgotComponent,
        AuthComponent,
        ActivationLinkComponent
      ]
    }
)
export class AuthModule {

  static forRoot(): ModuleWithProviders {
    return {
      ngModule: AuthModule,
      providers: [
        AuthService,
        AuthGuard,
        RegService
      ]
    };
  }
}
