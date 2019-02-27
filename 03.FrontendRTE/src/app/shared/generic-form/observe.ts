/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { BehaviorSubject } from 'rxjs/BehaviorSubject';

export interface IChangedObject<ObjectType> {
  propertyKey: string | null;
  oldValue: any | null;
  newValue: any | null;
  obj: ObjectType;
}

export function Watch<ObjectType>(obj: any): BehaviorSubject<IChangedObject<ObjectType>> {
  const bs: BehaviorSubject<IChangedObject<ObjectType>> =
          new BehaviorSubject({ obj, oldValue: null, newValue: null, propertyKey: null });

  const propertyKeys = Object.getOwnPropertyNames(obj);

  for (const propertyKey of propertyKeys) {
    ApplyWatchTo(
      obj,
      propertyKey,
      (oldValue: any, newValue: any) => bs.next({ obj, oldValue, newValue, propertyKey }),
    );
  }

  return bs;
}

export function Unwatch(obj: any) {
  const propertyKeys = Object.getOwnPropertyNames(obj);

  for (const propertyKey of propertyKeys) {
    RemoveWatchFrom(obj, propertyKey);
  }
}

export function ApplyWatchTo(obj: any, prop: string, handler: (oldValue: any, newValue: any) => void) {
  let val      = obj[prop];
  const getter = function () {
    return val;
  };

  const setter = function (newVal: any) {
    handler(val, newVal);
    val = newVal;
  };

  if (delete obj[prop]) {
    Object.defineProperty(obj, prop, {
      get: getter,
      set: setter,
    });
  }
}

export function RemoveWatchFrom(obj: any, prop: string) {
  const val = obj[prop];
  delete obj[prop]; // remove accessors
  obj[prop] = val;
}
