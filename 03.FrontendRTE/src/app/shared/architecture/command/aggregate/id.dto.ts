/* (c) https://github.com/MontiCore/monticore */

import { JsonMember, JsonObject } from '@upe/typedjson';
import { DTO } from '@shared/architecture';

@JsonObject()
export class IdDTO extends DTO<IdDTO> {
  @JsonMember({
    isRequired: true,
    name: 'id',
    type: Number,
  }) private _objectId: number;

  constructor() {
    super();
    this.typeName = 'IdDTO';
    this._objectId = 0;
  }

  get objectId(): number {
    return this._objectId;
  }

  set objectId(value: number) {
    this._objectId = value;
  }
}
