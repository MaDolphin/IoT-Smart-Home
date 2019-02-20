import {JsonMember, JsonObject} from '@upe/typedjson';
import {CommandResultDTO} from '@shared/architecture/command/response/command.result.dto';

interface ICommandResponseDTO {
  id: number;
  responses: CommandResultDTO[];
}

@JsonObject()
export class CommandResponseDTO implements ICommandResponseDTO {
  @JsonMember({
    isRequired: true,
    name: 'id',
    type: Number,
  }) public id: number;

  @JsonMember({
    isRequired: true,
    name: 'responses',
    elements: CommandResultDTO,
  }) public responses: CommandResultDTO[] = [];
}