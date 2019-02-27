/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { FormControl, ValidationErrors } from '@angular/forms';
import 'rxjs/add/operator/skipWhile';
import { Subject } from 'rxjs/Subject';
import { ValidationError } from '../validator';
import { ADD_VALIDATOR } from './config';
import { extractPrototype } from './decorators/group/utils';
import { IGenericFormGroup } from './generic-form-group.interface';
import { ValidatorFn } from './validator-fn';

export class GenericFormControl<T, G extends IGenericFormGroup = IGenericFormGroup, R extends IGenericFormGroup = IGenericFormGroup> extends FormControl {
  get placeholder(): string {
    return this._placeholder === undefined ? this.getUIName() : this._placeholder;
  }

  set placeholder(value: string) {
    this._placeholder = value;
  }

  get label(): string {
    return this._label === undefined ? this.getUIName() : this._label;
  }

  set label(value: string) {
    this._label = value;
  }

  public static Empty: '' = '';

  public readonly parent: G;

  public get isDisabled(): boolean {
    return this.disabled;
  }

  // TODO : add tests
  public set isDisabled(value: boolean) {
    if (this.disabled === value) {

    } else if (this.disabled && value === false) {
      this.enable({ onlySelf: true });
    } else {
      this.disable({ onlySelf: true });
    }
  }

  public validationChanges: Subject<void> = new Subject<void>();

  public isReadonly = false;
  public isRequired = false;
  public isAdded    = false;
  private _label: string;
  private _placeholder: string;
  public name: string;
  public propertyKey: string;

  public readonly validValueChanges: Subject<T> = new Subject<T>();
  public readonly validatorFunctions: ValidatorFn[] = [];

  public constructor() {
    super(null);

    this.setValidators(() => {
      let result = null;
      if (null !== this.validatorFunctions) {
        for (const fnc of this.validatorFunctions) {
          try {
            // run validator
            fnc.apply(this);
          } catch (e) {
            if (e instanceof ValidationError) {
              // if failed break and return false
              result = { error: e.msg };
              break;
            }
            throw e;
          }
        }
      }
      return result;
    });
    this.valueChanges.skipWhile(() => this.invalid).subscribe(() => this.validValueChanges.next(this.value));
    if (Reflect.hasMetadata(ADD_VALIDATOR, extractPrototype(this))) {
      this.addValidatorFunction(...Reflect.getMetadata(ADD_VALIDATOR, extractPrototype(this)));
    }
  }

  public validate(): ValidationErrors | null {
    this.markAsTouched({ onlySelf: true });
    this.updateValueAndValidity({
      onlySelf: true,
      emitEvent: false,
    });
    return this.errors;
  }

  public addValidatorFunction(...fn: ValidatorFn[]) {
    this.validatorFunctions.push(...fn);
  }

  public removeValidatorFunction(...fns: ValidatorFn[]) {
    for (const fn of fns) {
      let index = this.validatorFunctions.indexOf(fn);

      // Due to possible multiple validators of one type,
      // we need to remove all of the same type
      while ( index > -1) {
        this.validatorFunctions.splice(index, 1);
        index = this.validatorFunctions.indexOf(fn);
      }
    }
  }

  /**
   * Returns the form value converted for the model
   * @return {any}
   */
  public getModelValue(): any {
    return this.value;
  }

  public setModelValue(modelValue: any) {
    this.setValue(modelValue);
  }

  setValue(value: T, options?: {
    onlySelf?: boolean;
    emitEvent?: boolean;
    emitModelToViewChange?: boolean;
    emitViewToModelChange?: boolean;
  }): void {
    super.setValue(value, options);
  }

  patchValue(value: T, options?: {
    onlySelf?: boolean;
    emitEvent?: boolean;
    emitModelToViewChange?: boolean;
    emitViewToModelChange?: boolean;
  }): void {
    super.setValue(value, options);
  }

  reset(formState: T | null = null, options?: {
    onlySelf?: boolean;
    emitEvent?: boolean;
  }): void {
    super.reset(formState, options);
  }

  private getUIName(): string {
    const firstUpperCase = this.name ? this.name.charAt(0).toUpperCase() + this.name.slice(1) : '';
    return firstUpperCase.split(/(?=[A-Z])/).join(' ');
  }

}

