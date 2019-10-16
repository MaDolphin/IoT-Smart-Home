/* (c) https://github.com/MontiCore/monticore */

import { JsonMember, JsonObject } from '@upe/typedjson';
import { DataTableStats } from './data-table-stats';

// TODO : move to model folder

@JsonObject()
export class UserSettings {

  // TODO : lazyload + multi table handling
  @JsonMember({name: 'dataTables', type: DataTableStats}) dataTables: DataTableStats;

}
