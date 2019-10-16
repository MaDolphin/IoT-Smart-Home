/* (c) https://github.com/MontiCore/monticore */

import { JsonMember } from '@upe/typedjson';

interface ICommandResult {
  id: number;
  type: string;
  data: string;
}

// TODO SVa: deprecated
export class CommandResult implements ICommandResult {
  @JsonMember({
    isRequired: true,
    name: 'id',
    type: Number,
  }) public id: number;

  @JsonMember({
    isRequired: true,
    name: 'type',
    type: String,
  }) public type: string;

  @JsonMember({
    isRequired: true,
    name: 'data',
    type: String,
  }) public data: string;
}
