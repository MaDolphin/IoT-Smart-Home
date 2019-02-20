import { JsonMember } from '@upe/typedjson';

export abstract class Type {

  @JsonMember({name: 'id', type: Number}) public id: number = 0;

  @JsonMember({name: 'value', type: String}) public value: string = '';

  public toString(): string {
    return this.value;
  }

}
