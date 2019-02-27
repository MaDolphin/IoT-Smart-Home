/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { ILoggable, Logger } from '@upe/logger';
import { TypedJSON } from '@upe/typedjson';
import { IDTO } from '@shared/architecture/data/aggregates/dto';
import { IModel } from '@shared/architecture/data/models/model';
import { ISerializable } from '@shared/architecture/serializable';
import { JsonApiService } from '@jsonapiservice/json-api.service';

export interface IApiService {
  baseUrl: string;
}

export abstract class ApiService<D extends IModel | IDTO> implements IApiService, ILoggable {

  public get baseUrl(): string {
    return this._baseUrl;
  }

   public get logger(): Logger {
    return this._logger;
  }

  protected get api(): JsonApiService {
    return this._api;
  }

  private readonly _logger: Logger;

  protected constructor(private _api: JsonApiService, private _baseUrl: string) {
    this._logger = new Logger({
      name: this['constructor']['name'] + 'Service',
      flags: ['service', 'api']
    });
  }

  public static deserialize<T>(json: any, type: { new (): T }): T {
    return Object.keys(json).length !== 0 ? TypedJSON.parse(JSON.stringify(json), type) : null;
  }

  /**
   * This method is a wrapper for the member method serialize.
   * This method will serialize an array of models to an array of IModel objects
   * @param models {Model[]}
   * @return {IModel[]}
   */
  protected serializeArray(models: ISerializable<D>[]): D[] {
    return models.map((model: ISerializable<D>) => model.serialize());
  }

  /**
   * This method is a wrapper method for TypedJSON.parse
   * Optional the internal id of the new object can be force set to a specific value.
   * @param json {any}
   * @param type {D}
   * @return {D}
   */
  protected deserialize(json: any, type: { new (): D }): D {
    let obj;
    try {
      obj = ApiService.deserialize(json, type);
    } catch (e) {
      this.logger.error('MAF0x008B: deserialize', {error: e, json: json, type: type});
    }

    return obj;
  }

  /**
   * This method is a wrapper method for TypedJSON.parse
   * @param json {any} the passed object must be an array
   * @param type {new (): T}
   * @return {T[]}
   */
  protected deserializeArray(json: any, type: { new (): D }): D[] {
    let deserialized: D[] = [];
    if (json) {
      if (Array.isArray(json)) {
        json.forEach((element: any) => {
          for (let key in element) {
            if (element.hasOwnProperty(key)) {
              if (element[key] === '' || element[key] === null) {
                delete element[key];
              }
            } else {
              this.logger.warn('key not found in object - deserializeArray');
            }
          }
          try {
            deserialized.push(this.deserialize(element, type));
          } catch (e) {
            this.logger.error('MAF0x00B7: ' + e.message);
            throw new Error('MAF0x00B7: ' + e.message);
          }
        });
      } else {
        this.logger.error('MAF0x00B8: Passed json is not an array!');
        throw new Error('MAF0x00B8: Passed json is not an array!');
      }
    } else {
      this.logger.error('MAF0x00B9: Passed json is not an array!');
      throw new Error('MAF0x00B9: Passed json is not defined!');
    }
    return deserialized;
  }

}
