/* (c) https://github.com/MontiCore/monticore */

import { FormControl, ValidatorFn } from '@angular/forms';
import { Logger } from '@upe/logger';
import { Subject } from 'rxjs/Subject';

export class FormularControl<Input = string, Output = Input> extends FormControl {

  public get onValidChange(): Subject<Output | null> {
    return this._onValidChange;
  }

  protected logger: Logger;

  private _onValidChange: Subject<Output> = new Subject<Output>();

  public required: boolean = false;

  public isReadonly = false;
  public isRequired = false;
  public isAdded = false;
  public label: string;
  public placeholder: string;
  public name: string;

  // region valid/invalid
  // TODO : refactor - hacky
  public get valid(): boolean {
    if (this.status === 'DISABLED') {
      return true;
    }
    // TODO : find >>null << assignment
    if (!this.value || this.value === 'null' || this.value === 'null ') {
      return !this.required && (this.touched || this.dirty);
    }
    return this.status === 'VALID';
  }

  public get invalid(): boolean {
    return !this.valid;
  }

  // endregion

  public get empty(): boolean {
    return this.value === null || this.value === '' || this.value === 'null' || this.value === 'null ';
  }

  public constructor(formState: Input | null = null, ...validator: ValidatorFn[]) {
    super(formState);
    // TODO : save validators
    this.setValidators(validator);

    this.logger = new Logger({name: this['constructor']['name']});

    if ((formState || formState as any === 0) && !this.empty) {
      this.markAsDirty();
      this.markAsTouched();
    }

    this.valueChanges.subscribe((value: Input) => this.onValueChanges(value));
  }

  /**
   * Called if the value is changed.
   * And if the {@link handelChange} method or the control is valid
   * or the control value is empty the {@link onValidChange} Subject will
   * be triggered with the return value of {@link handelChange}
   * @param value {Input}
   */
  protected onValueChanges(value: Input) {
    if (!value && this.dirty) {
      this.reset();
    }
    // run handelChange method if valid or empty input value is empty
    const handelValue: Output | null = this.handelChange(value);
    if (handelValue || this.valid || !this.value) {
      this.onValidChange.next(handelValue);
    }
  }

  // region overwrites

  public runValidator(): void {
    this['_runValidator']();
  }

  public setValue(value: Input | null, options?: {
    onlySelf?: boolean;
    emitEvent?: boolean;
    emitModelToViewChange?: boolean;
    emitViewToModelChange?: boolean;
  }): void {
    super.setValue(value, options);
  }

  public patchValue(value: Input | null): void {
    super.patchValue(value);
  }

  // endregion

  /**
   * Called if an value change occurred, and transform the value eventually
   * This method is used to convert the input value to correctly formatted output value
   * @param value {Input | null}
   * @return {Output | null}
   */
  protected handelChange(value: Input | null): Output | null {
    return value as any;
  }

  protected mergeValidators(v1: ValidatorFn | ValidatorFn[], v2: ValidatorFn | ValidatorFn[]): ValidatorFn[] {
    let newValidators: ValidatorFn[] = [];

      let val = v1;

    for (let i = 0; i < 2; i++) {
      if (i === 1)
            val = v2;

          if (val.constructor === Array) {
            for (let j = 0; j < val.length; j++)
                newValidators.push(<ValidatorFn> val[j]);
          } else
              newValidators.push(<ValidatorFn> val);
      }



    return newValidators;

  }

}
