/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { JsonMember, JsonObject } from '@upe/typedjson';

@JsonObject()
export class StringWrapper {
  @JsonMember({name: 'value', type: String}) _value: string | null = null;

  constructor(value: string | null = null) {
    this.value = value;
  }

  public get value(): string | null {
    return this._value;
  }

  public set value(value: string | null) {
    this._value = value;
  }
}
