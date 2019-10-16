/* (c) https://github.com/MontiCore/monticore */

import { ILoggable } from '@upe/logger';
import { JsonMember } from '@upe/typedjson';
import { DateFormats } from '@shared/pipes/date-to-string.pipe';
import { ISerializable, Serializable } from '@shared/architecture/serializable';

export interface IModel {
  id?: number;
  isActive?: boolean;
}

export abstract class Model<D extends IModel> extends Serializable<D> implements IModel, ILoggable, ISerializable<D> {

  public static DateFormat: DateFormats = 'ISO';

  public get constructorFunction(): new () => D {
    return this['constructor'] as any;
  }

  @JsonMember({name: 'id', type: Number}) public id: number = 0;

  @JsonMember({name: 'isActive', type: Boolean}) public isActive: boolean = true;

  constructor() {
    super();
    this.logger.addFlag('model');
  }
}
