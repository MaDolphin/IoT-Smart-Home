import { JsonMember } from '@upe/typedjson';

export interface ICommand {
  commandId: number;
  identifier: string;
  method: string;
  id: number | undefined;
  param: string | undefined;
}

// TODO SVa: deprecated
export class Command implements ICommand {
  @JsonMember({
    isRequired: true,
    name: 'commandId',
    type: Number,
  }) public commandId: number;

  @JsonMember({
    isRequired: true,
    name: 'identifier',
    type: String,
  }) public identifier: string;

  @JsonMember({
    isRequired: true,
    name: 'method',
    type: String,
  }) public method: string;

  @JsonMember({
    isRequired: false,
    name: 'id',
    type: Number,
  }) public id: number | undefined;

  @JsonMember({
    isRequired: false,
    name: 'param',
    type: String,
  }) public param: string | undefined;

  constructor(identifier: string, method: string, id?: number, param?: string) {
    this.commandId = -1;
    this.identifier = identifier;
    this.method = method;
    this.id = id || undefined;
    this.param = param || undefined;
  }
}