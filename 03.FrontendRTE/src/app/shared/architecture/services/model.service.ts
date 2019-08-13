/* (c) https://github.com/MontiCore/monticore */

import { DiffPatcher } from 'jsondiffpatch';
import { Observable } from 'rxjs';
import { NotificationService } from '@shared/notification/notification.service';
import { ClonePipe } from '@shared/pipes/clone.pipe';
import { RemoveNullsPipe } from '@shared/pipes/remove-nulls.pipe';
import { MontiGemError } from '@shared/utils/montigem.error';
import { IModel, Model } from '@shared/architecture/data/models/model';
import { addModel, deleteModel, updateModel } from '@shared/architecture/debug';
import { ISerializable } from '@shared/architecture/serializable';
import { ApiService, IApiService } from './api.service';
import { JsonApiService, JsonResponse } from '@jsonapiservice/json-api.service';

export interface IModelService<D extends IModel> extends IApiService {
  getByIds(...ids: number[]): Observable<D[]>;

  getById(id: number): Observable<D | null>;

  getAll(first?: number, max?: number): Observable<D[]>;

  getUpdateLocal<T extends IModel & ISerializable<D>>(model: T): Observable<T>;

  delete<T extends IModel & ISerializable<D>>(model: T): Observable<any>;

  add<T extends IModel & ISerializable<D>>(model: T): Observable<T>;

  update<T extends IModel & ISerializable<D>>(model: T): Observable<T>;
}

export abstract class ModelService<D extends IModel> extends ApiService<D> implements IModelService<D> {

  private _clonePipe: ClonePipe = new ClonePipe();
  private _removeNullsPipe: RemoveNullsPipe = new RemoveNullsPipe();
  private _getObservables: Map<number, Observable<D>> = new Map();

  protected constructor(api: JsonApiService, protected modelConstructor: new () => D, baseUrl: string, private notificationService: NotificationService) {
    super(api, baseUrl);
    this.logger.addFlag('model');
    this.logger.addFlag('model-service');
  }

  /**
   * this method return all models with the passed ids. If some of the models already cached they will be not request
   * again
   * @param {number[]} ids
   * @returns {Observable<Model[]>}
   */
  public getByIds(...ids: number[]): Observable<D[]> {
    let observer: Observable<D[]> = Observable.throw('Something went getByIds badly wrong while getting models by ids from the database!');
    if (ids && Array.isArray(ids) && ids.length !== 0) {
      let requestUrl = this.baseUrl + `/all/%5B${ids.join('%2C')}%5D`;
      observer = this.api.get(requestUrl, true, false, JsonApiService.HeaderJson).map((response: JsonResponse) => {
        this.logger.debug(`getByIds(${ids.join(',')})`, response.json);
        const models = this.deserializeArray(response.json, this.modelConstructor);
        return models;
      }).catch((error: any) => {
        let macocoErr: MontiGemError = JsonApiService.deserializeError(error.json(), this.logger);
        this.notificationService.error(macocoErr);
        return Observable.throw(macocoErr);
      }).share();
    } else {
      observer = Observable.throw('Model ids object is not valid!');
    }
    return observer;
  }

  // TODO : check if model with id is loading
  /**
   * This method return the model with the passed id.
   * @param {number} id
   * @returns {Observable<Model>}
   */
  public getById(id: number): Observable<D | null> {
    let requestUrl = this.baseUrl + `/${id}`;
    let observer: Observable<D> = Observable.throw('Something went badly wrong while getting model by id from the database!');
    if (id > 0) {
      observer = this.api.get(
          requestUrl,
          true,
          false,
          JsonApiService.HeaderJson,
      ).map((response: JsonResponse) => {
        this.logger.debug(`getById(${id})`, response.json);
        const model = this.deserialize(response.json, this.modelConstructor);
        return model;
      }).catch((error: any) => {
        let macocoErr: MontiGemError = JsonApiService.deserializeError(error.json(), this.logger);
        this.notificationService.error(macocoErr);
        return Observable.throw(macocoErr);
      });
    } else {
      observer = Observable.throw('Passed id is not valid');
    }

    return observer;
  }

  /**
   * This method return all models based on the passed parameters. ModelCach will be ignored!
   *
   * @param {number} first (optional) offest
   * @param {number} max (optional) count of models
   * @returns {Observable<Model[]>}
   */
  public getAll(first?: number, max?: number): Observable<D[]> {
    let observer: Observable<D[]> = Observable.throw('Something went badly wrong while getting all models from the database!');
    let url = '';
    if (first && first > 0) {
      if (max && max > 0) {
        url = `?first=${first}&max=${max}`;
      } else {
        url = `?first=${first}`;
      }
    }

    observer = this.api.get(
        this.baseUrl + url,
        true,
        true,
        JsonApiService.HeaderJson)
        .map((response: JsonResponse) => {
          // deserialize all models
          return this.deserializeArray(response.json, this.modelConstructor);
        });

    return observer;
  }

  public getUpdateLocal<T extends IModel & ISerializable<D>>(model: T): Observable<T> {
    let observer: Observable<T> = Observable.throw('Something went badly wrong while update local model!');

    if (model.id) {
      let requestUrl = this.baseUrl + `/${model.id}`;
      observer = this.api.get(
          requestUrl,
          true,
          false,
          JsonApiService.HeaderJson
      ).map((response: JsonResponse) => {
        this.logger.debug(`getUpdateLocal(${model.id})`, {
          send: model.serialize(),
          response: response.json
        });
        model.deserialize(response.json);
        return model;
      });
    } else {
      observer = Observable.throw('Model has not an ID!');
    }

    return observer;
  }

  /**
   * This methode deletes the passed model from the database
   * @param model
   * @returns {Observable<Model>}
   */
  public delete(model: IModel & ISerializable<IModel>): Observable<boolean> {
    this.logger.debug('delete', model);
    let observer: Observable<any> = Observable.throw('Something went badly wrong while deleting model to database!');
    if (model) {
      if (!!model.id) {

        observer = this.deleteById(model.id).map((status: boolean) => {
          if (status) {
            deleteModel(model);
            // TODO : backed should send deleted model with isActive flag = false
            model.deserialize({isActive: false});
            return true;
          } else {
            return false;
          }
        }).share();
      } else {
        observer = Observable.empty();
      }
    } else {
      observer = Observable.throw('Model object is undefined or null!');
    }
    return observer;
  }

  public deleteById(id: number): Observable<boolean> {
    this.logger.debug('delete', id);
    let observer: Observable<any> = Observable.throw('Something went badly wrong while deleting model to database!');
    if (id > 0) {
      observer = this.api.delete(this.baseUrl + `/${id}`)
          .do((response) => this.logger.debug(`delete(${id})`, response))
          .map((response) => response.status < 400).share();
    } else {
      observer = Observable.throw('id is less or equal 0!');
    }
    return observer;
  }

  /**
   * This method adds the passed model to the database and add the model to the ModelCache
   * Throws if the model is already added to the datebase or the request don't response the new model id.
   * @param {Model} model
   * @returns {Observable<Model>}
   */
  public add<T extends IModel & ISerializable<D>>(model: T): Observable<T> {
    this.logger.debug('add', model.serialize());
    let observer: Observable<T> = Observable.throw('Something went badly wrong while adding model to database!');
    if (model) {
      if (!model.id) {
        const json = model.serialize();
        delete json.id;
        observer = this.api.post(
            this.baseUrl,
            json,
            JsonApiService.HeaderJson
        ).map((response: JsonResponse) => {
          this.logger.debug(`add()`, {send: model.serialize(), response: response.json});
          // update local fields with changed fields from the new object in the database (currency, etc.)
          model.deserialize(response.json);
          if (!model.id) {
            throw new Error('Model id was not set!');
          }
          addModel(model);
          return model;
        }).share();
      } else {
        observer = Observable.throw('Model has already an id and is probably already added to the database!');
      }
    }
    return observer;
  }

  /**
   * This method call for each changed field the  proper set function
   * @param {Model} model
   * @returns {Observable<Model>}
   */
  public update<T extends IModel & ISerializable<D>>(model: T): Observable<T> {
    this.logger.debug('update', model);
    let observer: Observable<T> = Observable.throw('Model object is undefined or null!');
    if (model) {
      if (model.id) {
        const json = model.serialize();
        delete json.id;
        observer = this.api.post(
            `${this.baseUrl}/${model.id}`,
            json,
            JsonApiService.HeaderJson
        ).map((response: JsonResponse) => {
          this.logger.debug(`update(${model.id})`, {
            send: model.serialize(),
            response: response.json,
            updated: new DiffPatcher().diff(
                this._removeNullsPipe.transform(model.serialize()),
                this._removeNullsPipe.transform(this._clonePipe.transform(response.json))
            )
          });
          // update local fields with changed fields from the new object in the database (currency, etc.)
          model.deserialize(response.json);
          updateModel(model);
          return model;
        }).share();
      } else {
        observer = Observable.throw('Model has not an id and is probably not added to the database!');
      }
    }
    return observer;
  }

  private set<T extends IModel & ISerializable<D>>(model: T, fnc: string): Observable<T> {
    let observer: Observable<T> = Observable.throw('Something went badly wrong while setting model property!');
    if (model && fnc) {
      if (model.id) {
        observer = this.api.put(
            `${this.baseUrl}/${model.id}/${fnc}${Array.isArray(model[fnc]) ? '/all' : ''}`,
            model[`_${fnc}`],
            JsonApiService.HeaderJson
        ).map((response: JsonResponse) => {
          this.logger.debug(`set(${model.id}, '${fnc}')`, {
            send: model.serialize(),
            response: response.json
          });
          model.deserialize(response.json);
          return model;
        });
      } else {
        observer = Observable.throw('Model is not present in the database!');
      }
    } else {
      observer = Observable.throw('Model object is undefined or null or fnc not valid!');
    }
    return observer;
  }

}
