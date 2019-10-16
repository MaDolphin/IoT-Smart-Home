/* (c) https://github.com/MontiCore/monticore */

import { Observable } from 'rxjs';
import { IModel, Model } from './models/model';
import { ISerializable, Serializable } from "@shared/architecture/serializable";
import { IModelService } from "@shared/architecture/services/model.service";
import { DiffPatcher } from "jsondiffpatch";
import { setEntry } from "@shared/architecture/debug";

export interface IViewModel<D extends IModel> extends IModel {
  concert: D;
  internId: string;
  deepSave(): Observable<IViewModel<D> | null>;
  flatSave(): Observable<IViewModel<D> | null>;
  delete(): Observable<boolean>;
  update(): Observable<IViewModel<D> | null>;
  clear(): void;
}

/**
 * Contains all already used internal ids
 * This array is used to prevent coalitions
 * @type {Array}
 */
const internIds: string[] = [];

export type ConsistencyTest<D extends IModel & ISerializable<D>> = (model: IViewModel<D>) => boolean;

export abstract class ViewModel<D extends IModel & ISerializable<D>,
  A extends IModelService<D>,
  P extends IViewModel<D> & IModel & ISerializable<D>> extends Serializable<D> implements IViewModel<D> {

  // region getter/setter

  public get id(): number {
    if (this.concert.id === undefined) {
      this.logger.error('MAF0x00AC: model id is undefined!');
      throw new Error('MAF0x00AC: model id is undefined!');
    }
    return this.concert.id;
  }

  public set id(value: number) {
    this.concert.id = value;
  }

  public get internId(): string {
    return this._internId;
  }

  public get isActive(): boolean {
    if (this.concert.isActive === undefined) {
      this.logger.error('MAF0x00AD: model isActive flag is undefined!');
      throw new Error('MAF0x00AD: model isActive flag is undefined!');
    }
    return this.concert.isActive;
  }

  private _internId: string = (() => {
    let id;
    do {
      id = Math.random().toString(36).substr(2, 5);
    } while (internIds.indexOf(id) !== -1);
    internIds.push(id);
    return id;
  })();

  /**
   * Get the concert model object.
   * @return {D}
   */
  public get concert(): D {
    return this._concert;
  }

  public get original(): D {
    return JSON.parse(JSON.stringify(this._original));
  }

  protected get api(): A {
    return this._api;
  }

  /**
   * Indecent if the model is changed locally
   * @return {boolean}
   */
  public get changed(): boolean {
    if (this._original) {

      const removeNulls = (obj) => {
        for (const key in obj) {
          if (obj[key] !== null) {
            if (Array.isArray(obj[key])) {
              obj[key] = obj[key].filter(item => item !== null);
              for (const arrayItem of obj[key]) {
                removeNulls(arrayItem);
              }
            } else if (typeof obj[key] === 'object') {
              removeNulls(obj[key]);
            }
          } else {
            delete obj[key];
          }
        }

        return obj;
      };

      let current = this.serialize();
      current = removeNulls(current);
      let original = removeNulls(JSON.parse(JSON.stringify(this._original)));
      let patcher: DiffPatcher = new DiffPatcher();
      let delta = patcher.diff(original, current);
      this.logger.debug('changed', {id: this.id, delta: delta});
      if (delta) {
        return Object.keys(delta).length > 0;
      } else {
        return false;
      }
    }
    return true;
  }


  // endregion

  private readonly _original: D;

  private _consistencyTests: Array<ConsistencyTest<D>> = [];

  protected constructor(private _concert: D, private _api: A) {
    super();
    this.logger.addFlag('model');
    this.logger.addFlag('viewModel');
    if (!(this.concert instanceof Model)) {
      this.logger.error('MAF0x0072: concert is not type of Model!', this.concert);
      throw new Error(`MAF0x0072: concert is not type of Model! [${typeof this.concert}]\n${JSON.stringify(this.concert)}`);
    }
    this._original = this.concert.serialize();
    setEntry(this);
  }

  // region consistency

  /**
   * Try to correct any consistent errors
   */
  public consistencyCorrection(): void {
  }

  // endregion

  // region api

  /**
   * This method will save this model and all associations.
   *
   * @return {Observable<Model>}
   */
  public deepSave(): Observable<P | null> {
    this.logger.debug('deepSave', {id: this.id, internId: this.internId, self: this});
    return this.flatSave();
  }

  /**
   * This method is a wrapper for update or add from the corresponding model service
   * The add method is called if the model id is not set.
   * The update method is called if the model is is set.
   * If the model is not changed neither of them will be called
   * This method will save this model into the db without saving any associations.
   *
   * @return {Observable<Model>}
   */
  public flatSave(): Observable<P | null> {
    let observer: Observable<P> = Observable.throw('fail to save');
    this.logger.debug('flatSave', {id: this.id, internId: this.internId, self: this.serialize()});
    if (this.changed) {
      if (this.id) {
        observer = this.api.update<P>(this as any).do((model) => {
          if (!model) {
            this.logger.error('MAF0x0073: model update failed', this);
          }
        });
      } else {
        observer = this.api.add<P>(this as any);
      }
    } else {
      if (this.id) {
        observer = Observable.of(this as any);
      } else {
        observer = this.api.add<P>(this as any);
      }
    }
    return observer;
  }

  /**
   * This method is a wrapper for delete from the corresponding model service
   * @return {Observable<any>}
   */
  public delete(): Observable<boolean> {
    return this.api.delete(this);
  }

  /**
   * This method is a warpper method for getUpdateLocal from the corresponding model service
   * @return {Observable<Model>}
   */
  public update(): Observable<P | null> {
    return this.api.getUpdateLocal<P>(this as any);
  }

  // endregion

  // region Serializable

  // FIXME : The return type is not the class that extends IModel. Its an interface of IModel
  public serialize(strict: boolean = false): D {
    return this.concert.serialize(strict);
  }

  public deserialize<T extends IModel>(json: T): void {
    this.concert.deserialize(json as any);
  }

  // endregion

  // region helper

  /**
   * This method compare two models.
   * If both models has an id the id's will be compared to determined if this objects are equal
   * If any model has not an id the internalId will be used
   * @param model
   * @return {number|boolean}
   */
  public equal(model: ViewModel<D, A, P>): boolean {
    return (this.id && model.id && this.id === model.id) ||
      (this.internId === model.internId)
  }

  /**
   * clears the [data model]{@link Model}
   * replace [concert]{@link ViewModel#concert} with new instance
   */
  public clear(): void {
    this._concert = new this.concert['constructorFunction']();
    this.reset();
  }

  /**
   * rests all load linked view models
   */
  public abstract reset(): void;

  // endregion

}
