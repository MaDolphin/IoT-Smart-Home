import { JsonObject } from '@upe/typedjson';
import { DTO } from '@shared/architecture/data';

@JsonObject()
export class OkDTO extends DTO<OkDTO> {
  public getData(): any {
    return '';
  }

  constructor() {
    super();
    this.typeName = 'OkDTO';
  }
}