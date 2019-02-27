/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Inject, Injector } from '@angular/core';
import { FormArray, FormGroup, ValidationErrors } from '@angular/forms';
import { AbstractControl } from '@angular/forms/src/model';
import { Logger } from '@upe/logger';
import { Subject } from 'rxjs/Subject';
import { CONTROL_NAME, SUB_GROUP } from './config';
import { extractPrototype } from './decorators/group/utils';
import { IAddIf } from './decorators/if';
import { GenericFormControl } from './generic-form-control';
import { IGenericFormGroup } from './generic-form-group.interface';
import { copyByValue } from '@shared/utils/util';

export abstract class GenericFormGroup<T = any, MSGT extends string = string> extends FormGroup implements IGenericFormGroup<T> {

  public get initialized(): boolean {
    return this._initialized;
  }

  public name: string;

  public model: T;

  protected _initialized = false;

  public logger: Logger;

  public validValueChanges: Subject<T> = new Subject<T>();

  public onAddControl: Subject<GenericFormControl<any> | GenericFormGroup<any>> = new Subject();
  public onRemoveControl: Subject<GenericFormControl<any> | GenericFormGroup<any>> = new Subject();

  public constructor(@Inject(Injector) public readonly injector: Injector) {
    super({});
    this.logger = new Logger({ name: this['constructor']['name'] });
    this.valueChanges.skipWhile(() => this.invalid).subscribe(() => this.validValueChanges.next(this.value));
  }

  public init<Self>(this: Self, model?: T, readonly: boolean = false): Self {
    throw new Error('Method not implemented.');
  }

  public beforeValidate(): void {
    throw new Error('Method not implemented.');
  }

  public afterValidate(): void {
    throw new Error('Method not implemented.');
  }

  public validate(): ValidationErrors[] | ValidationErrors | null {
    throw new Error('Method not implemented.');
  }

  public beforeSubmit(): boolean {
    throw new Error('Method not implemented.');
  }

  public submit(): boolean {
    throw new Error('Method not implemented.');
  }

  public afterSubmit(): boolean {
    throw new Error('Method not implemented.');
  }

  public getControlsByNames(controlNames: string[]): GenericFormControl<any>[] {
    throw new Error('Method not implemented.');
  }

  public addIf(addIfObj: IAddIf): void {
    throw new Error('Method not implemented.');
  }

  public removeIf(removeIfObj: IAddIf): void {
    throw new Error('Method not implemented.');
  }

  public addControl(name: string, control: AbstractControl): void {
    super.addControl(name, control);
    this.onAddControl.next(control as any);
    control['isAdded'] = true;
  }

  public createMultiSubGroup(name: MSGT, model?: any): void {
    throw new Error('Method not implemented.');
  }

  public removeMultiSubGroup(name: MSGT, index: number): void {
    throw new Error('Method not implemented.');
  }

  public countMultiSubGroup(name: MSGT): number {
    throw new Error('Method not implemented.');
  }

  public setModel(model: T): void {
    let copy: T = copyByValue(model);
    for (const key of Object.keys(copy)) {
      this.model[key] = copy[key];
    }
  }

  public setValues(model: T): void {

    const prototype = extractPrototype(this);

    const propertyKeys = Object.getOwnPropertyNames(this)
      .filter((propertyKey: string) => Reflect.hasMetadata(CONTROL_NAME, prototype, propertyKey));

    if (model !== undefined && model !== null) {
      for (const propertyKey of
        propertyKeys.filter((pk) => model[Reflect.getMetadata(CONTROL_NAME, prototype, pk)] !== undefined)) {
        this[propertyKey].setModelValue(model[Reflect.getMetadata(CONTROL_NAME, prototype, propertyKey)]);
      }
    }

    const groupKeys = Object.getOwnPropertyNames(this)
      .filter((groupKey: string) => Reflect.hasMetadata(SUB_GROUP, prototype, groupKey));

    if (model !== undefined && model !== null) {
      for (const groupKey of
        groupKeys.filter((gk) => model[Reflect.getMetadata(SUB_GROUP, prototype, gk)] !== undefined)) {
        this[groupKey].setValues(model[Reflect.getMetadata(SUB_GROUP, prototype, groupKey)]);
      }
    }
    console.warn('multi sub groups are not supported by setValues');

  }

  /**
   * @deprecated use setValues
   */
  public setValue(value: {
    [key: string]: any;
  }, options?: {
    onlySelf?: boolean;
    emitEvent?: boolean;
  }): void {
    super.setValue(value, options);
  }

  public removeControl(name: string): void {
    const control = this.controls[name];
    if (control) {
      control['isAdded'] = false;
    } else {
      throw new Error(`This control '${name}' is not added to this group`);
    }
    super.removeControl(name);
    this.onRemoveControl.next(control as any);
  }

  public getModelValue(): any {
    let res = {};

    for (const field in this.controls) {
      const control: GenericFormControl<any> = <GenericFormControl<any>>this.get(field);

      res[field] = control.getModelValue();

    }
    return res;

  }

  public markAsCDirty() {
    this.markAsDirty();

    for (let control in this.controls) {
      if (this.controls[control] instanceof FormArray) {
        (<FormArray>this.controls[control]).controls.forEach((con) => {
          if (con instanceof GenericFormGroup)
            con.markAsCDirty();
          else if (con instanceof GenericFormControl)
            con.markAsDirty();

        });
      } else if (this.controls[control] instanceof GenericFormGroup) {
        (<GenericFormGroup>this.controls[control]).markAsCDirty();
      } else
        this.controls[control].markAsDirty();
    }
  }

  public markAsCTouched() {
    this.markAsTouched();

    for (let control in this.controls) {
      if (this.controls[control] instanceof FormArray) {
        (<FormArray>this.controls[control]).controls.forEach((con) => {
          if (con instanceof GenericFormGroup)
            con.markAsCTouched();
          else if (con instanceof GenericFormControl)
            con.markAsTouched();

        });
      } else if (this.controls[control] instanceof GenericFormGroup) {
        (<GenericFormGroup>this.controls[control]).markAsCTouched();
      } else
        this.controls[control].markAsTouched();
    }
  }
}

