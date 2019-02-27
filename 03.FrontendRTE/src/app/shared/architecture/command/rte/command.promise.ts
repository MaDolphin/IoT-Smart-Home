/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { IDTO } from '@shared/architecture/data';

type ResponseModelPromise = (dto: IDTO) => Promise<void>;
export type ResponseModelCallback = (dto: IDTO) => void;

export class CommandPromise {
  private _id: number;
  private _method: ResponseModelPromise;

  constructor(callback: ResponseModelCallback) {
    this._id = -1;
    this._method = (dto) => {
      callback(dto);
      return Promise.resolve();
    };
  }

  get id(): number {
    return this._id;
  }

  set id(value: number) {
    this._id = value;
  }

  get method(): ResponseModelPromise {
    return this._method;
  }

  set method(value: ResponseModelPromise) {
    this._method = value;
  }

  public static emptyPromise(): CommandPromise {
    return new CommandPromise((_dto: IDTO): Promise<void> => {
      return Promise.resolve();
    });
  }
}