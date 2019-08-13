/* (c) https://github.com/MontiCore/monticore */

import { JsonMember, JsonObject } from '@upe/typedjson';

// TODO : move to model folder

@JsonObject()
export class DataTableStats {

  // TODO : getter / setter ?

  @JsonMember({name: 'time', type: Number}) time: number;
  @JsonMember({name: 'start', type: Number}) start: number;
  @JsonMember({name: 'length', type: Number}) length: number;
  @JsonMember({name: 'order', elements: {type: Object}}) order: any[];
  @JsonMember({name: 'search', type: Object}) search: { serach: string, regex: boolean, smart: boolean, caseInsensitive: boolean };
  @JsonMember({name: 'columns', elements: {type: Object}}) columns: [ { visible: boolean, search: any } ];
}
