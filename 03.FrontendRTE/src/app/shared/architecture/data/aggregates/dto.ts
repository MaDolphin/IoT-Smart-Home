import { ISerializable, Serializable } from '@shared/architecture/serializable';
import { JsonMember, JsonObject } from '@upe/typedjson';

export interface IDTO {
  typeName?: string,
  id: number

  getData(): DTO<any>

  transform()
}

@JsonObject()
export class DTO<D extends IDTO> extends Serializable<D> implements IDTO, ISerializable<D> {

  @JsonMember({
    isRequired: false,
    name: 'typeName',
    type: String,
  }) public typeName?: string = 'unknown';

  @JsonMember({
    isRequired: true,
    name: 'id',
    type: Number,
  }) public id: number = 0;

  constructor(dto?: IDTO) {
    super();
    this.typeName = (dto && dto.typeName) ? dto.typeName : 'unknown';
    this.id = (dto && dto.id) ? dto.id : 0;
    this.logger.addFlag('DTO');
  }

  getData(): DTO<any> {
    return this
  }

  transform() {
  }

}
