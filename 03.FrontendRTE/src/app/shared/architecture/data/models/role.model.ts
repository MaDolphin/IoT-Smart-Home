/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { JsonMember, JsonObject } from '@upe/typedjson';
import { IModel, Model } from './model';


export interface IRole extends IModel {
  name: string;
  permissions: string[];
}

@JsonObject()
export class Role extends Model<Role> implements IRole {

  @JsonMember({isRequired: true, name: 'name', type: String}) public name: string;

  @JsonMember({isRequired: true, name: 'permissions', elements: {type: String}}) public permissions: string[] = [];

}
