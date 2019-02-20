import { JsonMember, JsonObject } from '@upe/typedjson';
import { DTO, IDTO } from '@shared/architecture';
import { DTOTypeResolverCustom } from '@shared/architecture/command/response/dtotype.resolver.custom';

interface ICommandResultDTO {
  id: number;
  dto: IDTO;
}

@JsonObject()
export class CommandResultDTO implements ICommandResultDTO {
  @JsonMember({
    isRequired: true,
    name: 'id',
    type: Number,
  }) public id: number;

  @JsonMember({
    isRequired: true,
    name: 'dto',
    type: DTO,
    knownTypesResolver: (json) => DTOTypeResolverCustom.resolve(json.typeName),
  }) public dto: IDTO;
}