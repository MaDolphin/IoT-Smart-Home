import {JsonMember} from '@upe/typedjson';

export interface ICommandDTO {
  typeName: string;
  commandId: number;
}

export abstract class CommandDTO implements ICommandDTO {
  @JsonMember({
    isRequired: true,
    name: 'commandId',
    type: Number,
  }) public commandId: number;

  @JsonMember({
    isRequired: true,
    name: 'typeName',
    type: String,
  }) public typeName: string;

  protected constructor(typeName: string) {
    this.commandId = -1;
    this.typeName = typeName;
  }
}