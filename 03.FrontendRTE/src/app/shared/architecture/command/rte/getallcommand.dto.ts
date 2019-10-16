/* (c) https://github.com/MontiCore/monticore */

import { JsonMember } from '@upe/typedjson';
import { CommandDTO } from "@shared/architecture/command/rte/command.dto";

export abstract class GetAllCommandDTO extends CommandDTO {
  @JsonMember({
    isRequired: false,
    name: 'limit',
    type: Number,
  }) public limit: number;

  protected constructor(typeName: string, limit?: number) {
    super(typeName);
    this.limit = limit;
  }
}
