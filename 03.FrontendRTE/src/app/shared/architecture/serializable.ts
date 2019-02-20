import { Logger } from '@upe/logger';
import { TypedJSON } from '@upe/typedjson';
import { IDTO } from './data/aggregates/dto';
import { IModel } from './data/models/model';

export interface ISerializable<D extends IModel | IDTO> {
  /**
   * This method is a warpper function for the TypedJSON serialize.
   * Addinaly the id from the model will be delete and any empty or null property
   * @return {IModel}
   */
  serialize(strict?: boolean): D;
  /**
   * This method update the current object by deserialize all properties from the passed json object.
   * But only if the class has already a mating private field.
   * Optional the originalJson field wouldn't be updated.
   * @param json {IModel}
   */
  deserialize(json: D): void;
}

export abstract class Serializable<D extends IModel | IDTO> implements ISerializable<D> {

  public get logger(): Logger {
    return this._logger;
  }

  private readonly _logger: Logger;

  protected constructor() {
    this._logger = new Logger({name: this['constructor']['name'], flags: ['serializable']});
  }

  // FIXME : The return type is not the class that extends IModel. Its an interface of IModel
  public serialize(strict: boolean = false): D {
    let json = JSON.parse(TypedJSON.stringify(this));
    for (let key in json) {
      if (json.hasOwnProperty(key)) {
        if (strict) {
          if (json[key] === null || json[key] === undefined || json[key] === '') {
            delete json[key];
          }
        } else {
          if (json[key] === undefined) {
            delete json[key];
          }
        }
      }
    }
    return json;
  }

  public deserialize<T extends IModel>(json: T): void {
    Object.assign(this, json);
  }
}
