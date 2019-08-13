/* (c) https://github.com/MontiCore/monticore */

import { JsonMember, JsonObject } from '@upe/typedjson';
import { IModel, Model } from './model';

export interface IUserReg extends IModel {
  username: string;
  email: string;
  password?: string | null;
}

@JsonObject()
export class UserReg extends Model<UserReg> implements IUserReg {

  @JsonMember({isRequired: true, name: 'username', type: String}) public username: string = '';
  @JsonMember({isRequired: true, name: 'email', type: String}) public email: string = '';
  @JsonMember({isRequired: false, name: 'password', type: String}) public password: string | null = null;
}
