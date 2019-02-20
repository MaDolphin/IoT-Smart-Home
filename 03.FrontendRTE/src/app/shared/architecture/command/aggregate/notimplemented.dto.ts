import { JsonMember, JsonObject } from '@upe/typedjson';
import { DTO } from '@shared/architecture';

@JsonObject()
export class NotImplementedDTO extends DTO<NotImplementedDTO> {
  @JsonMember({
    isRequired: true,
    name: 'msg',
    type: String,
  }) private _msg: string;

  constructor() {
    super();
    this.typeName = 'NotImplementedDTO';
    this._msg = '';
  }

  get msg(): string {
    return this._msg;
  }

  set msg(value: string) {
    this._msg = value;
  }
}