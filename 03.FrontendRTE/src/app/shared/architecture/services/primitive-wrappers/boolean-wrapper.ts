/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { JsonMember, JsonObject } from '@upe/typedjson';

@JsonObject()
export class BooleanWrapper {
  @JsonMember({name: 'value', type: Boolean}) _value: boolean = false;

  public get value() {
    return this._value;
  }

  public set value(value: boolean) {
    this._value = value;
  }
}
