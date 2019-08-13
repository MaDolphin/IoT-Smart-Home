/* (c) https://github.com/MontiCore/monticore */

import { JsonMember, JsonObject } from '@upe/typedjson';
import { IModel, Model } from './model';

export interface IUser extends IModel {
  username: string;
  email: string;
  initials: string;
  activated: string;
  enabled: boolean;
}

@JsonObject()
export class User extends Model<User> implements IUser {

  @JsonMember({isRequired: true, name: 'username', type: String}) public username: string = '';
  @JsonMember({isRequired: true, name: 'email', type: String}) public email: string = '';
  @JsonMember({name: 'initials', type: String}) public initials: string = '';
  @JsonMember({isRequired: true, name: 'activated', type: String}) public activated: string = '';
  @JsonMember({isRequired: true, name: 'enabled', type: Boolean}) public enabled: boolean = false;
}
