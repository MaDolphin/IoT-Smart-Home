import { Injectable } from '@angular/core';
import { JsonApiService } from '@jsonapiservice/json-api.service';

export interface Registration {
  username: string,
  email: string,
  password: string
}

@Injectable()
export class RegService {

  constructor(private api: JsonApiService) {
  }

  registerUser(reg: Registration) {
    this.api.post('/users', JSON.stringify(reg), JsonApiService.HeaderJson, false).subscribe(
      response => {
        if (response.status !== 200) {
          alert('Could not create user! Status code: ' + response.status);
        }
      },
      error => {
        alert('Error while creating user!');
      }
    );
  }
}
