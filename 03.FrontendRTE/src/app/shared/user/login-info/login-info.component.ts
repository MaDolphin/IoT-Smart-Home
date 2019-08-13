/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { AuthService } from '@shared/auth/auth.service';
import { User } from '../user';

@Component(
  {
    selector: 'macoco-login-info',
    templateUrl: './login-info.component.html',
    styleUrls: ['./login-info.component.scss']
  }
)
export class LoginInfoComponent {

  user: User;

  constructor(public auth: AuthService) {
  }

}
